package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ClientboundManagerLogLevelUpdatedPacket(
        int windowId,
        String logLevel
) {

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
            ClientboundManagerLogLevelUpdatedPacket msg, Supplier<NetworkEvent.Context> contextSupplier
    ) {
        contextSupplier.get().enqueueWork(msg::handle);
        contextSupplier.get().setPacketHandled(true);
    }

    public void handle() {
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
