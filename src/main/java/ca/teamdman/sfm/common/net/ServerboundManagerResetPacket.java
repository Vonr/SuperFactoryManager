package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class ServerboundManagerResetPacket extends MenuPacket {

    public ServerboundManagerResetPacket(int windowId, BlockPos pos) {
        super(windowId, pos);
    }

    public static class PacketHandler extends MenuPacketHandler<ManagerContainerMenu, ManagerBlockEntity, ServerboundManagerResetPacket> {
        public PacketHandler() {
            super(ManagerContainerMenu.class, ManagerBlockEntity.class);
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
                ManagerContainerMenu menu,
                ManagerBlockEntity blockEntity
        ) {
            blockEntity.reset();
        }
    }
}
