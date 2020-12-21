/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.common.flow.data.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.ResourceLocation;

public abstract class FlowIconButton extends FlowButton {

	public final FlowSprite BACKGROUND;
	public final FlowSprite ICON;

	public FlowIconButton(ButtonBackground background, ButtonLabel label, Position pos) {
		super(pos, new Size(background.WIDTH, background.HEIGHT));
		this.BACKGROUND = createBackground(
			ButtonBackground.SPRITE_SHEET,
			background.LEFT,
			background.TOP,
			background.WIDTH,
			background.HEIGHT
		);
		this.ICON = createLabel(
			ButtonLabel.SPRITE_SHEET,
			label.LEFT,
			label.TOP,
			label.WIDTH,
			label.HEIGHT
		);
	}

	@Override
	public void drawGhost(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		BACKGROUND.drawGhostAt(screen, matrixStack, mx, my);
		ICON.drawGhostAt(screen, matrixStack, mx + 4, my + 4);
	}

	public FlowIconButton(ButtonLabel type, Position pos) {
		this(ButtonBackground.NORMAL, type, pos);
	}

	public FlowIconButton(ButtonBackground background, ButtonLabel label) {
		this(background, label, new Position(0, 0));
	}

	public FlowIconButton(ButtonLabel type) {
		this(type, new Position(0, 0));
	}

	public FlowSprite createBackground(
		ResourceLocation sheet, int left, int top, int width,
		int height
	) {
		return new FlowSprite(sheet, left, top, width, height);
	}

	public FlowSprite createLabel(
		ResourceLocation sheet, int left, int top, int width,
		int height
	) {
		return new FlowSprite(sheet, left, top, width, height);
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx,
		int my, float deltaTime
	) {
		BACKGROUND.drawAt(screen, matrixStack, getPosition());
		ICON.drawAt(screen, matrixStack, getPosition().getX() + 4, getPosition().getY() + 4);
	}

	public enum ButtonBackground {
		NORMAL(14, 0, 22, 22),
		DEPRESSED(14, 22, 22, 22),
		LINE_NODE(36, 0, 8, 8);

		public static final ResourceLocation SPRITE_SHEET = new ResourceLocation(
			SFM.MOD_ID,
			"textures/gui/sprites.png"
		);
		public final int LEFT, TOP, WIDTH, HEIGHT;

		ButtonBackground(int left, int top, int width, int height) {
			this.LEFT = left;
			this.TOP = top;
			this.WIDTH = width;
			this.HEIGHT = height;
		}
	}

	public enum ButtonLabel {
		INPUT(0, 0, 14, 14),
		OUTPUT(0, 14, 14, 14),
		ADD_INPUT(0, 28, 14, 14),
		ADD_OUTPUT(0, 42, 14, 14),
		TRIGGER(0, 56, 14, 14),
		NONE(0, 0, 0, 0);

		public static final ResourceLocation SPRITE_SHEET = new ResourceLocation(
			SFM.MOD_ID,
			"textures/gui/sprites.png"
		);
		public final int LEFT, TOP, WIDTH, HEIGHT;

		ButtonLabel(int left, int top, int width, int height) {
			this.LEFT = left;
			this.TOP = top;
			this.WIDTH = width;
			this.HEIGHT = height;
		}
	}
}
