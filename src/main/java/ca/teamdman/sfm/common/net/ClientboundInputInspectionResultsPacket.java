package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.client.ClientStuff;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ClientboundInputInspectionResultsPacket(
        String results
) {
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
            ClientboundInputInspectionResultsPacket msg, NetworkEvent.Context context
    ) {
        context.enqueueWork(() -> ClientStuff.showProgramEditScreen(msg.results));
        context.setPacketHandled(true);
    }
}
