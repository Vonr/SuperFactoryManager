package ca.teamdman.sfm.client.gui.flow.impl.manager.core;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.IFlowCloneable;
import ca.teamdman.sfm.client.gui.flow.core.IFlowController;
import ca.teamdman.sfm.client.gui.flow.core.IFlowView;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton.ButtonBackground;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;

public class CloneController implements IFlowController, IFlowView {

	public final ManagerFlowController CONTROLLER;
	private IFlowCloneable cloning = null;
	private int startX, startY;

	public CloneController(ManagerFlowController CONTROLLER) {
		this.CONTROLLER = CONTROLLER;
	}

	@Override
	public boolean mousePressed(int mx, int my, int button) {
		return Screen.hasControlDown() && CONTROLLER.getElementUnderMouse(mx, my)
			.filter(c -> c instanceof IFlowCloneable)
			.map(c -> ((IFlowCloneable) c))
			.map(x -> {
				startX = mx;
				startY = my;
				cloning = x;
				return true;
			})
			.isPresent();
	}

	@Override
	public boolean mouseReleased(int mx, int my, int button) {
		if (cloning != null) {
			cloning.cloneWithPosition(mx, my);
			cloning = null;
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseDragged(int mx, int my, int button, int dmx, int dmy) {
		return cloning != null;
	}

	@Override
	public IFlowView getView() {
		return this;
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		if (cloning == null) {
			return;
		}
		cloning.drawGhostAtPosition(screen, matrixStack, mx, my, deltaTime);
	}

	/**
	 * Draws a blank background button at the position.
	 * Unused because looked tacky.
	 */
	private void drawGhostIndicator(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		ButtonBackground background = ButtonBackground.NORMAL;
		BaseScreen.bindTexture(ButtonBackground.SPRITE_SHEET);
		screen.drawTextureWithRGBA(
			matrixStack,
			mx,
			my,
			background.LEFT,
			background.TOP,
			background.WIDTH,
			background.HEIGHT,
			1f, 1f, 1f, 0.5f
		);
	}

	@Override
	public int getZIndex() {
		return 1;
	}
}
