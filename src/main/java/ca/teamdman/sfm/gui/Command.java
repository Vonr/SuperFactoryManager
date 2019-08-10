package ca.teamdman.sfm.gui;

import net.minecraft.util.text.TranslationTextComponent;

public class Command extends Button {
	private final ISprite sprite;

	public Command(int x, int y, ISprite sprite, TranslationTextComponent label, Runnable onClick) {
		super(x, y, Sprite.CASE.getWidth(), Sprite.CASE.getHeight(), label, onClick);
		this.sprite = sprite;
	}

	public ISprite getSprite() {
		return sprite;
	}

	@Override
	protected <T extends Component> T copy(ManagerGui gui) {
		Command copy = new Command(x,y,sprite,label,onClick);
		gui.commandController.addCommand(copy);
		return (T) copy;
	}
}
