package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.registry.SFMPackets;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;


import java.util.function.Supplier;

public record ServerboundManagerClearLogsPacket(
        int windowId,
        BlockPos pos
) {
    public static void encode(ServerboundManagerClearLogsPacket msg, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(msg.windowId());
        friendlyByteBuf.writeBlockPos(msg.pos());
    }

    public static ServerboundManagerClearLogsPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ServerboundManagerClearLogsPacket(
                friendlyByteBuf.readVarInt(),
                friendlyByteBuf.readBlockPos()
        );
    }

    public static void handle(ServerboundManagerClearLogsPacket msg, NetworkEvent.Context context) {
        SFMPackets.handleServerboundContainerPacket(
                context,
                ManagerContainerMenu.class,
                ManagerBlockEntity.class,
                msg.pos,
                msg.windowId,
                (menu, manager) -> {
                    manager.logger.clear();
                    manager.logger.info(x -> x.accept(Constants.LocalizationKeys.LOGS_GUI_CLEAR_LOGS_BUTTON_PACKET_RECEIVED.get()));
                }
        );
       context.setPacketHandled(true);
    }
}
