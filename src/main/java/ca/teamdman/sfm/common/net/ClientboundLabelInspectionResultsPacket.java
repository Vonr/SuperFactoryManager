package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.ClientStuff;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundLabelInspectionResultsPacket(
        String results
) implements CustomPacketPayload {

    public static final Type<ServerboundManagerProgramPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            SFM.MOD_ID,
            "clientbound_label_inspection_results_packet"
    ));
    public static final int MAX_RESULTS_LENGTH = 50_000;
    public static final StreamCodec<FriendlyByteBuf, ClientboundLabelInspectionResultsPacket> STREAM_CODEC = StreamCodec.ofMember(
            ClientboundLabelInspectionResultsPacket::encode,
            ClientboundLabelInspectionResultsPacket::decode
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void encode(
            ClientboundLabelInspectionResultsPacket msg,
            FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeUtf(msg.results(), MAX_RESULTS_LENGTH);
    }

    public static ClientboundLabelInspectionResultsPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ClientboundLabelInspectionResultsPacket(
                friendlyByteBuf.readUtf(MAX_RESULTS_LENGTH)
        );
    }

    public static void handle(
            ClientboundLabelInspectionResultsPacket msg,
            IPayloadContext context
    ) {
        ClientStuff.showProgramEditScreen(msg.results);

    }
}
