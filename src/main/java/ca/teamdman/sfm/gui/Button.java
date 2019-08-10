package ca.teamdman.sfm.gui;

import net.minecraft.util.text.TranslationTextComponent;

public class Button extends Component {
	protected TranslationTextComponent label;
	protected Runnable                 onClick;

	public TranslationTextComponent getLabel() {
		return label;
	}

	private boolean pressed = false;

	public Button(int x, int y, int width, int height, TranslationTextComponent label, Runnable onClick) {
		super(x, y, width, height);
		this.label = label;
		this.onClick = onClick;
	}

	public boolean isPressed() {
		return pressed;
	}

	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}

	public void click() {
		onClick.run();
	}

	@Override
	protected <T extends Component> T copy(ManagerGui gui) {
		Button copy = new Button(x,y,width,height,label,onClick);
		gui.buttonController.addButton(copy);
		return (T) copy;
	}
}
