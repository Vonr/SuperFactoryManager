package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.SFM;
import net.minecraft.util.ResourceLocation;

public enum ButtonLabel {
	INPUT(0, 0, 14, 14),
	OUTPUT(0, 14, 14, 14),
	ADD_INPUT(0, 28, 14, 14),
	ADD_TIMER_TRIGGER(0, 70, 14, 14),
	ADD_OUTPUT(0, 42, 14, 14),
	TRIGGER(0, 56, 14, 14),
	SETTINGS(0, 84, 14, 14),
	COMPARER_MATCHER(0, 98, 14, 14),
	MODID_MATCHER(0, 112, 14, 14),
	NONE(0, 0, 0, 0);

	public final ResourceLocation SPRITE_SHEET = new ResourceLocation(
		SFM.MOD_ID,
		"textures/gui/sprites.png"
	);
	public final int LEFT, TOP, WIDTH, HEIGHT;
	public final FlowSprite SPRITE;

	ButtonLabel(int left, int top, int width, int height) {
		this.LEFT = left;
		this.TOP = top;
		this.WIDTH = width;
		this.HEIGHT = height;
		this.SPRITE = new FlowSprite(SPRITE_SHEET, left, top, width, height);
	}
}
