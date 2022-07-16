package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.menu.ManagerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class ServerboundManagerFixPacket extends MenuPacket {

    public ServerboundManagerFixPacket(int windowId, BlockPos pos) {
        super(windowId, pos);
    }

    public static class ResetPacketHandler extends MenuPacketHandler<ManagerMenu, ManagerBlockEntity, ServerboundManagerFixPacket> {
        public ResetPacketHandler() {
            super(ManagerMenu.class, ManagerBlockEntity.class);
        }

        @Override
        public void encode(
                ServerboundManagerFixPacket msg, FriendlyByteBuf buf
        ) {
        }

        @Override
        public ServerboundManagerFixPacket decode(int containerId, BlockPos pos, FriendlyByteBuf buf) {
            return new ServerboundManagerFixPacket(containerId, pos);
        }

        @Override
        public void handle(
                ServerboundManagerFixPacket msg,
                ManagerMenu menu,
                ManagerBlockEntity manager
        ) {
            manager.getDisk().ifPresent(disk -> manager.getCompiledProgram().getProgram().fixWarnings(disk, manager));
        }
    }
}
