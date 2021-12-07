package ca.teamdman.sfm.common.net;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class MenuPacket {
    public final BlockPos POSITION;
    public final int      CONTAINER_ID;

    public MenuPacket(int containerId, BlockPos pos) {
        this.POSITION     = pos;
        this.CONTAINER_ID = containerId;
    }

    public static abstract class MenuPacketHandler<MENU extends AbstractContainerMenu, BLOCK_ENTITY extends BaseContainerBlockEntity, MSG extends MenuPacket> {
        private final Class<MENU>         MENU_CLASS;
        private final Class<BLOCK_ENTITY> BLOCK_ENTITY_CLASS;

        protected MenuPacketHandler(Class<MENU> menuClass, Class<BLOCK_ENTITY> blockEntityClass) {
            MENU_CLASS         = menuClass;
            BLOCK_ENTITY_CLASS = blockEntityClass;
        }


        public final void _encode(MSG msg, FriendlyByteBuf buf) {
            buf.writeInt(msg.CONTAINER_ID);
            buf.writeBlockPos(msg.POSITION);
            encode(msg, buf);
        }

        public abstract void encode(MSG msg, FriendlyByteBuf buf);

        public final MSG _decode(FriendlyByteBuf buf) {
            var containerId = buf.readInt();
            var pos         = buf.readBlockPos();
            return decode(containerId, pos, buf);
        }

        public abstract MSG decode(int containerId, BlockPos pos, FriendlyByteBuf buf);

        public abstract void handle(
                MSG msg,
                MENU menu,
                BLOCK_ENTITY blockEntity
        );

        public final void _handle(MSG msg, Supplier<NetworkEvent.Context> ctxSupplier) {
            if (ctxSupplier == null) return;

            var ctx = ctxSupplier.get();
            if (ctx == null) return;

            var sender = ctx.getSender();
            if (sender == null) return;

            var menu = sender.containerMenu;
            if (!MENU_CLASS.isInstance(menu)) return;
            if (menu.containerId != msg.CONTAINER_ID) return;

            var level = sender.getLevel();
            if (level == null) return;
            if (!level.isLoaded(msg.POSITION)) return;

            var blockEntity = level.getBlockEntity(msg.POSITION);
            if (!BLOCK_ENTITY_CLASS.isInstance(blockEntity)) return;

            //noinspection unchecked
            handle(msg, (MENU) menu, (BLOCK_ENTITY) blockEntity);
        }
    }
}
