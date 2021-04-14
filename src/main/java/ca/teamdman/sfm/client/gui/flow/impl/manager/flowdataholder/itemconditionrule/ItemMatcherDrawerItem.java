package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemconditionrule;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.ItemStackFlowButton;
import ca.teamdman.sfm.common.config.Config.Client;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.ItemMatcher;
import ca.teamdman.sfm.common.flow.core.Position;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.glfw.GLFW;

class ItemMatcherDrawerItem<T extends FlowComponent & FlowDataHolder<? extends ItemMatcher>> extends
	FlowContainer {

	private final T DELEGATE;
	private final DisplayIcon ICON;
	private final ItemsSection PARENT;

	public ItemMatcherDrawerItem(
		ItemsSection parent,
		T delegate
	) {
		super(new Position(), ItemStackFlowButton.DEFAULT_SIZE);
		this.PARENT = parent;
		this.DELEGATE = delegate;

		// When we scroll out of view, hide the delegate
		setPosition(new Position() {
			//Specifically override the method the drawer uses to update position
			@Override
			public void setXY(int x, int y) {
				super.setXY(x, y);
				if (
					ICON.isSelected()
						&& !PARENT.DRAWER.isChildVisible(
						ItemMatcherDrawerItem.this)
				) {
					ICON.setSelected(false);
					ICON.onSelectionChanged();
				}
			}
		});

		this.ICON = new DisplayIcon();
		DELEGATE.setPosition(parent.getPosition()
			.withConstantOffset(parent.DRAWER.getPosition())
			.withConstantOffset(parent.PARENT.getPosition())
			.withConstantOffset(getPosition())
			.withConstantOffset(getSize().getWidth() + 5, 0));
		ICON.setSelected(DELEGATE.isVisible());
		addChild(ICON);
	}


	private class DisplayIcon extends ItemStackFlowButton {
		private int tick = 0;
		public DisplayIcon() {
			super(ItemStack.EMPTY, new Position());
			setNextIcon();
		}

		@Override
		public boolean isInBounds(int mx, int my) {
			// Undo mouse coordinate localization before checking if in the bounds of parent
			// We want to make sure that objects scrolled out of view are not capturing mouse actions
			return PARENT.DRAWER.isInBounds(
				mx + (
					ItemMatcherDrawerItem.this.getPosition().getX()
						+ PARENT.DRAWER.getPosition().getX()
				),
				my + (
					ItemMatcherDrawerItem.this.getPosition().getY()
						+ PARENT.DRAWER.getPosition().getY()
				)
			) && super.isInBounds(mx, my);
		}

		@Override
		public boolean isTooltipEnabled(int mx, int my) {
			return !ICON.isSelected() && super.isTooltipEnabled(mx, my);
		}

		@Override
		public void tick() {
			tick++;
			if (tick%10==0) {
				setNextIcon();
			}
		}

		private void setNextIcon() {
			List<ItemStack> opt = DELEGATE.getData().getPreview();
			if (opt.size() == 0) return;
			setItemStack(opt.get((tick/10)%opt.size()));
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
			List<ITextProperties> rtn = new ArrayList<>(super.getTooltip());
			rtn.set(0,
				new StringTextComponent(DELEGATE.getData().getDisplayQuantity() + " x ")
					.appendSibling(((IFormattableTextComponent) rtn.get(0)))
			);
			rtn.add(1,
				new StringTextComponent(DELEGATE.getData().getMatcherDisplayName())
					.mergeStyle(TextFormatting.GRAY)
			);
			return rtn;
		}

		@Override
		public void onSelectionChanged() {
			if (!Client.allowMultipleRuleWindows && isSelected()) {
				PARENT.PARENT.CONTROLLER.getChildren().stream()
					.filter(c -> c instanceof FlowDataHolder)
					.filter(c -> ((FlowDataHolder<?>) c).getData() instanceof ItemMatcher)
					.filter(c -> c != this)
					.forEach(c -> c.setVisibleAndEnabled(false));

				PARENT.DRAWER.getChildren().stream()
					.filter(c -> c instanceof ItemMatcherDrawerItem
						&& c != ItemMatcherDrawerItem.this)
					.forEach(c -> ((ItemMatcherDrawerItem<?>) c).ICON.setSelected(false));
			}
			DELEGATE.setVisible(isSelected());
			DELEGATE.setEnabled(isSelected());
		}
	}
}
