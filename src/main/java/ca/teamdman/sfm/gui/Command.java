package ca.teamdman.sfm.gui;

import net.minecraft.util.text.TranslationTextComponent;

import java.util.Optional;
import java.util.function.Consumer;

public class Command extends Button {
	private final ISprite SPRITE;

	public Command(Point position, ISprite sprite, TranslationTextComponent label, Consumer<Component> onClick) {
		super(position, Sprite.CASE.getWidth(), Sprite.CASE.getHeight(), label, onClick);
		this.SPRITE = sprite;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected <T extends Component> Optional<T> copy(ManagerGui gui) {
		Command copy = new Command(position.copy(), SPRITE, LABEL, ACTION);
		gui.COMMAND_CONTROLLER.addCommand(copy);
		return Optional.of((T) copy);
	}

	@Override
	public Point snapToEdge(int x, int y) {
		int bevel = 4;
		return new Point(
				x < position.getX() + bevel ? position.getX() + bevel : Math.min(x, position.getX() + width - bevel),
				y < position.getY() - bevel ? position.getY() + bevel : Math.min(y, position.getY() + height - bevel)
		);
	}

	@Override
	public String toString() {
		return String.format("Command %s (%d,%d) ", getSprite(), position.getX(), position.getY());
	}

	public ISprite getSprite() {
		return SPRITE;
	}
}
