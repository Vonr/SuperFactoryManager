package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemmovementrule;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowMinusButton;
import ca.teamdman.sfm.common.flow.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Arrays;
import java.util.List;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

class MinimizeButton extends FlowMinusButton {

	private final ItemMovementRuleFlowComponent PARENT;

	public MinimizeButton(
		ItemMovementRuleFlowComponent rule,
		Position pos,
		Size size
	) {
		super(pos, size, CONST.MINIMIZE);
		this.PARENT = rule;
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		PARENT.setVisible(false);
	}

	@Override
	public List<? extends ITextProperties> getTooltip() {
		return Arrays.asList(
			new TranslationTextComponent(
				"gui.sfm.manager.tile_entity_rule.minimize_button.hint1"
			),
			new TranslationTextComponent(
				"gui.sfm.manager.tile_entity_rule.minimize_button.hint2",
				PARENT.CONTROLLER.SCREEN.getMinecraft()
					.gameSettings.keyBindInventory.getKey()
					.func_237520_d_().getString()
			).mergeStyle(TextFormatting.GRAY)
		);
	}

	@Override
	public void draw(
		BaseScreen screen,
		MatrixStack matrixStack,
		int mx,
		int my,
		float deltaTime
	) {
		screen.drawRect(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getHeight(),
			CONST.SCREEN_BACKGROUND
		);
		screen.drawBorder(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getHeight(),
			1,
			CONST.PANEL_BORDER
		);
		super.draw(screen, matrixStack, mx, my, deltaTime);
	}
}
