package ca.teamdman.sfm.common.blockentity;

import ca.teamdman.sfm.common.item.DiskItem;
import ca.teamdman.sfm.common.menu.ManagerMenu;
import ca.teamdman.sfm.common.registry.SFMBlockEntities;
import ca.teamdman.sfm.common.util.SFMContainerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ManagerBlockEntity extends BaseContainerBlockEntity {
    private final Container INVENTORY = new SimpleContainer(1);

    public ManagerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(SFMBlockEntities.MANAGER_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ManagerBlockEntity tile) {
        level.setBlock(pos.below(), Blocks.DIAMOND_BLOCK.defaultBlockState(), 1 | 2);
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.sfm.manager");
    }

    @Override
    protected AbstractContainerMenu createMenu(int windowId, Inventory inv) {
        return new ManagerMenu(windowId, inv, this);
    }

    @Override
    public int getContainerSize() {
        return INVENTORY.getContainerSize();
    }

    @Override
    public boolean isEmpty() {
        return INVENTORY.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        return INVENTORY.getItem(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return INVENTORY.removeItem(slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return INVENTORY.removeItemNoUpdate(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        INVENTORY.setItem(slot, stack);
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return stack.getItem() instanceof DiskItem;
    }

    @Override
    public boolean stillValid(Player player) {
        return SFMContainerUtil.stillValid(this, player);
    }

    @Override
    public void clearContent() {
        INVENTORY.clearContent();
    }

}
