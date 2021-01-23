package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstacktileentityrule;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowMinusButton;
import ca.teamdman.sfm.common.flow.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

class MinimizeButton extends FlowMinusButton {

	private final ItemStackTileEntityRuleFlowComponent PARENT;

	public MinimizeButton(
		ItemStackTileEntityRuleFlowComponent itemStackTileEntityRuleFlowComponent,
		Position pos,
		Size size
	) {
		super(pos, size, CONST.MINIMIZE);
		this.PARENT = itemStackTileEntityRuleFlowComponent;
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		PARENT.getData().open = false;
		PARENT.CONTROLLER.SCREEN.sendFlowDataToServer(PARENT.getData());
	}

	@Override
	public List<? extends ITextProperties> getTooltip() {
		List<ITextProperties> rtn = new ArrayList<>();
		rtn.add(new TranslationTextComponent(
			"gui.sfm.manager.tile_entity_rule.minimize_button.hint1"
		));
		rtn.add(new TranslationTextComponent(
			"gui.sfm.manager.tile_entity_rule.minimize_button.hint2",
			PARENT.CONTROLLER.SCREEN.getMinecraft().gameSettings.keyBindInventory.getKey().func_237520_d_().getString()
		).mergeStyle(TextFormatting.GRAY));
		return rtn;
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
