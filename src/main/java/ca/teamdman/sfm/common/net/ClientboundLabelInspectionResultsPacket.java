package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.client.ClientStuff;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ClientboundLabelInspectionResultsPacket(
        String results
) {
    public static final int MAX_RESULTS_LENGTH = 10240;

    public static void encode(
            ClientboundLabelInspectionResultsPacket msg, FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeUtf(msg.results(), MAX_RESULTS_LENGTH);
    }

    public static ClientboundLabelInspectionResultsPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ClientboundLabelInspectionResultsPacket(
                friendlyByteBuf.readUtf(MAX_RESULTS_LENGTH)
        );
    }

    public static void handle(
            ClientboundLabelInspectionResultsPacket msg, Supplier<NetworkEvent.Context> contextSupplier
    ) {
        contextSupplier.get().enqueueWork(() -> ClientStuff.showProgramEditScreen(msg.results, next -> {
        }));
        contextSupplier.get().setPacketHandled(true);
    }
}
