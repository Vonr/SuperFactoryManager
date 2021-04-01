package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.common.flow.core.Position;
import java.util.List;
import net.minecraft.item.ItemStack;

public abstract class CyclingItemStackFlowButton extends ItemStackFlowButton {

	protected int tick;
	protected int current = 0;

	public CyclingItemStackFlowButton(
		Position pos
	) {
		super(ItemStack.EMPTY, pos);
	}

	public abstract List<ItemStack> getItemStacks();

	public void cycleItemStack() {
		List<ItemStack> items = getItemStacks();
		if (items.size() > 0) {
			current++;
			current%=items.size();
			setItemStack(items.get(current));
		}
	}

	@Override
	public void tick() {
		tick++;
		if (tick%10==0) {
			cycleItemStack();
		}
	}
}
