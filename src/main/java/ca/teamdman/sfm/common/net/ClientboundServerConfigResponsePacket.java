package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.client.ClientStuff;
import net.minecraft.network.FriendlyByteBuf;

public record ClientboundServerConfigResponsePacket(
        String configToml
) implements SFMPacket {
    public static final int MAX_LENGTH = 20480;

    public static class Daddy implements SFMPacketDaddy<ClientboundServerConfigResponsePacket> {
        @Override
        public void encode(
                ClientboundServerConfigResponsePacket msg,
                FriendlyByteBuf friendlyByteBuf
        ) {
            friendlyByteBuf.writeUtf(msg.configToml(), MAX_LENGTH);
        }

        @Override
        public ClientboundServerConfigResponsePacket decode(FriendlyByteBuf friendlyByteBuf) {
            return new ClientboundServerConfigResponsePacket(
                    friendlyByteBuf.readUtf(MAX_LENGTH)
            );
        }

        @Override
        public void handle(
                ClientboundServerConfigResponsePacket msg,
                SFMPacketHandlingContext context
        ) {
            ClientStuff.showProgramEditScreen(msg.configToml());
        }

        @Override
        public Class<ClientboundServerConfigResponsePacket> getPacketClass() {
            return ClientboundServerConfigResponsePacket.class;
        }
    }

}
