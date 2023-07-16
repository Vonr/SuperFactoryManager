package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.program.LimitedInputSlot;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.registry.SFMPackets;
import ca.teamdman.sfm.common.registry.SFMResourceTypes;
import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfm.common.util.SFMUtil;
import ca.teamdman.sfml.ast.InputStatement;
import ca.teamdman.sfml.ast.LabelAccess;
import ca.teamdman.sfml.ast.Program;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Optional;
import java.util.function.Supplier;

public record ServerboundInputInspectionRequestPacket(
        String programString,
        int inputNodeIndex
) {
    public static void encode(ServerboundInputInspectionRequestPacket msg, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeUtf(msg.programString, Program.MAX_PROGRAM_LENGTH);
        friendlyByteBuf.writeInt(msg.inputNodeIndex());
    }

    public static ServerboundInputInspectionRequestPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ServerboundInputInspectionRequestPacket(
                friendlyByteBuf.readUtf(Program.MAX_PROGRAM_LENGTH),
                friendlyByteBuf.readInt()
        );
    }

    private static <STACK, ITEM, CAP> Optional<InputStatement> getLine(
            LimitedInputSlot<STACK, ITEM, CAP> slot,
            LabelAccess labelAccess
    ) {
        return SFMResourceTypes.DEFERRED_TYPES
                .get()
                .getResourceKey(slot.type)
                .map(x -> {
                    //noinspection unchecked,rawtypes
                    return (ResourceKey<ResourceType<STACK, ITEM, CAP>>) (ResourceKey) x;
                })
                .map((ResourceKey<ResourceType<STACK, ITEM, CAP>> resourceTypeResourceKey) -> SFMUtil.getInputStatementForStack(
                        resourceTypeResourceKey,
                        slot.type,
                        slot.peekExtractPotential(),
                        "temp",
                        slot.slot,
                        false,
                        null
                ))
                // update the labels
                .map(inputStatement -> new InputStatement(new LabelAccess(
                        labelAccess.labels(),
                        labelAccess.directions(),
                        inputStatement.labelAccess()
                                .slots()
                ), inputStatement.resourceLimits(), inputStatement.each()));
    }

    public static void handle(
            ServerboundInputInspectionRequestPacket msg,
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
                            .getNodeAtIndex(msg.inputNodeIndex)
                            .filter(InputStatement.class::isInstance)
                            .map(InputStatement.class::cast)
                            .ifPresent(inputStatement -> {
                                StringBuilder payload = new StringBuilder();
                                payload.append(inputStatement).append("\n-- peek results --\n");

                                ProgramContext context = new ProgramContext(manager);
                                inputStatement.gatherSlots(
                                        context,
                                        slot -> getLine(slot, inputStatement.labelAccess()).ifPresent(line -> payload
                                                .append(line)
                                                .append("\n"))
                                );

                                SFMPackets.INSPECTION_CHANNEL.send(
                                        PacketDistributor.PLAYER.with(() -> player),
                                        new ClientboundInputInspectionResultsPacket(payload.toString())
                                );
                            }),
                    failure -> {
                    }
            );
        });
    }
}
