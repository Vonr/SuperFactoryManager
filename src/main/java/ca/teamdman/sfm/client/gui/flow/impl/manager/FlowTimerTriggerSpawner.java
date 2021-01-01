/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager;

import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateTimerTriggerPacketC2S;

public class FlowTimerTriggerSpawner extends FlowIconButton {

	private final ManagerFlowController managerFlowController;

	public FlowTimerTriggerSpawner(
		ManagerFlowController managerFlowController
	) {
		super(ButtonLabel.ADD_TIMER_TRIGGER, new Position(25, 25));
		setDraggable(false);
		this.managerFlowController = managerFlowController;
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		PacketHandler.INSTANCE.sendToServer(new ManagerCreateTimerTriggerPacketC2S(
			managerFlowController.SCREEN.getContainer().windowId,
			managerFlowController.SCREEN.getContainer().getSource().getPos(),
			new Position(0, 0)
		));
	}
}
