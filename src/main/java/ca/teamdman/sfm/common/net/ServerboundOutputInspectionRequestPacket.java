package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.program.LimitedInputSlot;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.registry.SFMPackets;
import ca.teamdman.sfm.common.registry.SFMResourceTypes;
import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfm.common.util.SFMUtil;
import ca.teamdman.sfml.ast.Number;
import ca.teamdman.sfml.ast.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.antlr.v4.runtime.misc.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public record ServerboundOutputInspectionRequestPacket(
        String programString,
        int outputNodeIndex
) {
    public static void encode(ServerboundOutputInspectionRequestPacket msg, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUtf(msg.programString, Program.MAX_PROGRAM_LENGTH);
        friendlyByteBuf.writeInt(msg.outputNodeIndex());
    }

    public static ServerboundOutputInspectionRequestPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ServerboundOutputInspectionRequestPacket(
                friendlyByteBuf.readUtf(Program.MAX_PROGRAM_LENGTH),
                friendlyByteBuf.readInt()
        );
    }

    private static <STACK, ITEM, CAP> ResourceLimit<STACK, ITEM, CAP> getSlotResource(LimitedInputSlot<STACK, ITEM, CAP> limitedInputSlot) {
        ResourceType<STACK, ITEM, CAP> resourceType = limitedInputSlot.type;
        //noinspection OptionalGetWithoutIsPresent
        ResourceKey<ResourceType<STACK, ITEM, CAP>> resourceTypeResourceKey = SFMResourceTypes.DEFERRED_TYPES
                .get()
                .getResourceKey(limitedInputSlot.type)
                .map(x -> {
                    //noinspection unchecked,rawtypes
                    return (ResourceKey<ResourceType<STACK, ITEM, CAP>>) (ResourceKey) x;
                })
                .get();
        STACK stack = limitedInputSlot.peekExtractPotential();
        ResourceLocation stackId = resourceType.getRegistryKey(stack);

        Limit amountLimit = new Limit(
                new ResourceQuantity(
                        new Number(limitedInputSlot.type.getAmount(stack)),
                        ResourceQuantity.IdExpansionBehaviour.NO_EXPAND
                ),
                ResourceQuantity.MAX_QUANTITY
        );
        ResourceIdentifier<STACK, ITEM, CAP> resourceIdentifier = new ResourceIdentifier<>(
                resourceTypeResourceKey.location().getNamespace(),
                resourceTypeResourceKey.location().getPath(),
                stackId.getNamespace(),
                stackId.getPath()
        );
        return new ResourceLimit<>(
                resourceIdentifier,
                amountLimit
        );
    }


    public static void handle(
            ServerboundOutputInspectionRequestPacket msg,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        contextSupplier.get().enqueueWork(() -> {
            // we don't know if the player has the program edit screen open from a manager or a disk in hand
            ServerPlayer player = contextSupplier.get().getSender();
            if (player == null) return;
            ManagerBlockEntity manager;
            if (player.containerMenu instanceof ManagerContainerMenu mcm) {
                if (player.getLevel().getBlockEntity(mcm.MANAGER_POSITION) instanceof ManagerBlockEntity mbe) {
                    manager = mbe;
                } else {
                    return;
                }
            } else {
                //todo: localize
                SFMPackets.INSPECTION_CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new ClientboundInputInspectionResultsPacket(
                                "This inspection is only available when editing inside a manager.")
                );
                return;
            }
            Program.compile(
                    msg.programString,
                    (successProgram, builder) -> builder
                            .getNodeAtIndex(msg.outputNodeIndex)
                            .filter(OutputStatement.class::isInstance)
                            .map(OutputStatement.class::cast)
                            .ifPresent(outputStatement -> {
                                StringBuilder payload = new StringBuilder();
                                payload.append(outputStatement.toStringPretty()).append("\n-- control flows --\n\n");

                                successProgram.replaceOutputStatement(outputStatement, new OutputStatement(
                                        outputStatement.labelAccess(),
                                        outputStatement.resourceLimits(),
                                        outputStatement.each()
                                ) {
                                    @Override
                                    public void tick(ProgramContext context) {
                                        context.getExecutionPath().forEach(branch -> {
                                            if (branch.wasTrue()) {
                                                payload.append(branch.ifStatement().condition().sourceCode());
                                            } else {
                                                payload.append(branch.ifStatement().condition().negate().sourceCode());
                                            }
                                            payload.append("\n");

                                            StringBuilder branchPayload = new StringBuilder();
                                            branchPayload.append("-- predicted inputs:\n");
                                            List<Pair<LimitedInputSlot<?, ?, ?>, LabelAccess>> inputSlots = new ArrayList<>();
                                            context
                                                    .getInputs()
                                                    .forEach(inputStatement -> inputStatement.gatherSlots(
                                                            context,
                                                            slot -> inputSlots.add(new Pair<>(
                                                                    slot,
                                                                    inputStatement.labelAccess()
                                                            ))
                                                    ));
                                            inputSlots.stream()
                                                    .map(slot -> SFMUtil.getInputStatementForSlot(slot.a, slot.b))
                                                    .filter(Optional::isPresent)
                                                    .map(Optional::get)
                                                    .map(InputStatement::toStringCondensed)
                                                    .map(x -> x + "\n")
                                                    .forEach(branchPayload::append);

                                            branchPayload.append("-- total predicted outputs before limits:\n");
                                            ResourceLimits condensedResourceLimits;
                                            {
                                                ResourceLimits resourceLimits = new ResourceLimits(
                                                        inputSlots
                                                                .stream()
                                                                .map(slot -> slot.a)
                                                                .map(ServerboundOutputInspectionRequestPacket::getSlotResource)
                                                                .toList(),
                                                        ResourceIdSet.EMPTY
                                                );
                                                List<ResourceLimit<?, ?, ?>> condensedResourceLimitList = new ArrayList<>();
                                                for (ResourceLimit<?, ?, ?> resourceLimit : resourceLimits.resourceLimits()) {
                                                    // check if an existing resource limit has the same resource identifier
                                                    condensedResourceLimitList
                                                            .stream()
                                                            .filter(x -> x
                                                                    .resourceId()
                                                                    .equals(resourceLimit.resourceId()))
                                                            .findFirst()
                                                            .ifPresentOrElse(found -> {
                                                                int i = condensedResourceLimitList.indexOf(found);
                                                                ResourceLimit<?, ?, ?> newLimit = found.withLimit(new Limit(
                                                                        found
                                                                                .limit()
                                                                                .quantity()
                                                                                .add(resourceLimit.limit().quantity()),
                                                                        ResourceQuantity.MAX_QUANTITY
                                                                ));
                                                                condensedResourceLimitList.set(i, newLimit);
                                                            }, () -> condensedResourceLimitList.add(resourceLimit));
                                                }
                                                condensedResourceLimits = new ResourceLimits(
                                                        condensedResourceLimitList,
                                                        ResourceIdSet.EMPTY
                                                );
                                            }
                                            branchPayload
                                                    .append(new OutputStatement(
                                                            outputStatement.labelAccess(),
                                                            condensedResourceLimits,
                                                            outputStatement.each()
                                                    ).toStringPretty())
                                                    .append("\n\n");
                                            payload.append(branchPayload.toString().indent(1));
                                        });
                                    }
                                });

                                // run the program down each possible if-branch combination
                                for (
                                        int branchIndex = 0;
                                        branchIndex < Math.pow(2, successProgram.getConditionCount());
                                        branchIndex++
                                ) {
                                    successProgram.tick(new ProgramContext(
                                            successProgram,
                                            manager,
                                            ProgramContext.ExecutionPolicy.EXPLORE_BRANCHES,
                                            branchIndex
                                    ));
                                }
                                SFMPackets.INSPECTION_CHANNEL.send(
                                        PacketDistributor.PLAYER.with(() -> player),
                                        new ClientboundOutputInspectionResultsPacket(payload.toString().strip())
                                );
                            }),
                    failure -> {
                        //todo: translate
                        SFMPackets.INSPECTION_CHANNEL.send(
                                PacketDistributor.PLAYER.with(() -> player),
                                new ClientboundOutputInspectionResultsPacket("failed to compile program")
                        );
                    }
            );
        });
    }
}
