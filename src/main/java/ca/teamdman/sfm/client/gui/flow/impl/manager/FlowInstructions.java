/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.common.flow.data.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.resources.I18n;

public class FlowInstructions extends FlowComponent {

	public FlowInstructions(Position pos) {
		super(pos, new Size(0, 0));
		setEnabled(false);
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		matrixStack.push();
		matrixStack.translate(getPosition().getX(), getPosition().getY(), 0);
		screen.drawRightAlignedString(
			matrixStack,
			I18n.format("gui.sfm.manager.legend.chain"),
			0,
			0,
			CONST.TEXT_BACKGROUND.toInt()
		);
		screen.drawRightAlignedString(
			matrixStack,
			I18n.format("gui.sfm.manager.legend.clone"),
			0,
			10,
			CONST.TEXT_BACKGROUND.toInt()
		);
		screen.drawRightAlignedString(
			matrixStack,
			I18n.format("gui.sfm.manager.legend.move"),
			0,
			20,
			CONST.TEXT_BACKGROUND.toInt()
		);
		screen.drawRightAlignedString(
			matrixStack,
			I18n.format("gui.sfm.manager.legend.snaptogrid"),
			0,
			30,
			CONST.TEXT_BACKGROUND.toInt()
		);
		matrixStack.pop();
	}

	@Override
	public int getZIndex() {
		return super.getZIndex() - 2400;
	}
}
