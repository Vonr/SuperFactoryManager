package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.tilepositionmatcher;

import ca.teamdman.sfm.client.gui.flow.impl.util.ItemStackFlowComponent;
import ca.teamdman.sfm.common.flow.core.Position;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;

public class PickerActivator extends ItemStackFlowComponent {

	private final TilePositionMatcherFlowComponent PARENT;

	public PickerActivator(
		TilePositionMatcherFlowComponent parent,
		Position pos
	) {
		super(ItemStack.EMPTY, pos);
		PARENT = parent;
		setItemStack(new ItemStack(Blocks.DIAMOND_BLOCK));
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		PARENT.PICKER.toggleVisibilityAndEnabled();
	}
}
