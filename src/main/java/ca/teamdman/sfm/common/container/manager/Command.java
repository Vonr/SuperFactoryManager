package ca.teamdman.sfm.common.container.manager;

import ca.teamdman.sfm.common.container.ManagerContainer;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Optional;
import java.util.function.Consumer;

public class Command extends Button {
	private final ISprite SPRITE;

	public Command(ManagerContainer container, Point position, ISprite sprite, TranslationTextComponent label, Consumer<Component> onClick) {
		super(container, position, Sprite.CASE.getWidth(), Sprite.CASE.getHeight(), label, onClick);
		this.SPRITE = sprite;
	}

	@Override
	protected  Optional<Component> copy() {
		Command copy = new Command(CONTAINER, position.copy(), SPRITE, LABEL, ACTION);
		CONTAINER.COMMAND_CONTROLLER.addCommand(copy);
		return Optional.of(copy);
	}

	@Override
	public Point snapToEdge(int x, int y) {
		int bevel = 4;
		return new Point(
				x < position.getX() + bevel ? position.getX() + bevel : Math.min(x, position.getX() + width - bevel),
				y < position.getY() + bevel ? position.getY() + bevel : Math.min(y, position.getY() + height - bevel)
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
