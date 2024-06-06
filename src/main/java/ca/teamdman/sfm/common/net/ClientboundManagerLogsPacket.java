package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.logging.TranslatableLogEvent;
import ca.teamdman.sfm.common.logging.TranslatableLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.Collection;
import java.util.function.Supplier;

public record ClientboundManagerLogsPacket(
        int windowId,
        Collection<TranslatableLogEvent> logs
) {

    public static void encode(
            ClientboundManagerLogsPacket msg, FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeVarInt(msg.windowId());
        TranslatableLogger.encodeAndDrain(msg.logs(), friendlyByteBuf);
    }

    public static ClientboundManagerLogsPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ClientboundManagerLogsPacket(
                friendlyByteBuf.readVarInt(),
                TranslatableLogger.decode(friendlyByteBuf)
        );
    }

    public static void handle(
            ClientboundManagerLogsPacket msg, Supplier<NetworkEvent.Context> contextSupplier
    ) {
        contextSupplier.get().enqueueWork(msg::handle);
        contextSupplier.get().setPacketHandled(true);
    }

    public void handle() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null
            || !(player.containerMenu instanceof ManagerContainerMenu menu)
            || menu.containerId != this.windowId()) {
            SFM.LOGGER.error("Invalid logs packet received, ignoring.");
            return;
        }

        menu.logs.addAll(logs());
//        SFM.LOGGER.debug(Constants.LocalizationKeys.LOGS_RECEIVED.getString(logs().size()));
    }
}
