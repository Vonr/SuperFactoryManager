package ca.teamdman.sfm.common.slot;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SlotMemoryItemHandler extends SlotItemHandler {

	ItemStack ghost = ItemStack.EMPTY;

	public SlotMemoryItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public void putStack(@Nonnull ItemStack stack) {
		ghost = stack.copy();
		super.putStack(stack);
	}
}
