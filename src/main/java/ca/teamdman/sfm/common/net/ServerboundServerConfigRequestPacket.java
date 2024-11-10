package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.config.ConfigExporter;
import ca.teamdman.sfm.common.config.SFMConfig;
import ca.teamdman.sfm.common.registry.SFMPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public record ServerboundServerConfigRequestPacket() implements SFMPacket {
    public static class Daddy implements SFMPacketDaddy<ServerboundServerConfigRequestPacket> {
        @Override
        public void encode(
                ServerboundServerConfigRequestPacket msg,
                FriendlyByteBuf friendlyByteBuf
        ) {

        }

        @Override
        public ServerboundServerConfigRequestPacket decode(FriendlyByteBuf friendlyByteBuf) {
            return new ServerboundServerConfigRequestPacket();
        }

        @Override
        public void handle(
                ServerboundServerConfigRequestPacket msg,
                SFMPacketHandlingContext context
        ) {
            ServerPlayer player = context.sender();
            if (player == null) {
                SFM.LOGGER.error("Received ServerboundServerConfigRequestPacket from null player");
                return;
            }
            String configToml = ConfigExporter.getConfigToml(SFMConfig.COMMON_SPEC);
            SFM.LOGGER.info("Sending config to player: {}", player.getName().getString());
            SFMPackets.sendToPlayer(() -> player, new ClientboundServerConfigResponsePacket(configToml));
        }

        @Override
        public Class<ServerboundServerConfigRequestPacket> getPacketClass() {
            return ServerboundServerConfigRequestPacket.class;
        }
    }
}
