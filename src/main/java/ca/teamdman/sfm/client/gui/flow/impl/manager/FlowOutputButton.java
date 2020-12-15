/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager;

import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.util.CableInventoryDrawerButton;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.impl.FlowOutputData;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateOutputPacketC2S;

public class FlowOutputButton extends CableInventoryDrawerButton<FlowOutputData> {

	public FlowOutputButton(
		ManagerFlowController controller,
		FlowOutputData data
	) {
		super(controller, data, ButtonLabel.OUTPUT);
	}

	@Override
	public void cloneWithPosition(int x, int y) {
		PacketHandler.INSTANCE.sendToServer(new ManagerCreateOutputPacketC2S(
			CONTROLLER.SCREEN.CONTAINER.windowId,
			CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
			new Position(x, y)
		));
	}
}
