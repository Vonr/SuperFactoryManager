package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.menu.ManagerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class ServerboundManagerResetPacket extends MenuPacket {

    public ServerboundManagerResetPacket(int windowId, BlockPos pos) {
        super(windowId, pos);
    }

    public static class ResetPacketHandler extends MenuPacketHandler<ManagerMenu, ManagerBlockEntity, ServerboundManagerResetPacket> {
        public ResetPacketHandler() {
            super(ManagerMenu.class, ManagerBlockEntity.class);
        }

        @Override
        public void encode(
                ServerboundManagerResetPacket msg, FriendlyByteBuf buf
        ) {
        }

        @Override
        public ServerboundManagerResetPacket decode(int containerId, BlockPos pos, FriendlyByteBuf buf) {
            return new ServerboundManagerResetPacket(containerId, pos);
        }

        @Override
        public void handle(
                ServerboundManagerResetPacket msg,
                ManagerMenu menu,
                ManagerBlockEntity blockEntity
        ) {
            blockEntity.reset();
        }
    }
}
