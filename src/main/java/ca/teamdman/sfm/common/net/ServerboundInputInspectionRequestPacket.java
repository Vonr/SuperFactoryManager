package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.program.SimulateExploreAllPathsProgramBehaviour;
import ca.teamdman.sfm.common.util.SFMUtils;
import ca.teamdman.sfml.ast.InputStatement;
import ca.teamdman.sfml.ast.Program;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundInputInspectionRequestPacket(
        String programString,
        int inputNodeIndex
) implements CustomPacketPayload {

    public static final Type<ServerboundInputInspectionRequestPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            SFM.MOD_ID,
            "serverbound_input_inspection_request_packet"
    ));
    public static final StreamCodec<FriendlyByteBuf, ServerboundInputInspectionRequestPacket> STREAM_CODEC = StreamCodec.ofMember(
            ServerboundInputInspectionRequestPacket::encode,
            ServerboundInputInspectionRequestPacket::decode
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void encode(
            ServerboundInputInspectionRequestPacket msg,
            FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeUtf(msg.programString, Program.MAX_PROGRAM_LENGTH);
        friendlyByteBuf.writeInt(msg.inputNodeIndex());
    }

    public static ServerboundInputInspectionRequestPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ServerboundInputInspectionRequestPacket(
                friendlyByteBuf.readUtf(Program.MAX_PROGRAM_LENGTH),
                friendlyByteBuf.readInt()
        );
    }

    public static void handle(
            ServerboundInputInspectionRequestPacket msg,
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
            PacketDistributor.sendToPlayer(
                    player,
                    new ClientboundInputInspectionResultsPacket(
                            "This inspection is only available when editing inside a manager.")
            );
            return;
        }
        Program.compile(
                msg.programString,
                successProgram -> successProgram.builder()
                        .getNodeAtIndex(msg.inputNodeIndex)
                        .filter(InputStatement.class::isInstance)
                        .map(InputStatement.class::cast)
                        .ifPresent(inputStatement -> {
                            StringBuilder payload = new StringBuilder();
                            payload
                                    .append(inputStatement.toStringPretty())
                                    .append("\n-- peek results --\n");

                            ProgramContext programContext = new ProgramContext(
                                    successProgram,
                                    manager,
                                    new SimulateExploreAllPathsProgramBehaviour()
                            );
                            int preLen = payload.length();
                            inputStatement.gatherSlots(
                                    programContext,
                                    slot -> SFMUtils
                                            .getInputStatementForSlot(
                                                    slot,
                                                    inputStatement.labelAccess()
                                            )
                                            .ifPresent(is -> payload
                                                    .append(is.toStringPretty())
                                                    .append("\n"))
                            );
                            if (payload.length() == preLen) {
                                payload.append("none");
                            }

                            PacketDistributor.sendToPlayer(
                                    player,

                                    new ClientboundInputInspectionResultsPacket(
                                            SFMUtils.truncate(
                                                    payload.toString(),
                                                    ClientboundInputInspectionResultsPacket.MAX_RESULTS_LENGTH
                                            ))
                            );
                        }),
                failure -> {
                }
        );

    }
}

