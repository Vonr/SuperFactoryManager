package ca.teamdman.sfm.common.container.core.component;

import ca.teamdman.sfm.common.container.core.ISprite;
import ca.teamdman.sfm.common.container.core.Point;
import ca.teamdman.sfm.common.container.core.Sprite;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Optional;
import java.util.function.Consumer;

public class CommandButton extends Button {
	private final ISprite SPRITE;

	public CommandButton(CompoundNBT tag) {
		super(new Point(), 0, 0, new TranslationTextComponent(""), (__) -> {
		});
		this.SPRITE = Sprite.CASE;
		this.deserializeNBT(tag);
	}

	public CommandButton( Point position, ISprite sprite, TranslationTextComponent label, Consumer<Component> onClick) {
		super(position, Sprite.CASE.getWidth(), Sprite.CASE.getHeight(), label, onClick);
		this.SPRITE = sprite;
	}

	@Override
	public Optional<Component> copy() {
		CommandButton copy = new CommandButton(position.copy(), SPRITE, LABEL, ACTION);
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
