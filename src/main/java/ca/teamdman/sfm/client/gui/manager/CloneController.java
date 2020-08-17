package ca.teamdman.sfm.client.gui.manager;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.FlowIconButton.ButtonBackground;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import com.mojang.blaze3d.matrix.MatrixStack;

public class CloneController implements IFlowController, IFlowView {

	public final ManagerFlowController CONTROLLER;

	public CloneController(ManagerFlowController CONTROLLER) {
		this.CONTROLLER = CONTROLLER;
	}

	@Override
	public boolean mousePressed(int mx, int my, int button) {
		return false;
	}

	@Override
	public boolean mouseReleased(int mx, int my, int button) {
		return false;
	}

	@Override
	public boolean mouseDragged(int mx, int my, int button, int dmx, int dmy) {
		return false;
	}

	@Override
	public IFlowView getView() {
		return this;
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		BaseScreen.bindTexture(ButtonBackground.SPRITE_SHEET);
		ButtonBackground background = ButtonBackground.NORMAL;
		screen.drawSpriteRaw(
			matrixStack,
			mx,
			my,
			background.LEFT,
			background.TOP,
			background.WIDTH,
			background.HEIGHT
		);
	}

	@Override
	public int getZIndex() {
		return 1;
	}
}
