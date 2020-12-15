/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager;

import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowPanel;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.impl.FlowLineNodeData;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.ManagerPositionPacketC2S;
import java.util.Optional;

public class FlowLineNode extends FlowIconButton {

	public final ManagerFlowController CONTROLLER;
	public FlowLineNodeData data;

	public FlowLineNode(ManagerFlowController controller,
		FlowLineNodeData data) {
		super(ButtonBackground.LINE_NODE, ButtonLabel.NONE);
		POS.setMovable(true);
		this.data = data;
		this.CONTROLLER = controller;
		this.POS.getPosition().setXY(data.getPosition());
	}

	@Override
	public Optional<FlowData> getData() {
		return Optional.of(data);
	}

	@Override
	public FlowPanel createPositionBox(Position pos, int width, int height) {
		//noinspection DuplicatedCode
		return new FlowPanel(pos, new Size(width, height)) {
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
}
