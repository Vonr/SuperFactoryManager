package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemrule;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowItemStackPicker;
import ca.teamdman.sfm.client.gui.flow.impl.util.ItemStackFlowButton;
import ca.teamdman.sfm.common.flow.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Collections;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

class RuleIconComponent extends FlowContainer {

	private final ItemRuleFlowComponent PARENT;
	private final FlowItemStackPicker PICKER;
	protected final ItemStackFlowButton BUTTON;

	public RuleIconComponent(
		ItemRuleFlowComponent parent,
		Position pos
	) {
		super(pos);
		this.PARENT = parent;
		this.BUTTON = new MyButton();
		this.PICKER = new MyPicker();
		PICKER.setVisibleAndEnabled(false);
		addChild(PICKER);
		addChild(BUTTON);
	}

	private class MyButton extends ItemStackFlowButton {

		public MyButton() {
			super(PARENT.getData().icon, new Position());
		}

		@Override
		public void onClicked(int mx, int my, int button) {
			if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
				PICKER.toggleVisibilityAndEnabled();
			} else if (button == GLFW.GLFW_MOUSE_BUTTON_2) {
				PICKER.onItemStackChanged(ItemStack.EMPTY);
			}
		}

		@Override
		public List<? extends ITextProperties> getTooltip() {
			return Collections.singletonList(new TranslationTextComponent("gui.sfm.flow.tooltip.right_click_to_clear"));
		}


		@Override
		public void draw(
			BaseScreen screen,
			MatrixStack matrixStack,
			int mx,
			int my,
			float deltaTime
		) {
			drawBackground(screen, matrixStack, CONST.PANEL_BACKGROUND_LIGHT);
			super.draw(screen, matrixStack, mx, my, deltaTime);
		}
	}

	private class MyPicker extends FlowItemStackPicker {

		public MyPicker() {
			super(
				PARENT.CONTROLLER,
				new Position(BUTTON.getSize().getWidth() + 5,0)
			);
		}

		@Override
		public void onItemStackChanged(ItemStack stack) {
			PARENT.getData().icon = stack;
			PARENT.CONTROLLER.SCREEN.sendFlowDataToServer(PARENT.getData());
		}
	}
}
