/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager;

import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.put.ManagerFlowInputDataPacketC2S;

public class FlowInputButtonSpawner extends FlowIconButton {

	private final ManagerFlowController managerFlowController;

	public FlowInputButtonSpawner(
		ManagerFlowController managerFlowController
	) {
		super(ButtonLabel.ADD_INPUT, new Position(25, 25));
		this.managerFlowController = managerFlowController;
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		PacketHandler.INSTANCE.sendToServer(new ManagerFlowInputDataPacketC2S(
			managerFlowController.SCREEN.CONTAINER.windowId,
			managerFlowController.SCREEN.CONTAINER.getSource().getPos(),
			new Position(0, 0)
		));
	}
}
