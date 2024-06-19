package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.registry.SFMPackets;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;


import java.util.function.Supplier;

public record ServerboundManagerLogDesireUpdatePacket(
        int windowId,
        BlockPos pos,
        boolean isLogScreenOpen
) {
    public static void encode(ServerboundManagerLogDesireUpdatePacket msg, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(msg.windowId());
        friendlyByteBuf.writeBlockPos(msg.pos());
        friendlyByteBuf.writeBoolean(msg.isLogScreenOpen());
    }

    public static ServerboundManagerLogDesireUpdatePacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ServerboundManagerLogDesireUpdatePacket(
                friendlyByteBuf.readVarInt(),
                friendlyByteBuf.readBlockPos(),
                friendlyByteBuf.readBoolean()
        );
    }

    public static void handle(
            ServerboundManagerLogDesireUpdatePacket msg,
            NetworkEvent.Context ctx
    ) {
        SFMPackets.handleServerboundContainerPacket(
                ctx,
                ManagerContainerMenu.class,
                ManagerBlockEntity.class,
                msg.pos,
                msg.windowId,
                (menu, manager) -> {
                    menu.isLogScreenOpen = msg.isLogScreenOpen();
                    manager.sendUpdatePacket();
                }
        );
    }
}
