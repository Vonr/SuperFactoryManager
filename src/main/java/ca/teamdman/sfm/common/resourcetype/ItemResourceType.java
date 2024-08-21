package ca.teamdman.sfm.common.resourcetype;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.stream.Stream;

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
    public long getAmount(ItemStack stack) {
        return stack.getCount();
    }

    @Override
    public ItemStack getStackInSlot(
            IItemHandler cap,
            int slot
    ) {
        return cap.getStackInSlot(slot);
    }

    @Override
    public ItemStack extract(
            IItemHandler handler,
            int slot,
            long amount,
            boolean simulate
    ) {
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

    /**
     * We want to also return block tags here.
     * <p>
     * <a href="https://github.com/CoFH/CoFHCore/blob/58b83bd0ef1676783323dce54788c3161faab49d/src/main/java/cofh/core/event/CoreClientEvents.java#L127">CoFH Core adds the "Press Ctrl for Tags" tooltip</a>
     * See: {@link cofh.core.event.CoreClientEvents#handleItemTooltipEvent(ItemTooltipEvent)}
     */
    @Override
    public Stream<ResourceLocation> getTagsForStack(ItemStack itemStack) {
        // Get block tags
        Stream<TagKey<Block>> blockTagKeys;
        if (!itemStack.isEmpty()) {
            Block block = Block.byItem(itemStack.getItem());
            if (block != Blocks.AIR) {
                //noinspection deprecation
                blockTagKeys = block.builtInRegistryHolder().getTagKeys();
            } else {
                blockTagKeys = Stream.empty();
            }
        } else {
            blockTagKeys = Stream.empty();
        }

        // Get item tags
        //noinspection deprecation
        Stream<TagKey<Item>> itemTagKeys = itemStack.getItem().builtInRegistryHolder().tags();

        // Return union
        return Stream.concat(itemTagKeys, blockTagKeys).map(TagKey::location);
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
    public long getMaxStackSizeForSlot(
            IItemHandler handler,
            int slot
    ) {
        return handler.getSlotLimit(slot);
    }

    /**
     * @return remaining stack that was not inserted
     */
    @Override
    public ItemStack insert(
            IItemHandler handler,
            int slot,
            ItemStack stack,
            boolean simulate
    ) {
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

    @Override
    protected ItemStack setCount(
            ItemStack stack,
            long amount
    ) {
        int finalAmount = amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount;
        stack.setCount(finalAmount);
        return stack;
    }

}
