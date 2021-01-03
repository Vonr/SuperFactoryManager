/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.common.flow.data.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;

public class FlowToggleBox extends FlowButton {

	private boolean checked = false;

	public FlowToggleBox(Position pos, Size size) {
		super(pos, size);
		setDraggable(false);
	}

	public FlowToggleBox(Position pos, Size size, boolean checked) {
		super(pos, size);
		this.checked = checked;
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		screen.drawRect(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getHeight(),
			CONST.CHECKBOX_BACKGROUND
		);

		int checkMargin = 2;
		Colour3f colour = isChecked()
			? CONST.SELECTED
			: isInBounds(mx, my) ? CONST.HIGHLIGHT : null;
		if (colour != null) {
			screen.drawRect(
				matrixStack,
				getPosition().getX() + checkMargin,
				getPosition().getY() + checkMargin,
				getSize().getWidth() - checkMargin * 2,
				getSize().getHeight() - checkMargin * 2,
				colour
			);
		}

		drawTooltip(screen, matrixStack, mx, my, deltaTime);
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
		onChecked(isChecked());
	}

	public void onChecked(boolean checked) {
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		setChecked(!isChecked());
	}
}
