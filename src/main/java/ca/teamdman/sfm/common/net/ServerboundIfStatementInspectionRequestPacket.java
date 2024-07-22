package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.program.SimulateExploreAllPathsProgramBehaviour;
import ca.teamdman.sfm.common.registry.SFMPackets;
import ca.teamdman.sfm.common.util.SFMUtils;
import ca.teamdman.sfml.ast.IfStatement;
import ca.teamdman.sfml.ast.Program;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;


import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.function.Supplier;

public record ServerboundIfStatementInspectionRequestPacket(
        String programString,
        int inputNodeIndex
) implements CustomPacketPayload {
    public static final Type<ServerboundManagerProgramPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            SFM.MOD_ID,
            "serverbound_if_statement_inspection_request_packet"
    ));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void encode(
            ServerboundIfStatementInspectionRequestPacket msg,
            FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeUtf(msg.programString, Program.MAX_PROGRAM_LENGTH);
        friendlyByteBuf.writeInt(msg.inputNodeIndex());
    }

    public static ServerboundIfStatementInspectionRequestPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ServerboundIfStatementInspectionRequestPacket(
                friendlyByteBuf.readUtf(Program.MAX_PROGRAM_LENGTH),
                friendlyByteBuf.readInt()
        );
    }

    public static void handle(
            ServerboundIfStatementInspectionRequestPacket msg,
            IPayloadContext context
    ) {
        // todo: duplicate code
        // we don't know if the player has the program edit screen open from a manager or a disk in hand
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }
        ManagerBlockEntity manager;
        if (player.containerMenu instanceof ManagerContainerMenu mcm) {
            if (player.level().getBlockEntity(mcm.MANAGER_POSITION) instanceof ManagerBlockEntity mbe) {
                manager = mbe;
            } else {
                return;
            }
        } else {
            //todo: localize
            PacketDistributor.sendToPlayer(player,
                    new ClientboundInputInspectionResultsPacket(
                            "This inspection is only available when editing inside a manager.")
            );
            return;
        }
        Program.compile(
                msg.programString,
                successProgram -> successProgram.builder()
                        .getNodeAtIndex(msg.inputNodeIndex)
                        .filter(IfStatement.class::isInstance)
                        .map(IfStatement.class::cast)
                        .ifPresent(ifStatement -> {
                            StringBuilder payload = new StringBuilder();
                            payload
                                    .append(ifStatement.toStringShort())
                                    .append("\n-- peek results --\n");
                            ProgramContext programContext = new ProgramContext(
                                    successProgram,
                                    manager,
                                    new SimulateExploreAllPathsProgramBehaviour()
                            );
                            boolean result = ifStatement.condition().test(programContext);
                            payload.append(result ? "TRUE" : "FALSE");

                            PacketDistributor.sendToPlayer(player,
                                    new ClientboundIfStatementInspectionResultsPacket(
                                            SFMUtils.truncate(
                                                    payload.toString(),
                                                    ClientboundIfStatementInspectionResultsPacket.MAX_RESULTS_LENGTH
                                            ))
                            );
                        }),
                failure -> {
                }
        );

    }
}
