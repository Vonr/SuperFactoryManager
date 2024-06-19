package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.client.ClientStuff;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ClientboundContainerExportsInspectionResultsPacket(
        int windowId,
        String results
) {
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
            ClientboundContainerExportsInspectionResultsPacket msg, Supplier<NetworkEvent.Context> contextSupplier
    ) {
        contextSupplier.get().enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            var container = player.containerMenu;
            if (container.containerId != msg.windowId) return;
            ClientStuff.showProgramEditScreen(msg.results);
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
