package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.config.ConfigExporter;
import ca.teamdman.sfm.common.config.SFMConfig;
import ca.teamdman.sfm.common.registry.SFMPackets;
import net.minecraft.commands.Commands;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public record ServerboundServerConfigRequestPacket(
        boolean requestingEditMode
) implements SFMPacket {
    public static class Daddy implements SFMPacketDaddy<ServerboundServerConfigRequestPacket> {
        @Override
        public void encode(
                ServerboundServerConfigRequestPacket msg,
                FriendlyByteBuf friendlyByteBuf
        ) {
            friendlyByteBuf.writeBoolean(msg.requestingEditMode);
        }

        @Override
        public ServerboundServerConfigRequestPacket decode(FriendlyByteBuf friendlyByteBuf) {
            return new ServerboundServerConfigRequestPacket(friendlyByteBuf.readBoolean());
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
            if (!player.hasPermissions(Commands.LEVEL_OWNERS) && msg.requestingEditMode) {
                SFM.LOGGER.warn(
                        "Player {} tried to request server config for editing but does not have the necessary permissions, this should never happen o-o",
                        player.getName().getString()
                );
                return;
            }
            String configToml = ConfigExporter.getConfigToml(SFMConfig.COMMON_SPEC);
            configToml = configToml.replaceAll("(?m)^#", "-- ");
            configToml = configToml.replaceAll("\r", "");
            SFM.LOGGER.info("Sending config to player: {}", player.getName().getString());
            SFMPackets.sendToPlayer(
                    () -> player,
                    new ClientboundServerConfigResponsePacket(configToml, msg.requestingEditMode())
            );
        }

        @Override
        public Class<ServerboundServerConfigRequestPacket> getPacketClass() {
            return ServerboundServerConfigRequestPacket.class;
        }
    }
}
