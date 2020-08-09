package ca.teamdman.sfm.client.gui.impl;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.FlowIconButton;
import ca.teamdman.sfm.client.gui.core.FlowPositionBox;
import ca.teamdman.sfm.client.gui.core.Position;
import ca.teamdman.sfm.client.gui.core.Size;
import ca.teamdman.sfm.common.flowdata.InputData;

public class FlowInputButton extends FlowIconButton {

	public InputData data;

	public FlowInputButton(InputData data) {
		super(ButtonLabel.INPUT);
		this.data = data;
	}

	@Override
	public FlowPositionBox createPositionBox(Position pos, int width, int height) {
		return new FlowPositionBox(pos, new Size(width, height)) {
			@Override
			public void onMoveFinished(BaseScreen screen, int startMouseX, int startMouseY,
				int finishMouseX, int finishMouseY, int button) {
//				PacketHandler.INSTANCE.sendToServer(new ButtonPositionPacketC2S(
//					CONTAINER.windowId,
//					CONTAINER.getSource().getPos(),
//					0,
//					this.getPosition().getX(),
//					this.getPosition().getY()));
			}
		};
	}
}
