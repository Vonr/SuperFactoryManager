package ca.teamdman.sfm.gui;

import net.minecraft.util.text.TranslationTextComponent;

import java.util.Optional;
import java.util.function.Consumer;

public class Button extends Component {
	protected final Consumer<Component>      ACTION;
	protected final TranslationTextComponent LABEL;
	private         boolean                  pressed = false;

	public Button(Point position, int width, int height, TranslationTextComponent label, Consumer<Component> action) {
		super(position, width, height);
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
		ACTION.accept(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T extends Component> Optional<T> copy(ManagerGui gui) {
		Button copy = new Button(position, width, height, LABEL, ACTION);
		gui.BUTTON_CONTROLLER.addButton(copy);
		return Optional.of((T) copy);
	}
}
