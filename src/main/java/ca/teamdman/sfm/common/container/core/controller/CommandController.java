package ca.teamdman.sfm.common.container.core.controller;

import ca.teamdman.sfm.common.container.CoreContainer;
import ca.teamdman.sfm.common.container.core.Point;
import ca.teamdman.sfm.common.container.core.Sprite;
import ca.teamdman.sfm.common.container.core.component.CommandButton;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.ManagerUpdatePacket;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class CommandController extends BaseController {
	private final ArrayList<CommandButton> COMMAND_LIST = new ArrayList<>();

	public CommandController(CoreContainer<?> container) {
		super(container);
		addCommand(new CommandButton(new Point(50, 50), Sprite.INPUT, new TranslationTextComponent("woot"), (c) -> {
			System.out.println("left " + c.getPosition().getX() + "\t" + c.getPosition().getY());
			PacketHandler.INSTANCE.sendToServer(new ManagerUpdatePacket("howdy"));
		}));
		addCommand(new CommandButton(new Point(150, 50), Sprite.OUTPUT, new TranslationTextComponent("woot"), (c) -> System.out.println("center " + c.getPosition().getX() + "\t" + c.getPosition().getY())));
		addCommand(new CommandButton(new Point(250, 50), Sprite.INPUT, new TranslationTextComponent("woot"), (c) -> System.out.println("right " + c.getPosition().getX() + "\t" + c.getPosition().getY())));
	}

	public CommandButton addCommand(CommandButton c) {
		COMMAND_LIST.add(c);
		CONTAINER.BUTTON_CONTROLLER.addButton(c);
		return c;
	}


	public List<CommandButton> getCommands() {
		return Collections.unmodifiableList(COMMAND_LIST);
	}
}
