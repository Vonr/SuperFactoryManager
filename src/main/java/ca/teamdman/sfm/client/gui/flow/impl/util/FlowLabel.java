/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.common.flow.data.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;

public class FlowLabel extends FlowComponent {
	private String content;

	private Colour3f textColour = CONST.TEXT_PRIMARY;
	private Colour3f backgroundColour = CONST.PANEL_BORDER;

	public FlowLabel(
		Position pos,
		Size size,
		String content
	) {
		super(pos, size);
		this.content = content;
		setDraggable(false);
	}


	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
//		super.draw(screen, matrixStack, mx, my, deltaTime);
		screen.drawRect(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getHeight(),
			backgroundColour
		);
		screen.drawString(
			matrixStack,
			content,
			getPosition().getX()+5,
			getPosition().getY()+5,
			textColour.toInt()
		);
	}
}
