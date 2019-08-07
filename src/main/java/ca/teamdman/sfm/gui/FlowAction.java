package ca.teamdman.sfm.gui;

import net.minecraft.util.text.TranslationTextComponent;

public class FlowAction extends Button {
	private final ISprite sprite;
	private boolean pressed = false;

	public FlowAction(int x, int y, ISprite sprite, TranslationTextComponent label, Runnable onClick) {
		super(x, y, Sprite.CASE.getWidth(), Sprite.CASE.getHeight(), label, onClick);
		this.sprite = sprite;
	}

	public boolean isPressed() {
		return pressed;
	}

	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}

	public ISprite getSprite() {
		return sprite;
	}
}
