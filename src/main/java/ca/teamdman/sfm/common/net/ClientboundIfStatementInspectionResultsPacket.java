package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.client.ClientStuff;
import net.minecraft.network.FriendlyByteBuf;

import net.neoforged.neoforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ClientboundIfStatementInspectionResultsPacket(
        String results
) {
    public static final int MAX_RESULTS_LENGTH = 2048;

    public static void encode(
            ClientboundIfStatementInspectionResultsPacket msg, FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeUtf(msg.results(), MAX_RESULTS_LENGTH);
    }

    public static ClientboundIfStatementInspectionResultsPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ClientboundIfStatementInspectionResultsPacket(
                friendlyByteBuf.readUtf(MAX_RESULTS_LENGTH)
        );
    }

    public static void handle(
            ClientboundIfStatementInspectionResultsPacket msg, NetworkEvent.Context context
    ) {
        context.enqueueWork(msg::handle);
        context.setPacketHandled(true);
    }

    public void handle() {
        ClientStuff.showProgramEditScreen(results);
    }
}
