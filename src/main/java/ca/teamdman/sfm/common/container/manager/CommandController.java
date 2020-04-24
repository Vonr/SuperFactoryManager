package ca.teamdman.sfm.common.container.manager;

import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.ManagerUpdatePacket;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CommandController {
	private final ArrayList<Command> COMMAND_LIST = new ArrayList<>();
	private final ManagerContainer   CONTAINER;

	public CommandController(ManagerContainer container) {
		this.CONTAINER = container;
		addCommand(new Command(CONTAINER, new Point(50, 50), Sprite.INPUT, new TranslationTextComponent("woot"), (c) -> {
			System.out.println("left " + c.getPosition().getX() + "\t" + c.getPosition().getY());
			PacketHandler.INSTANCE.sendToServer(new ManagerUpdatePacket("howdy"));
		}));
		addCommand(new Command(CONTAINER, new Point(150, 50), Sprite.OUTPUT, new TranslationTextComponent("woot"), (c) -> System.out.println("center " + c.getPosition().getX() + "\t" + c.getPosition().getY())));
		addCommand(new Command(CONTAINER, new Point(250, 50), Sprite.INPUT, new TranslationTextComponent("woot"), (c) -> System.out.println("right " + c.getPosition().getX() + "\t" + c.getPosition().getY())));
	}

	public Command addCommand(Command c) {
		COMMAND_LIST.add(c);
		CONTAINER.BUTTON_CONTROLLER.addButton(c);
		return c;
	}


	public List<Command> getCommands() {
		return Collections.unmodifiableList(COMMAND_LIST);
	}
}
