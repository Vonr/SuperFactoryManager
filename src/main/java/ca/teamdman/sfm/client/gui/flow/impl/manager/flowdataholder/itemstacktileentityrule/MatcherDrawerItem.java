package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstacktileentityrule;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.ItemStackFlowComponent;
import ca.teamdman.sfm.common.config.Config.Client;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.ItemStackMatcher;
import ca.teamdman.sfm.common.flow.core.Position;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;

class MatcherDrawerItem<T extends FlowComponent & FlowDataHolder<? extends ItemStackMatcher>> extends
	FlowContainer {

	private final T DELEGATE;
	private final DisplayIcon ICON;
	private final ItemStackTileEntityRuleFlowComponent PARENT;
	private boolean open = false;

	public MatcherDrawerItem(
		ItemStackTileEntityRuleFlowComponent parent,
		T delegate
	) {
		super(new Position(), ItemStackFlowComponent.DEFAULT_SIZE);
		this.PARENT = parent;
		this.ICON = new DisplayIcon(delegate.getData().getPreview().get(0), new Position());
		this.DELEGATE = delegate;
		DELEGATE.setPosition(parent.getPosition()
				.withConstantOffset(parent.MATCHER_DRAWER.getPosition())
				.withConstantOffset(getPosition())
				.withConstantOffset(getSize().getWidth() + 5, 0));
		addChild(ICON);
	}

	private class DisplayIcon extends ItemStackFlowComponent {

		public DisplayIcon(ItemStack stack, Position pos) {
			super(stack, pos);
		}

		@Override
		public List<? extends ITextProperties> getTooltip() {
			List<ITextProperties> rtn = (List<ITextProperties>) super.getTooltip();
			rtn.set(
				0,
				new StringTextComponent(DELEGATE.getData().getQuantity() + "x ")
					.append(((IFormattableTextComponent) rtn.get(0)))
			);
			return rtn;
		}

		@Override
		public boolean isTooltipEnabled(int mx, int my) {
			return !open && super.isTooltipEnabled(mx, my);
		}

		@Override
		public void onSelectionChanged() {
			if (!Client.allowMultipleRuleWindows && isSelected()) {
				PARENT.CONTROLLER.getChildren().stream()
					.filter(c -> c instanceof FlowDataHolder)
					.filter(c -> ((FlowDataHolder<?>) c).getData() instanceof ItemStackMatcher)
					.forEach(c -> {
						c.setVisible(false);
						c.setEnabled(false);
					});
				PARENT.MATCHER_DRAWER.getChildren().stream()
					.filter(c -> c instanceof MatcherDrawerItem && c != MatcherDrawerItem.this)
					.forEach(c -> ((MatcherDrawerItem<?>) c).ICON.setSelected(false));
			}
			DELEGATE.setVisible(isSelected());
			DELEGATE.setEnabled(isSelected());
		}

		@Override
		public void toggleSelected() {
			open = !open;
			setSelected(open);
		}
	}
}
