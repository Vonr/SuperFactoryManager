package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.ClientStuff;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundIfStatementInspectionResultsPacket(
        String results
) implements CustomPacketPayload {
    public static final Type<ServerboundManagerProgramPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            SFM.MOD_ID,
            "clientbound_if_statement_inspection_results_packet"
    ));
    public static final int MAX_RESULTS_LENGTH = 2048;
    public static final StreamCodec<FriendlyByteBuf, ClientboundIfStatementInspectionResultsPacket> STREAM_CODEC = StreamCodec.ofMember(
            ClientboundIfStatementInspectionResultsPacket::encode,
            ClientboundIfStatementInspectionResultsPacket::decode
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void encode(
            ClientboundIfStatementInspectionResultsPacket msg,
            FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeUtf(msg.results(), MAX_RESULTS_LENGTH);
    }

    public static ClientboundIfStatementInspectionResultsPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ClientboundIfStatementInspectionResultsPacket(
                friendlyByteBuf.readUtf(MAX_RESULTS_LENGTH)
        );
    }

    public static void handle(
            ClientboundIfStatementInspectionResultsPacket msg,
            IPayloadContext context
    ) {
        msg.handleInner();

    }

    public void handleInner() {
        ClientStuff.showProgramEditScreen(results);
    }
}
