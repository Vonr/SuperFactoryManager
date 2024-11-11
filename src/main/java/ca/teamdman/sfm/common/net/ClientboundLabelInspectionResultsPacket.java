package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.client.ClientStuff;
import net.minecraft.network.FriendlyByteBuf;

public record ClientboundLabelInspectionResultsPacket(
        String results
) implements SFMPacket {
    public static final int MAX_RESULTS_LENGTH = 50_000;

    public static class Daddy implements SFMPacketDaddy<ClientboundLabelInspectionResultsPacket> {
        @Override
        public void encode(
                ClientboundLabelInspectionResultsPacket msg,
                FriendlyByteBuf friendlyByteBuf
        ) {
            friendlyByteBuf.writeUtf(msg.results(), MAX_RESULTS_LENGTH);
        }

        @Override
        public ClientboundLabelInspectionResultsPacket decode(FriendlyByteBuf friendlyByteBuf) {
            return new ClientboundLabelInspectionResultsPacket(
                    friendlyByteBuf.readUtf(MAX_RESULTS_LENGTH)
            );
        }

        @Override
        public void handle(
                ClientboundLabelInspectionResultsPacket msg,
                SFMPacketHandlingContext context
        ) {
            ClientStuff.showProgramEditScreen(msg.results());
        }

        @Override
        public Class<ClientboundLabelInspectionResultsPacket> getPacketClass() {
            return ClientboundLabelInspectionResultsPacket.class;
        }
    }

}
