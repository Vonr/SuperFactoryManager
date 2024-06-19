package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.client.ClientStuff;
import net.minecraft.network.FriendlyByteBuf;

import net.neoforged.neoforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ClientboundBoolExprStatementInspectionResultsPacket(
        String results
) {
    public static final int MAX_RESULTS_LENGTH = 2048;

    public static void encode(
            ClientboundBoolExprStatementInspectionResultsPacket msg, FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeUtf(msg.results(), MAX_RESULTS_LENGTH);
    }

    public static ClientboundBoolExprStatementInspectionResultsPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ClientboundBoolExprStatementInspectionResultsPacket(
                friendlyByteBuf.readUtf(MAX_RESULTS_LENGTH)
        );
    }

    public static void handle(
            ClientboundBoolExprStatementInspectionResultsPacket msg, NetworkEvent.Context context
    ) {
        context.enqueueWork(msg::handle);
        context.setPacketHandled(true);
    }

    public void handle() {
        ClientStuff.showProgramEditScreen(results);
    }
}
