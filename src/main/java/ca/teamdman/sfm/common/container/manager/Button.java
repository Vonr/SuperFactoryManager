package ca.teamdman.sfm.common.container.manager;

import ca.teamdman.sfm.common.container.ManagerContainer;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Optional;
import java.util.function.Consumer;

public class Button extends Component {
	protected final Consumer<Component>      ACTION;
	protected final TranslationTextComponent LABEL;
	private         boolean                  pressed = false;

	public Button(ManagerContainer container, Point position, int width, int height, TranslationTextComponent label, Consumer<Component> action) {
		super(container, position, width, height);
		this.LABEL = label;
		this.ACTION = action;
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

	@Override
	protected Optional<Component> copy() {
		Button copy = new Button(CONTAINER, position, width, height, LABEL, ACTION);
		CONTAINER.BUTTON_CONTROLLER.addButton(copy);
		return Optional.of(copy);
	}
}
