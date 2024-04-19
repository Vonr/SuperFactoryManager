package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.ClientStuff;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public record ClientboundContainerExportsInspectionResultsPacket(
        int windowId,
        String results
) implements CustomPacketPayload {
    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        encode(this, friendlyByteBuf);
    }
    public static final ResourceLocation ID = new ResourceLocation(SFM.MOD_ID, "clientbound_container_exports_inspection_results_packet");
    @Override
    public ResourceLocation id() {
        return ID;
    }
    public static final int MAX_RESULTS_LENGTH = 20480;

    public static void encode(
            ClientboundContainerExportsInspectionResultsPacket msg, FriendlyByteBuf friendlyByteBuf
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
            ClientboundContainerExportsInspectionResultsPacket msg, PlayPayloadContext context
    ) {
        context.workHandler().submitAsync(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            var container = player.containerMenu;
            if (container.containerId != msg.windowId) return;
            ClientStuff.showProgramEditScreen(msg.results, next -> {
            });
        });
    }
}
