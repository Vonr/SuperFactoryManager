package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.program.SimulateExploreAllPathsProgramBehaviour;
import ca.teamdman.sfm.common.registry.SFMPackets;
import ca.teamdman.sfm.common.util.SFMUtils;
import ca.teamdman.sfml.ast.BoolExpr;
import ca.teamdman.sfml.ast.Program;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public record ServerboundBoolExprStatementInspectionRequestPacket(
        String programString,
        int inputNodeIndex
) {
    public static void encode(
            ServerboundBoolExprStatementInspectionRequestPacket msg,
            FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeUtf(msg.programString, Program.MAX_PROGRAM_LENGTH);
        friendlyByteBuf.writeInt(msg.inputNodeIndex());
    }

    public static ServerboundBoolExprStatementInspectionRequestPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ServerboundBoolExprStatementInspectionRequestPacket(
                friendlyByteBuf.readUtf(Program.MAX_PROGRAM_LENGTH),
                friendlyByteBuf.readInt()
        );
    }

    public static void handle(
            ServerboundBoolExprStatementInspectionRequestPacket msg,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        contextSupplier.get().enqueueWork(() -> {
            // todo: duplicate code
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
                    successProgram -> successProgram.builder()
                            .getNodeAtIndex(msg.inputNodeIndex)
                            .filter(BoolExpr.class::isInstance)
                            .map(BoolExpr.class::cast)
                            .ifPresent(expr -> {
                                StringBuilder payload = new StringBuilder();
                                payload
                                        .append(expr.toStringPretty())
                                        .append("\n-- peek results --\n");
                                ProgramContext programContext = new ProgramContext(
                                        successProgram,
                                        manager,
                                        new SimulateExploreAllPathsProgramBehaviour()
                                );
                                boolean result = expr.test(programContext);
                                payload.append(result ? "TRUE" : "FALSE");

                                SFMPackets.INSPECTION_CHANNEL.send(
                                        PacketDistributor.PLAYER.with(() -> player),
                                        new ClientboundBoolExprStatementInspectionResultsPacket(
                                                SFMUtils.truncate(
                                                        payload.toString(),
                                                        ClientboundBoolExprStatementInspectionResultsPacket.MAX_RESULTS_LENGTH
                                                ))
                                );
                            }),
                    failure -> {
                    }
            );
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
