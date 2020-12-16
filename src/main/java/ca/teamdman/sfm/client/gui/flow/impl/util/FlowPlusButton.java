/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.IFlowController;
import ca.teamdman.sfm.client.gui.flow.core.IFlowTangible;
import ca.teamdman.sfm.client.gui.flow.core.IFlowView;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.common.flow.data.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;

public class FlowPlusButton implements IFlowView, IFlowController, IFlowTangible {

	private final Colour3f COLOUR;
	private final FlowPanel PANEL;
	private boolean clicking = false;

	public FlowPlusButton(Position pos, Size size, Colour3f colour) {
		this.PANEL = new FlowPanel(pos, size);
		this.COLOUR = colour;
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		int x = PANEL.getPosition().getX();
		int y = PANEL.getPosition().getY();
		int w = PANEL.getSize().getWidth();
		int h = PANEL.getSize().getHeight();
		int thickness = 4;
		int margin = 2;
		if (isInBounds(mx, my)) {
			// highlight
			screen.drawRect(
				matrixStack,
				x,
				y,
				w,
				h,
				CONST.HIGHLIGHT
			);
		}
		// vertical bar
		screen.drawRect(
			matrixStack,
			x + w / 2 - thickness / 2,
			y + margin,
			thickness,
			h - margin * 2,
			COLOUR
		);
		// horizontal bar
		screen.drawRect(
			matrixStack,
			x + margin,
			y + h / 2 - thickness / 2,
			w - margin * 2,
			thickness,
			COLOUR
		);
	}

	@Override
	public IFlowView getView() {
		return this;
	}

	@Override
	public Position getPosition() {
		return PANEL.getPosition();
	}

	@Override
	public Size getSize() {
		return PANEL.getSize();
	}

	@Override
	public boolean mousePressed(int mx, int my, int button) {
		return clicking = isInBounds(mx, my);
	}

	@Override
	public boolean mouseReleased(int mx, int my, int button) {
		boolean wasClicking = clicking;
		clicking = false;
		if (wasClicking && isInBounds(mx, my)) {
			onClicked();
			return true;
		}
		return false;
	}

	public void onClicked() {};
}
