package ca.teamdman.sfm.gui;

import net.minecraft.util.text.TranslationTextComponent;

public class Command extends Button {
	private final ISprite SPRITE;

	public Command(int x, int y, ISprite sprite, TranslationTextComponent label, Runnable onClick) {
		super(x, y, Sprite.CASE.getWidth(), Sprite.CASE.getHeight(), label, onClick);
		this.SPRITE = sprite;
	}

	public ISprite getSprite() {
		return SPRITE;
	}

	@Override
	protected <T extends Component> T copy(ManagerGui gui) {
		Command copy = new Command(x, y, SPRITE, LABEL, ACTION);
		gui.commandController.addCommand(copy);
		return (T) copy;
	}
}
