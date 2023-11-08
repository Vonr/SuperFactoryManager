package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.client.ClientStuff;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ClientboundOutputInspectionResultsPacket(
        String results
) {
    public static final int MAX_RESULTS_LENGTH = 10240;

    public static void encode(
            ClientboundOutputInspectionResultsPacket msg, FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeUtf(msg.results(), MAX_RESULTS_LENGTH);
    }

    public static ClientboundOutputInspectionResultsPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ClientboundOutputInspectionResultsPacket(
                friendlyByteBuf.readUtf(MAX_RESULTS_LENGTH)
        );
    }

    public static void handle(
            ClientboundOutputInspectionResultsPacket msg, Supplier<NetworkEvent.Context> contextSupplier
    ) {
        contextSupplier.get().enqueueWork(() -> ClientStuff.showProgramEditScreen(msg.results, next -> {
        }));
        contextSupplier.get().setPacketHandled(true);
    }
}
