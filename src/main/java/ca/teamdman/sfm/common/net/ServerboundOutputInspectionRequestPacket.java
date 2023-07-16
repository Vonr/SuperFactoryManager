package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.program.LimitedOutputSlot;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.registry.SFMPackets;
import ca.teamdman.sfml.ast.OutputStatement;
import ca.teamdman.sfml.ast.Program;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
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

    private static <STACK, ITEM, CAP> String getLine(LimitedOutputSlot<STACK, ITEM, CAP> slot) {
//        STACK stack = slot.peekExtractPotential();

//        return slot.type.getCount(stack) + " " + slot.type.getRegistryKey(stack);
        return "output slot";
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
                                ProgramContext context = new ProgramContext(manager);
                                List<LimitedOutputSlot<?, ?, ?>> slotList = new ArrayList<>();
                                outputStatement.gatherSlots(context, slotList::add);

                                StringBuilder payload = new StringBuilder();
                                payload.append(outputStatement).append("\n-- peek results --\n");
                                slotList.forEach(slot -> payload.append(getLine(slot)).append("\n"));

                                SFMPackets.INSPECTION_CHANNEL.send(
                                        PacketDistributor.PLAYER.with(() -> player),
                                        new ClientboundOutputInspectionResultsPacket(payload.toString())
                                );
                            }),
                    failure -> {
                    }
            );
        });
    }
}
