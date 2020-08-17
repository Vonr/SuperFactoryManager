package ca.teamdman.sfm.client.gui.impl;

import ca.teamdman.sfm.client.gui.core.FlowIconButton;
import ca.teamdman.sfm.client.gui.core.FlowPositionBox;
import ca.teamdman.sfm.client.gui.core.IDeletable;
import ca.teamdman.sfm.client.gui.core.Size;
import ca.teamdman.sfm.client.gui.manager.ManagerFlowController;
import ca.teamdman.sfm.common.flowdata.FlowData;
import ca.teamdman.sfm.common.flowdata.InputFlowData;
import ca.teamdman.sfm.common.flowdata.Position;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.ManagerDeletePacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.ManagerPositionPacketC2S;
import java.util.Optional;

public class FlowInputButton extends FlowIconButton implements IDeletable {

	public final ManagerFlowController CONTROLLER;
	public InputFlowData data;

	public FlowInputButton(ManagerFlowController controller,
		InputFlowData data) {
		super(ButtonLabel.INPUT);
		POS.setMovable(true);
		this.data = data;
		this.CONTROLLER = controller;
		this.POS.getPosition().setXY(data.position);
	}

	@Override
	public Optional<FlowData> getData() {
		return Optional.of(data);
	}

	@Override
	public FlowPositionBox createPositionBox(Position pos, int width, int height) {
		//noinspection DuplicatedCode
		return new FlowPositionBox(pos, new Size(width, height)) {
			@Override
			public void onMoveFinished(int startMouseX, int startMouseY,
				int finishMouseX, int finishMouseY, int button) {
				PacketHandler.INSTANCE.sendToServer(new ManagerPositionPacketC2S(
					CONTROLLER.SCREEN.CONTAINER.windowId,
					CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
					data.getId(),
					this.getPosition()));
			}
		};
	}

	@Override
	public void delete() {
		PacketHandler.INSTANCE.sendToServer(new ManagerDeletePacketC2S(
			CONTROLLER.SCREEN.CONTAINER.windowId,
			CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
			data.getId()
		));
	}
}
