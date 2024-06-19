package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;


import java.util.function.Supplier;

public record ClientboundManagerLogLevelUpdatedPacket(
        int windowId,
        String logLevel
) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(SFM.MOD_ID, "clientbound_manager_log_level_updated_packet");

    @Override
    public ResourceLocation id() {
        return ID;
    }
    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        encode(this, friendlyByteBuf);
    }

    public static void encode(
            ClientboundManagerLogLevelUpdatedPacket msg, FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeVarInt(msg.windowId());
        friendlyByteBuf.writeUtf(msg.logLevel(), ServerboundManagerSetLogLevelPacket.MAX_LOG_LEVEL_NAME_LENGTH);
    }

    public static ClientboundManagerLogLevelUpdatedPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ClientboundManagerLogLevelUpdatedPacket(
                friendlyByteBuf.readVarInt(),
                friendlyByteBuf.readUtf(ServerboundManagerSetLogLevelPacket.MAX_LOG_LEVEL_NAME_LENGTH)
        );
    }

    public static void handle(
            ClientboundManagerLogLevelUpdatedPacket msg, PlayPayloadContext context
    ) {
        context.workHandler().submitAsync(msg::handleInner);
        
    }

    public void handleInner() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null
            || !(player.containerMenu instanceof ManagerContainerMenu menu)
            || menu.containerId != this.windowId()) {
            SFM.LOGGER.error("Invalid log level packet received, ignoring.");
            return;
        }
        menu.logLevel = this.logLevel;
    }
}
