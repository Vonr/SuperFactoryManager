package ca.teamdman.sfm.client.gui.impl;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import net.minecraft.util.ResourceLocation;

public class FlowSprite {
	private final int LEFT, TOP, WIDTH, HEIGHT;
	private final ResourceLocation SHEET;

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

	public void drawAt(BaseScreen screen, Position pos) {
		drawAt(screen, pos.getX(), pos.getY());
	}

	public void drawAt(BaseScreen screen, int x, int y) {
		BaseScreen.bindTexture(SHEET);
		screen.drawSprite(x, y, LEFT, TOP, WIDTH, HEIGHT);
	}
}
