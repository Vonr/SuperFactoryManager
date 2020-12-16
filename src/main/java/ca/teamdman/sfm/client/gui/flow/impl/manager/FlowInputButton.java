/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager;

import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.util.TileEntityRuleDrawer;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.impl.FlowTileInputData;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateInputPacketC2S;

public class FlowInputButton extends TileEntityRuleDrawer<FlowTileInputData> {

	public FlowInputButton(
		ManagerFlowController controller,
		FlowTileInputData data
	) {
		super(controller, data, ButtonLabel.INPUT);
	}

	@Override
	public void cloneWithPosition(int x, int y) {
		PacketHandler.INSTANCE.sendToServer(new ManagerCreateInputPacketC2S(
			CONTROLLER.SCREEN.CONTAINER.windowId,
			CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
			new Position(x, y)
		));
	}
}
