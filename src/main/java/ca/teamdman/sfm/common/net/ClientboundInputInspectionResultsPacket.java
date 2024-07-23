package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.ClientStuff;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundInputInspectionResultsPacket(
        String results
) implements CustomPacketPayload {

    public static final Type<ClientboundInputInspectionResultsPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            SFM.MOD_ID,
            "clientbound_input_inspection_results_packet"
    ));
    public static final int MAX_RESULTS_LENGTH = 20480;
    public static final StreamCodec<FriendlyByteBuf, ClientboundInputInspectionResultsPacket> STREAM_CODEC = StreamCodec.ofMember(
            ClientboundInputInspectionResultsPacket::encode,
            ClientboundInputInspectionResultsPacket::decode
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void encode(
            ClientboundInputInspectionResultsPacket msg,
            FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeUtf(msg.results(), MAX_RESULTS_LENGTH);
    }

    public static ClientboundInputInspectionResultsPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ClientboundInputInspectionResultsPacket(
                friendlyByteBuf.readUtf(MAX_RESULTS_LENGTH)
        );
    }

    public static void handle(
            ClientboundInputInspectionResultsPacket msg,
            IPayloadContext context
    ) {
        ClientStuff.showProgramEditScreen(msg.results);

    }
}

