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
import org.lwjgl.glfw.GLFW;

class MatcherDrawerItem<T extends FlowComponent & FlowDataHolder<? extends ItemStackMatcher>> extends
	FlowContainer {

	private final T DELEGATE;
	private final DisplayIcon ICON;
	private final ItemsSection PARENT;

	public MatcherDrawerItem(
		ItemsSection parent,
		T delegate
	) {
		super(new Position(), ItemStackFlowComponent.DEFAULT_SIZE);
		// When we scroll out of view, hide the delegate
		setPosition(new Position() {
			//Specifically override the method the drawer uses to update position
			@Override
			public void setXY(int x, int y) {
				super.setXY(x, y);
				if (
					ICON.isSelected()
						&& !PARENT.DRAWER.isChildVisible(MatcherDrawerItem.this)
				) {
					ICON.setSelected(false);
					ICON.onSelectionChanged();
				}
			}
		});
		this.PARENT = parent;
		this.ICON = new DisplayIcon(delegate.getData().getPreview().get(0), new Position());
		this.DELEGATE = delegate;
		DELEGATE.setPosition(parent.getPosition()
			.withConstantOffset(parent.DRAWER.getPosition())
			.withConstantOffset(getPosition())
			.withConstantOffset(getSize().getWidth() + 5, 0));
		addChild(ICON);
	}


	private class DisplayIcon extends ItemStackFlowComponent {

		public DisplayIcon(ItemStack stack, Position pos) {
			super(stack, pos);
		}

		@Override
		public boolean isInBounds(int mx, int my) {
			// Undo mouse coordinate localization before checking if in the bounds of parent
			// We want to make sure that objects scrolled out of view are not capturing mouse actions
			return PARENT.DRAWER.isInBounds(
				mx + (
					MatcherDrawerItem.this.getPosition().getX()
						+ PARENT.DRAWER.getPosition().getX()
				),
				my + (
					MatcherDrawerItem.this.getPosition().getY()
						+ PARENT.DRAWER.getPosition().getY()
				)
			) && super.isInBounds(mx, my);
		}

		@Override
		public boolean isTooltipEnabled(int mx, int my) {
			return !ICON.isSelected() && super.isTooltipEnabled(mx, my);
		}

		@Override
		public void onClicked(int mx, int my, int button) {
			if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
				super.onClicked(mx, my, button);
			} else if (button == GLFW.GLFW_MOUSE_BUTTON_2) {
				PARENT.PARENT.CONTROLLER.SCREEN
					.sendFlowDataDeleteToServer(DELEGATE.getData().getId());
			}
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
		public void onSelectionChanged() {
			if (!Client.allowMultipleRuleWindows && isSelected()) {
				PARENT.PARENT.CONTROLLER.getChildren().stream()
					.filter(c -> c instanceof FlowDataHolder)
					.filter(c -> ((FlowDataHolder<?>) c).getData() instanceof ItemStackMatcher)
					.forEach(c -> {
						c.setVisible(false);
						c.setEnabled(false);
					});
				PARENT.DRAWER.getChildren().stream()
					.filter(c -> c instanceof MatcherDrawerItem && c != MatcherDrawerItem.this)
					.forEach(c -> ((MatcherDrawerItem<?>) c).ICON.setSelected(false));
			}
			DELEGATE.setVisible(isSelected());
			DELEGATE.setEnabled(isSelected());
		}
	}
}
