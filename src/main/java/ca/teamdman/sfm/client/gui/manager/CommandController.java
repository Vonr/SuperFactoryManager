package ca.teamdman.sfm.client.gui.manager;

import ca.teamdman.sfm.client.gui.ManagerScreen;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.ManagerUpdatePacket;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CommandController {
	private final ArrayList<Command> COMMAND_LIST = new ArrayList<>();
	private final ManagerScreen      GUI;

	public CommandController(ManagerScreen gui) {
		this.GUI = gui;
		addCommand(new Command(new Point(50, 50), Sprite.INPUT, new TranslationTextComponent("woot"), (c) -> {
			System.out.println("left " + c.getPosition().getX() + "\t" + c.getPosition().getY());
			PacketHandler.INSTANCE.sendToServer(new ManagerUpdatePacket("howdy"));
		}));
		addCommand(new Command(new Point(150, 50), Sprite.OUTPUT, new TranslationTextComponent("woot"), (c) -> System.out.println("center " + c.getPosition().getX() + "\t" + c.getPosition().getY())));
		addCommand(new Command(new Point(250, 50), Sprite.INPUT, new TranslationTextComponent("woot"), (c) -> System.out.println("right " + c.getPosition().getX() + "\t" + c.getPosition().getY())));
	}

	public Command addCommand(Command c) {
		COMMAND_LIST.add(c);
		GUI.BUTTON_CONTROLLER.addButton(c);
		return c;
	}

	public void draw() {
		BaseScreen.bindTexture(Sprite.SHEET);
		for (Command action : COMMAND_LIST) {
			GUI.drawSprite(action.getPosition().getX(), action.getPosition().getY(), action.isPressed() ? Sprite.CASE_DARK : Sprite.CASE);
			GUI.drawSprite(action.getPosition().getX() + 4, action.getPosition().getY() + 4, action.getSprite());
		}
	}

	public List<Command> getCommands() {
		return Collections.unmodifiableList(COMMAND_LIST);
	}
}
