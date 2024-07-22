package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.ClientStuff;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;


import java.util.function.Supplier;

public record ClientboundOutputInspectionResultsPacket(
        String results
) implements CustomPacketPayload {

    public static final Type<ServerboundManagerProgramPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            SFM.MOD_ID,
            "clientbound_output_inspection_results_packet"
    ));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final int MAX_RESULTS_LENGTH = 10240;

    public static void encode(
            ClientboundOutputInspectionResultsPacket msg,
            FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeUtf(msg.results(), MAX_RESULTS_LENGTH);
    }

    public static ClientboundOutputInspectionResultsPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ClientboundOutputInspectionResultsPacket(
                friendlyByteBuf.readUtf(MAX_RESULTS_LENGTH)
        );
    }

    public static void handle(
            ClientboundOutputInspectionResultsPacket msg,
            IPayloadContext context
    ) {
        ClientStuff.showProgramEditScreen(msg.results);

    }
}
