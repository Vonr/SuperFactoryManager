package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.SFM;
import net.minecraft.util.ResourceLocation;

public enum ButtonBackground {
	NORMAL(14, 0, 22, 22),
	DEPRESSED(14, 22, 22, 22),
	LINE_NODE(36, 0, 8, 8),
	NONE(220, 220, 22, 22);

	public final ResourceLocation SPRITE_SHEET = new ResourceLocation(
		SFM.MOD_ID,
		"textures/gui/sprites.png"
	);
	public final int LEFT, TOP, WIDTH, HEIGHT;
	public final FlowSprite SPRITE;

	ButtonBackground(int left, int top, int width, int height) {
		this.LEFT = left;
		this.TOP = top;
		this.WIDTH = width;
		this.HEIGHT = height;
		this.SPRITE = new FlowSprite(SPRITE_SHEET, left, top, width, height);
	}
}
