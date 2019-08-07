package ca.teamdman.sfm.gui;

import ca.teamdman.sfm.SFM;
import net.minecraft.util.ResourceLocation;

public enum Sprite implements ISprite {
	INPUT(0, 0, 14, 14),
	OUTPUT(0, 14, 14, 14),
	CASE(14,0,22,22),
	CASE_DARK(14,22,22,22);

	private final int left, top, width, height;
	public static final ResourceLocation SHEET = new ResourceLocation(SFM.MOD_ID, "textures/gui/sprites.png");

	Sprite(int left, int top, int width, int height) {
		this.top = top;
		this.left = left;
		this.width = width;
		this.height = height;
	}

	@Override
	public int getLeft() {
		return left;
	}

	@Override
	public int getTop() {
		return top;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}
	}
