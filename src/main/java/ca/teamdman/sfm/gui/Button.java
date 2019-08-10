package ca.teamdman.sfm.gui;

import net.minecraft.util.text.TranslationTextComponent;

public class Button extends Component {
	protected final Runnable                 ACTION;
	protected final TranslationTextComponent LABEL;
	private boolean pressed = false;

	public Button(int x, int y, int width, int height, TranslationTextComponent label, Runnable action) {
		super(x, y, width, height);
		this.LABEL = label;
		this.ACTION = action;
	}

	public TranslationTextComponent getLABEL() {
		return LABEL;
	}

	public boolean isPressed() {
		return pressed;
	}

	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}

	public void click() {
		ACTION.run();
	}

	@Override
	protected <T extends Component> T copy(ManagerGui gui) {
		Button copy = new Button(x, y, width, height, LABEL, ACTION);
		gui.buttonController.addButton(copy);
		return (T) copy;
	}
}
