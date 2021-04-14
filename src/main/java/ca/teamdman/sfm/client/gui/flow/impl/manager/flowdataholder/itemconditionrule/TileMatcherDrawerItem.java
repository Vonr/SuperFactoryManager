package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemconditionrule;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.util.CyclingItemStackFlowButton;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.core.TileMatcher;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextProperties;
import org.lwjgl.glfw.GLFW;

class TileMatcherDrawerItem<T extends FlowComponent & FlowDataHolder<? extends TileMatcher>> extends
	CyclingItemStackFlowButton {

	public final T DELEGATE;
	private final CableNetwork NETWORK;
	private TilesSection PARENT;

	public TileMatcherDrawerItem(TilesSection parent, T comp, CableNetwork network) {
		super(new Position());
		this.DELEGATE = comp;
		this.PARENT = parent;
		this.NETWORK = network;

		DELEGATE.setPosition(parent.getPosition()
			.withConstantOffset(parent.DRAWER.getPosition())
			.withConstantOffset(parent.PARENT.getPosition())
			.withConstantOffset(getPosition())
			.withConstantOffset(getSize().getWidth() + 5, 0));

		cycleItemStack();
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
			super.onClicked(mx, my, button);
		} else if (button == GLFW.GLFW_MOUSE_BUTTON_2) {
			// delete the matcher data
			// the item rules will detect the delete and prune accordingly
			PARENT.PARENT.CONTROLLER.SCREEN.sendFlowDataDeleteToServer(DELEGATE.getData().getId());
		}
	}

	@Override
	public List<? extends ITextProperties> getTooltip() {
		return DELEGATE.getData().getTooltip(super.getTooltip());
	}

	@Override
	public boolean isSelected() {
		return DELEGATE.getData().isVisible();
	}

	@Override
	public void setSelected(boolean value) {
		//todo: only one open at once
		if (value != DELEGATE.getData().isVisible()) {
			DELEGATE.getData().setVisibility(value);
			PARENT.PARENT.CONTROLLER.SCREEN.sendFlowDataToServer(DELEGATE.getData());
		}
	}

	@Override
	public boolean isTooltipEnabled(int mx, int my) {
		return !isSelected() && super.isTooltipEnabled(mx, my);
	}

	@Override
	public List<ItemStack> getItemStacks() {
		return DELEGATE.getData().getPreview(NETWORK);
	}
}
