package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.tiletypematcher;

import ca.teamdman.sfm.client.gui.flow.impl.util.CyclingItemStackFlowButton;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.flow.core.Position;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;

class Icon extends CyclingItemStackFlowButton {
	private final TileTypeMatcherFlowComponent PARENT;

	public Icon(
		TileTypeMatcherFlowComponent parent,
		Position pos
	) {
		super(pos);
		PARENT = parent;
		cycleItemStack();
	}

	@Override
	public List<ItemStack> getItemStacks() {
		ArrayList<ItemStack> rtn = new ArrayList<>();
		CableNetworkManager
			.getOrRegisterNetwork(PARENT.PARENT.SCREEN.getContainer().getSource())
			.map(PARENT.getData()::getPreview)
			.ifPresent(rtn::addAll);
		return rtn;
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		PARENT.PICKER.toggleVisibilityAndEnabled();
	}
}
