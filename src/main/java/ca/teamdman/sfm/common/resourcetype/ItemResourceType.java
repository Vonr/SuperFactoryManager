package ca.teamdman.sfm.common.resourcetype;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class ItemResourceType extends ResourceType<ItemStack, Item, IItemHandler> {
    public ItemResourceType() {
        super(ForgeCapabilities.ITEM_HANDLER);
    }

    @Override
    public IForgeRegistry<Item> getRegistry() {
        return ForgeRegistries.ITEMS;
    }


    @Override
    public Item getItem(ItemStack itemStack) {
        return itemStack.getItem();
    }

    @Override
    public ItemStack copy(ItemStack stack) {
        return stack.copy();
    }

    @Override
    protected ItemStack setCount(ItemStack stack, long amount) {
        int finalAmount = amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount;
        stack.setCount(finalAmount);
        return stack;
    }

    @Override
    public long getAmount(ItemStack stack) {
        return stack.getCount();
    }

    @Override
    public ItemStack getStackInSlot(IItemHandler cap, int slot) {
        return cap.getStackInSlot(slot);
    }

    @Override
    public ItemStack extract(IItemHandler handler, int slot, long amount, boolean simulate) {
        int finalAmount = amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount;
        // Mekanism bin intentionally only returns 64x stacks without going past the max stack size
        // https://github.com/mekanism/Mekanism/blob/f92b48a49e0766cd3aa78e95c9c4a47ba90402f5/src/main/java/mekanism/common/inventory/slot/BasicInventorySlot.java#L174-L175
        return handler.extractItem(slot, finalAmount, simulate);
    }

    @Override
    public boolean matchesStackType(Object o) {
        return o instanceof ItemStack;
    }

    @Override
    public boolean matchesCapabilityType(Object o) {
        return o instanceof IItemHandler;
    }

    @Override
    public int getSlots(IItemHandler handler) {
        return handler.getSlots();
    }

    @Override
    public long getMaxStackSize(ItemStack itemStack) {
        return itemStack.getMaxStackSize();
    }

    @Override
    public long getMaxStackSize(IItemHandler handler, int slot) {
        return handler.getSlotLimit(slot);
    }

    /**
     * @return remaining stack that was not inserted
     */
    @Override
    public ItemStack insert(IItemHandler handler, int slot, ItemStack stack, boolean simulate) {
        return handler.insertItem(slot, stack, simulate);
    }

    @Override
    public boolean isEmpty(ItemStack stack) {
        return stack.isEmpty();
    }

    @Override
    public ItemStack getEmptyStack() {
        return ItemStack.EMPTY;
    }

}
