/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.common.flow.data.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.ResourceLocation;

public class FlowSprite {

	public final int LEFT, TOP, WIDTH, HEIGHT;
	public final ResourceLocation SHEET;

	/**
	 * @param sheet  Sprite sheet to be bound for render
	 * @param left   Offset on the sheet from the left
	 * @param top    Offset on the sheet from the top
	 * @param width  Width of the render area on the sheet
	 * @param height Height of the render area on the sheet
	 */
	public FlowSprite(ResourceLocation sheet, int left, int top, int width, int height) {
		this.SHEET = sheet;
		this.LEFT = left;
		this.TOP = top;
		this.WIDTH = width;
		this.HEIGHT = height;
	}

	public void drawAt(
		BaseScreen screen, MatrixStack matrixStack,
		Position pos
	) {
		drawAt(screen, matrixStack, pos.getX(), pos.getY());
	}

	public void drawAt(
		BaseScreen screen, MatrixStack matrixStack, int x,
		int y
	) {
		BaseScreen.bindTexture(SHEET);
		screen.drawTexture(matrixStack, x, y, LEFT, TOP, WIDTH, HEIGHT);
	}

	public void drawGhostAt(BaseScreen screen, MatrixStack matrixStack, int x, int y) {
		BaseScreen.bindTexture(SHEET);
		screen.drawTextureWithRGBA(matrixStack, x, y, LEFT, TOP, WIDTH, HEIGHT, 1, 1, 1, 0.5f);
	}
}
