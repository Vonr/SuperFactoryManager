package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import net.minecraft.commands.Commands;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public record ServerboundConfigUpdatePacket(
        String newConfig
) implements SFMPacket {
    public static final int MAX_CONFIG_LENGTH = 1024;
    public static class Daddy implements SFMPacketDaddy<ServerboundConfigUpdatePacket> {
        @Override
        public void encode(
                ServerboundConfigUpdatePacket msg,
                FriendlyByteBuf friendlyByteBuf
        ) {
            friendlyByteBuf.writeUtf(msg.newConfig, MAX_CONFIG_LENGTH);
        }

        @Override
        public ServerboundConfigUpdatePacket decode(FriendlyByteBuf friendlyByteBuf) {
            return new ServerboundConfigUpdatePacket(friendlyByteBuf.readUtf(MAX_CONFIG_LENGTH));
        }

        @Override
        public void handle(
                ServerboundConfigUpdatePacket msg,
                SFMPacketHandlingContext context
        ) {
            ServerPlayer player = context.sender();
            if (player == null) {
                SFM.LOGGER.error("Received ServerboundServerConfigRequestPacket from null player");
                return;
            }
            if (!player.hasPermissions(Commands.LEVEL_OWNERS)) {
                SFM.LOGGER.fatal(
                        "Player {} tried to WRITE server config but does not have the necessary permissions, this should never happen o-o",
                        player.getName().getString()
                );
                return;
            }
//            String configToml = ConfigExporter.getConfigToml(SFMConfig.COMMON_SPEC);
//            configToml = configToml.replaceAll("(?m)^", "-- ");
//            configToml = configToml.replaceAll("\r", "");
//            SFM.LOGGER.info("Sending config to player: {}", player.getName().getString());
//            SFMPackets.sendToPlayer(() -> player, new ClientboundServerConfigResponsePacket(configToml));
        }

        @Override
        public Class<ServerboundConfigUpdatePacket> getPacketClass() {
            return ServerboundConfigUpdatePacket.class;
        }
    }
}
