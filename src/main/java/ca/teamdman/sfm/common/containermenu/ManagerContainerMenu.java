package ca.teamdman.sfm.common.containermenu;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.item.DiskItem;
import ca.teamdman.sfm.common.registry.SFMMenus;
import ca.teamdman.sfml.ast.Program;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ManagerContainerMenu extends AbstractContainerMenu {
    public final Container CONTAINER;
    public final Inventory INVENTORY;
    public final BlockPos MANAGER_POSITION;
    public String program;
    public ManagerBlockEntity.State state;
    public long[] tickTimeNanos;


    public ManagerContainerMenu(
            int windowId,
            Inventory inv,
            Container container,
            BlockPos blockEntityPos,
            String program,
            ManagerBlockEntity.State state,
            long[] tickTimeNanos
    ) {
        super(SFMMenus.MANAGER_MENU.get(), windowId);
        checkContainerSize(container, 1);
        this.CONTAINER = container;
        this.INVENTORY = inv;
        this.MANAGER_POSITION = blockEntityPos;
        this.program = program;
        this.state = state;
        this.tickTimeNanos = tickTimeNanos;

        this.addSlot(new Slot(container, 0, 15, 47) {
            @Override
            public int getMaxStackSize() {
                return 1;
            }

            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof DiskItem;
            }
        });

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inv, k, 8 + k * 18, 142));
        }
    }

    public ManagerContainerMenu(int windowId, Inventory inventory, FriendlyByteBuf buf) {
        this(
                windowId,
                inventory,
                new SimpleContainer(1),
                buf.readBlockPos(),
                buf.readUtf(Program.MAX_PROGRAM_LENGTH),
                buf.readEnum(ManagerBlockEntity.State.class),
                buf.readLongArray(null, ManagerBlockEntity.TICK_TIME_HISTORY_SIZE)
        );
    }

    public ManagerContainerMenu(int windowId, Inventory inventory, ManagerBlockEntity manager) {
        this(
                windowId,
                inventory,
                manager,
                manager.getBlockPos(),
                manager.getProgramString().orElse(""),
                manager.getState(),
                manager.getTickTimeNanos()
        );
    }

    public static void encode(ManagerBlockEntity manager, FriendlyByteBuf buf) {
        buf.writeBlockPos(manager.getBlockPos());
        buf.writeUtf(manager.getProgramString().orElse(""), Program.MAX_PROGRAM_LENGTH);
        buf.writeEnum(manager.getState());
        buf.writeLongArray(manager.getTickTimeNanos());
    }


    @Override
    public boolean stillValid(Player player) {
        return CONTAINER.stillValid(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        var slot = this.slots.get(slotIndex);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        var containerEnd = CONTAINER.getContainerSize();
        var inventoryEnd = this.slots.size();

        var contents = slot.getItem();
        var result = contents.copy();

        if (slotIndex < containerEnd) {
            // clicked slot in container
            if (!this.moveItemStackTo(contents, containerEnd, inventoryEnd, true)) return ItemStack.EMPTY;
        } else {
            // clicked slot in inventory
            if (!this.moveItemStackTo(contents, 0, containerEnd, false)) return ItemStack.EMPTY;
        }

        if (contents.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        return result;
    }
}
