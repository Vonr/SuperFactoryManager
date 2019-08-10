package ca.teamdman.sfm.gui;

import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ca.teamdman.sfm.gui.BaseGui.bindTexture;
import static ca.teamdman.sfm.gui.Sprite.*;

public class CommandController {
	private final ArrayList<Command> COMMAND_LIST = new ArrayList<>();
	private final ManagerGui         GUI;

	public CommandController(ManagerGui gui) {
		this.GUI = gui;
		addCommand(new Command(50, 50, INPUT, new TranslationTextComponent("woot"), () -> {
			System.out.println("left");
		}));
		addCommand(new Command(150, 50, OUTPUT, new TranslationTextComponent("woot"), () -> {
			System.out.println("center");
		}));
		addCommand(new Command(250, 50, INPUT, new TranslationTextComponent("woot"), () -> {
			System.out.println("right");
		}));
	}

	public Command addCommand(Command c) {
		COMMAND_LIST.add(c);
		GUI.buttonController.addButton(c);
		return c;
	}

	public void draw() {
		bindTexture(SHEET);
		for (Command action : COMMAND_LIST) {
			GUI.drawSprite(action.getX(), action.getY(), action.isPressed() ? CASE_DARK : CASE);
			GUI.drawSprite(action.getX() + 4, action.getY() + 4, action.getSprite());
		}
	}

	public List<Command> getCommands() {
		return Collections.unmodifiableList(COMMAND_LIST);
	}
}
