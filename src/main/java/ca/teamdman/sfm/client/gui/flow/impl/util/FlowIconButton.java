/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.common.flow.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;

public abstract class FlowIconButton extends FlowButton {

	protected FlowSprite NORMAL_BACKGROUND;
	protected FlowSprite DEPRESSED_BACKGROUND;
	protected FlowSprite LABEL;

	public FlowIconButton(
		ButtonBackground normalBackground,
		ButtonBackground depressedBackground,
		ButtonLabel label,
		Position pos
	) {
		super(pos, new Size(normalBackground.WIDTH, normalBackground.HEIGHT));
		this.NORMAL_BACKGROUND = normalBackground.SPRITE;
		this.DEPRESSED_BACKGROUND = depressedBackground.SPRITE;
		this.LABEL = label.SPRITE;
	}

	public FlowIconButton(ButtonLabel type, Position pos) {
		this(ButtonBackground.NORMAL, ButtonBackground.DEPRESSED, type, pos);
	}

	public FlowIconButton(ButtonLabel type) {
		this(type, new Position(0, 0));
	}

	@Override
	public void drawGhost(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		NORMAL_BACKGROUND.drawGhostAt(screen, matrixStack, mx, my);
		LABEL.drawGhostAt(screen, matrixStack, mx + 4, my + 4);
	}

	protected boolean isDepressed() {
		return isHovering() || clicking;
	}

	@Override
	public void draw(BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime) {
		if (isDepressed()) {
			DEPRESSED_BACKGROUND.drawAt(screen, matrixStack, getPosition());
		} else {
			NORMAL_BACKGROUND.drawAt(screen, matrixStack, getPosition());
		}
		LABEL.drawAt(screen, matrixStack, getPosition().getX() + 4, getPosition().getY() + 4);
		super.draw(screen, matrixStack, mx, my, deltaTime);
	}

}
