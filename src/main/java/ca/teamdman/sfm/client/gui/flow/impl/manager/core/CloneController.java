/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.core;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.IFlowCloneable;
import ca.teamdman.sfm.client.gui.flow.core.IFlowView;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton.ButtonBackground;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Optional;
import net.minecraft.client.gui.screen.Screen;

public class CloneController extends FlowComponent {

	public final ManagerFlowController CONTROLLER;
	private FlowComponent cloning = null;

	public CloneController(ManagerFlowController CONTROLLER) {
		this.CONTROLLER = CONTROLLER;
	}

	@Override
	public boolean mousePressed(int mx, int my, int button) {
		if (!Screen.hasControlDown()) {
			return false;
		}
		Optional<FlowComponent> hit = CONTROLLER.getElementUnderMouse(mx, my)
			.filter(c -> c instanceof IFlowCloneable);
		hit.ifPresent(c -> cloning = c);
		return hit.isPresent();
	}

	@Override
	public boolean mouseReleased(int mx, int my, int button) {
		if (cloning != null) {
			((IFlowCloneable) cloning).cloneWithPosition(mx, my);
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
		cloning.drawGhost(screen, matrixStack, mx, my, deltaTime);
	}

	/**
	 * Draws a blank background button at the position. Unused because looked tacky.
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
		return super.getZIndex() + 300;
	}
}
