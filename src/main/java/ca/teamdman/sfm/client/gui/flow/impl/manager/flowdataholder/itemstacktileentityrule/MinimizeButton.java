package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstacktileentityrule;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowMinusButton;
import ca.teamdman.sfm.common.flow.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;

class MinimizeButton extends FlowMinusButton {

	private ItemStackTileEntityRuleFlowComponent itemStackTileEntityRuleFlowComponent;

	public MinimizeButton(
		ItemStackTileEntityRuleFlowComponent itemStackTileEntityRuleFlowComponent,
		Position pos,
		Size size
	) {
		super(pos, size, CONST.MINIMIZE);
		this.itemStackTileEntityRuleFlowComponent = itemStackTileEntityRuleFlowComponent;
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		itemStackTileEntityRuleFlowComponent.setVisible(false);
		itemStackTileEntityRuleFlowComponent.setEnabled(false);
		itemStackTileEntityRuleFlowComponent.CONTROLLER.SCREEN.getFlowDataContainer().notifyChanged(
			itemStackTileEntityRuleFlowComponent.getData());
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
