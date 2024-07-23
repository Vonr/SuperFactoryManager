package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.ClientStuff;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ClientboundContainerExportsInspectionResultsPacket(
        int windowId,
        String results
) implements CustomPacketPayload {

    public static final Type<ClientboundContainerExportsInspectionResultsPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            SFM.MOD_ID,
            "clientbound_container_exports_inspection_results_packet"
    ));
    public static final int MAX_RESULTS_LENGTH = 20480;
    public static final StreamCodec<FriendlyByteBuf, ClientboundContainerExportsInspectionResultsPacket> STREAM_CODEC = StreamCodec.ofMember(
            ClientboundContainerExportsInspectionResultsPacket::encode,
            ClientboundContainerExportsInspectionResultsPacket::decode
    );
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void encode(
            ClientboundContainerExportsInspectionResultsPacket msg,
            FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeVarInt(msg.windowId());
        friendlyByteBuf.writeUtf(msg.results(), MAX_RESULTS_LENGTH);
    }

    public static ClientboundContainerExportsInspectionResultsPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ClientboundContainerExportsInspectionResultsPacket(
                friendlyByteBuf.readVarInt(),
                friendlyByteBuf.readUtf(MAX_RESULTS_LENGTH)
        );
    }

    public static void handle(
            ClientboundContainerExportsInspectionResultsPacket msg,
            IPayloadContext context
    ) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        var container = player.containerMenu;
        if (container.containerId != msg.windowId) return;
        ClientStuff.showProgramEditScreen(msg.results);
    }
}

