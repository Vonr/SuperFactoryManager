package ca.teamdman.sfm.gui;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ca.teamdman.sfm.gui.BaseGui.bindTexture;
import static ca.teamdman.sfm.gui.Sprite.*;

public class CommandController {
	private final ManagerGui gui;
	private final ArrayList<Command> commandList = new ArrayList<>();

	public CommandController(ManagerGui gui) {
		this.gui = gui;
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
		commandList.add(c);
		gui.buttonController.addButton(c);
		return c;
	}

	public void draw() {
		bindTexture(SHEET);
		for (Command action : commandList) {
			gui.drawSprite(action.getX(), action.getY(), action.isPressed() ? CASE_DARK : CASE);
			gui.drawSprite(action.getX() + 4, action.getY() + 4, action.getSprite());
		}
	}

	public List<Command> getCommands() {
		return Collections.unmodifiableList(commandList);
	}
}
