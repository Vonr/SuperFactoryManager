package ca.teamdman.sfm.gui;

import ca.teamdman.sfm.SFM;
import net.minecraft.util.ResourceLocation;

public enum Sprite implements ISprite {
	INPUT(0, 0, 14, 14),
	OUTPUT(0, 14, 14, 14),
	CASE(14, 0, 22, 22),
	CASE_DARK(14, 22, 22, 22);

	public static final ResourceLocation SHEET = new ResourceLocation(SFM.MOD_ID, "textures/gui/sprites.png");
	private final int LEFT, TOP, WIDTH, HEIGHT;

	Sprite(int left, int top, int width, int height) {
		this.TOP = top;
		this.LEFT = left;
		this.WIDTH = width;
		this.HEIGHT = height;
	}

	@Override
	public int getTop() {
		return TOP;
	}

	@Override
	public int getLeft() {
		return LEFT;
	}

	@Override
	public int getWidth() {
		return WIDTH;
	}

	@Override
	public int getHeight() {
		return HEIGHT;
	}
}
