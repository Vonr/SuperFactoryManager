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

public class FlowCheckbox extends FlowContainer {

	private final FlowLabel LABEL;
	private final FlowToggleBox BOX;

	public FlowCheckbox(Position pos, String label) {
		super(pos, new Size(0, 0));
		this.LABEL = new FlowLabel(pos, new Size(0, 0), label);
		this.BOX = new FlowToggleBox();
	}

	public static class FlowToggleBox extends FlowButton {

		private boolean checked = false;

		@Override
		public void draw(
			BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
		) {
			super.draw(screen, matrixStack, mx, my, deltaTime);
			int checkMargin = 3;
			Colour3f colour = isChecked()
					? CONST.SELECTED
					: isInBounds(mx, my) ? CONST.HIGHLIGHT : null;
			if (colour != null) {
				screen.drawRect(
					matrixStack,
					getPosition().getX() + checkMargin,
					getPosition().getY() + checkMargin,
					getSize().getWidth(),
					getSize().getHeight(),
					colour
				);
			}
		}

		public boolean isChecked() {
			return checked;
		}

		public void setChecked(boolean checked) {
			this.checked = checked;
		}

		@Override
		public void onClicked(int mx, int my, int button) {
			setChecked(!isChecked());
		}
	}
}