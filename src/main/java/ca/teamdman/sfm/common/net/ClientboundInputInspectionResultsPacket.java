package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.ClientStuff;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record ClientboundInputInspectionResultsPacket(
        String results
) implements CustomPacketPayload {
    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        encode(this, friendlyByteBuf);
    }

    public static final ResourceLocation ID = new ResourceLocation(SFM.MOD_ID, "clientbound_input_inspection_results_packet");
    @Override
    public ResourceLocation id() {
        return ID;
    }
    public static final int MAX_RESULTS_LENGTH = 20480;

    public static void encode(
            ClientboundInputInspectionResultsPacket msg, FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeUtf(msg.results(), MAX_RESULTS_LENGTH);
    }

    public static ClientboundInputInspectionResultsPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ClientboundInputInspectionResultsPacket(
                friendlyByteBuf.readUtf(MAX_RESULTS_LENGTH)
        );
    }

    public static void handle(
            ClientboundInputInspectionResultsPacket msg, PlayPayloadContext context
    ) {
        context.workHandler().submitAsync(() -> ClientStuff.showProgramEditScreen(msg.results));

    }
}
