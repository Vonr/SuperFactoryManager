package ca.teamdman.sfm.common.container.core.component;

import ca.teamdman.sfm.common.container.core.Point;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Optional;
import java.util.function.Consumer;

public class Button extends Component  {
	protected final Consumer<Component>   ACTION;
	protected final TranslationTextComponent LABEL;
	private         boolean                  pressed = false;

	public Button(Point position, int width, int height, TranslationTextComponent label, Consumer<Component> action) {
		super(position, width, height);
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
	public Optional<Component> copy() {
		return Optional.of(new Button(position, width, height, LABEL, ACTION));
	}
}
