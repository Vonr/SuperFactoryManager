package ca.teamdman.sfm.gui;

import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ca.teamdman.sfm.SFM.LOGGER;
import static ca.teamdman.sfm.gui.BaseGui.bindTexture;
import static ca.teamdman.sfm.gui.Sprite.*;

public class CommandController {
	private final ArrayList<Command> COMMAND_LIST = new ArrayList<>();
	private final ManagerGui         GUI;

	public CommandController(ManagerGui gui) {
		this.GUI = gui;
		addCommand(new Command(new Point(50, 50), INPUT, new TranslationTextComponent("woot"), (c) -> System.out.println("left " + c.getPosition().getX() + "\t" + c.getPosition().getY())));
		addCommand(new Command(new Point(150, 50), OUTPUT, new TranslationTextComponent("woot"), (c) -> System.out.println("center " + c.getPosition().getX() + "\t" + c.getPosition().getY())));
		addCommand(new Command(new Point(250, 50), INPUT, new TranslationTextComponent("woot"), (c) -> System.out.println("right " + c.getPosition().getX() + "\t" + c.getPosition().getY())));
	}

	public Command addCommand(Command c) {
		COMMAND_LIST.add(c);
		GUI.BUTTON_CONTROLLER.addButton(c);
		LOGGER.debug("Command controller added command " + c);
		return c;
	}

	public void draw() {
		bindTexture(SHEET);
		for (Command action : COMMAND_LIST) {
			GUI.drawSprite(action.getPosition().getX(), action.getPosition().getY(), action.isPressed() ? CASE_DARK : CASE);
			GUI.drawSprite(action.getPosition().getX() + 4, action.getPosition().getY() + 4, action.getSprite());
		}
	}

	public List<Command> getCommands() {
		return Collections.unmodifiableList(COMMAND_LIST);
	}
}
