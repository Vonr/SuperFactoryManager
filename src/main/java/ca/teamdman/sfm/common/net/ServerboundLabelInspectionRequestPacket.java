package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfml.ast.Program;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashSet;

public record ServerboundLabelInspectionRequestPacket(
        String label
) implements CustomPacketPayload {

    public static final Type<ServerboundLabelInspectionRequestPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            SFM.MOD_ID,
            "serverbound_label_inspection_request_packet"
    ));
    public static final StreamCodec<FriendlyByteBuf, ServerboundLabelInspectionRequestPacket> STREAM_CODEC = StreamCodec.ofMember(
            ServerboundLabelInspectionRequestPacket::encode,
            ServerboundLabelInspectionRequestPacket::decode
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void encode(
            ServerboundLabelInspectionRequestPacket msg,
            FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeUtf(msg.label(), Program.MAX_LABEL_LENGTH);
    }

    public static ServerboundLabelInspectionRequestPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ServerboundLabelInspectionRequestPacket(
                friendlyByteBuf.readUtf(Program.MAX_LABEL_LENGTH)
        );
    }

    public static void handle(
            ServerboundLabelInspectionRequestPacket msg,
            IPayloadContext context
    ) {
        // we don't know if the player has the program edit screen open from a manager or a disk in hand
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }
        SFM.LOGGER.info("Received label inspection request packet from player {}", player.getStringUUID());
        LabelPositionHolder labelPositionHolder;
        if (player.containerMenu instanceof ManagerContainerMenu mcm) {
            SFM.LOGGER.info("Player is using a manager container menu - will append additional info to payload");
            labelPositionHolder = LabelPositionHolder.from(mcm.CONTAINER.getItem(0));
        } else {
            if (player.getMainHandItem().is(SFMItems.DISK_ITEM.get())) {
                labelPositionHolder = LabelPositionHolder.from(player.getMainHandItem());
            } else if (player.getOffhandItem().is(SFMItems.DISK_ITEM.get())) {
                labelPositionHolder = LabelPositionHolder.from(player.getOffhandItem());
            } else {
                labelPositionHolder = null;
            }
        }
        if (labelPositionHolder == null) {
            SFM.LOGGER.info("Label holder wasn't found - aborting");
            return;
        }
        SFM.LOGGER.info("building payload");
        StringBuilder payload = new StringBuilder();
        payload.append("-- Positions for label \"").append(msg.label()).append("\" --\n");
        payload.append(labelPositionHolder.getPositions(msg.label()).size()).append(" assignments\n");
        payload.append("-- Summary --\n");
        labelPositionHolder.labels()
                .getOrDefault(msg.label(), new HashSet<>()).forEach(pos -> {
            payload
                    .append(pos.getX())
                    .append(",")
                    .append(pos.getY())
                    .append(",")
                    .append(pos.getZ());
            if (player.level().isLoaded(pos)) {
                payload
                        .append(" -- ")
                        .append(player.level().getBlockState(pos).getBlock().getName().getString());
            } else {
                payload
                        .append(" -- chunk not loaded");
            }
            payload
                    .append("\n");
        });

        payload.append("\n\n\n-- Detailed --\n");
        for (BlockPos pos : (labelPositionHolder.labels()).getOrDefault(msg.label(), new HashSet<>())) {
            if (payload.length() > 20_000) {
                payload.append("... (truncated)");
                break;
            }
            payload
                    .append(pos.getX())
                    .append(",")
                    .append(pos.getY())
                    .append(",")
                    .append(pos.getZ());
            if (player.level().isLoaded(pos)) {
                payload
                        .append(" -- ")
                        .append(player.level().getBlockState(pos).getBlock().getName().getString());

                payload.append("\n").append(ServerboundContainerExportsInspectionRequestPacket
                                                    .buildInspectionResults(player.level(), pos)
                                                    .indent(1));
            } else {
                payload
                        .append(" -- chunk not loaded");
            }
            payload
                    .append("\n");
        }
        SFM.LOGGER.info(
                "Sending payload response length={} to player {}",
                payload.length(),
                player.getStringUUID()
        );
        PacketDistributor.sendToPlayer(
                player,
                new ClientboundLabelInspectionResultsPacket(
                        payload.toString()
                )
        );

    }
}

