package ca.teamdman.sfm.common.blockentity;

import ca.teamdman.sfm.common.item.DiskItem;
import ca.teamdman.sfm.common.menu.ManagerMenu;
import ca.teamdman.sfm.common.parser.SFMParser;
import ca.teamdman.sfm.common.registry.SFMBlockEntities;
import ca.teamdman.sfm.common.util.SFMContainerUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ManagerBlockEntity extends BaseContainerBlockEntity {
    public static final int                    STATE_DATA_ACCESS_KEY = 0;
    private final       NonNullList<ItemStack> ITEMS                 = NonNullList.withSize(1, ItemStack.EMPTY);
    private final       Object                 compiledProgram       = null;
    private             String                 program               = "";
    private final       ContainerData          DATA_ACCESS           = new ContainerData() {
        @Override
        public int get(int key) {
            return switch (key) {
                case STATE_DATA_ACCESS_KEY -> ManagerBlockEntity.this
                        .getState()
                        .ordinal();
                default -> 0;
            };
        }

        @Override
        public void set(int key, int val) {
        }

        @Override
        public int getCount() {
            return 1;
        }
    };

    public ManagerBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(SFMBlockEntities.MANAGER_BLOCK_ENTITY.get(), blockPos, blockState);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ManagerBlockEntity tile) {
        level.setBlock(pos.below(), Blocks.DIAMOND_BLOCK.defaultBlockState(), 1 | 2);
    }

    public State getState() {
        if (getItem(0).isEmpty()) return State.NO_DISK;
        if (program == null || program
                .trim()
                .isEmpty()) return State.NO_PROGRAM;
        if (compiledProgram == null) return State.INVALID_PROGRAM;
        return State.RUNNING;
    }

    public void setProgram(String program) {
        this.program = program;
        compileProgram();
    }

    private void compileProgram() {
        System.out.println("Compiling " + program.length());
        var parser = new SFMParser(program);
        //        var parser = new TomlParser();
        //        var x = parser.parse(program);
        //        System.out.println(x);
    }

    @Override
    protected Component getDefaultName() {
        return new TranslatableComponent("container.sfm.manager");
    }

    @Override
    protected AbstractContainerMenu createMenu(int windowId, Inventory inv) {
        return new ManagerMenu(windowId, inv, this, getBlockPos(), this.DATA_ACCESS);
    }

    @Override
    public int getContainerSize() {
        return ITEMS.size();
    }

    @Override
    public boolean isEmpty() {
        return ITEMS.isEmpty();
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot < 0 || slot >= ITEMS.size()) return ItemStack.EMPTY;
        return ITEMS.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(ITEMS, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return ContainerHelper.takeItem(ITEMS, slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot < 0 || slot >= ITEMS.size()) return;
        ITEMS.set(slot, stack);
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
    public void load(CompoundTag tag) {
        super.load(tag);
        ContainerHelper.loadAllItems(tag, ITEMS);
        tag.putString("program", program);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        super.save(tag);
        ContainerHelper.saveAllItems(tag, ITEMS);
        program = tag.getString("program");
        return tag;
    }

    @Override
    public void clearContent() {
        ITEMS.clear();
    }

    public enum State {
        NO_PROGRAM(ChatFormatting.RED),
        NO_DISK(ChatFormatting.RED),
        RUNNING(ChatFormatting.GREEN),
        INVALID_PROGRAM(ChatFormatting.DARK_RED);

        public final ChatFormatting COLOR;

        State(ChatFormatting color) {
            COLOR = color;
        }
    }

}
