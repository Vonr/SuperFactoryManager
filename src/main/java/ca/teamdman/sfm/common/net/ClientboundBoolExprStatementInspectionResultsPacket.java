package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.ClientStuff;
import net.minecraft.network.FriendlyByteBuf;


import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundBoolExprStatementInspectionResultsPacket(
        String results
) implements CustomPacketPayload {
    public static final Type<ServerboundManagerProgramPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            SFM.MOD_ID,
            "clientbound_bool_expr_statement_inspection_results_packet"
    ));
    public static final int MAX_RESULTS_LENGTH = 2048;

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void encode(
            ClientboundBoolExprStatementInspectionResultsPacket msg,
            FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeUtf(msg.results(), MAX_RESULTS_LENGTH);
    }

    public static ClientboundBoolExprStatementInspectionResultsPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ClientboundBoolExprStatementInspectionResultsPacket(
                friendlyByteBuf.readUtf(MAX_RESULTS_LENGTH)
        );
    }

    public static void handle(
            ClientboundBoolExprStatementInspectionResultsPacket msg,
            IPayloadContext context
    ) {
        msg.handleInner();

    }

    public void handleInner() {
        ClientStuff.showProgramEditScreen(results);
    }
}
