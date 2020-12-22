/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager;

import ca.teamdman.sfm.client.gui.flow.core.IFlowCloneable;
import ca.teamdman.sfm.client.gui.flow.core.IFlowDeletable;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.data.impl.FlowTimerTriggerData;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.delete.ManagerDeletePacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.patch.ManagerPositionPacketC2S;

public class FlowTimerTrigger extends FlowIconButton implements IFlowDeletable, IFlowCloneable,
	FlowDataHolder {

	public final ManagerFlowController CONTROLLER;
	public FlowTimerTriggerData data;

	public FlowTimerTrigger(ManagerFlowController controller, FlowTimerTriggerData data) {
		super(ButtonLabel.TRIGGER, data.getPosition().copy());
		this.CONTROLLER = controller;
		this.data = data;
	}

	@Override
	public FlowData getData() {
		return data;
	}

	@Override
	public void onDataChanged() {
		getPosition().setXY(data.getPosition());
	}

	@Override
	public void cloneWithPosition(int x, int y) {
		//todo: switch to timertrigger flowdata
//		PacketHandler.INSTANCE.sendToServer(new ManagerFlowInputDataPacketC2S(
//			CONTROLLER.SCREEN.CONTAINER.windowId,
//			CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
//			UUID.randomUUID(),
//			new Position(x, y),
//			Collections.emptyList()
//		));
	}

	@Override
	public void delete() {
		PacketHandler.INSTANCE.sendToServer(new ManagerDeletePacketC2S(
			CONTROLLER.SCREEN.CONTAINER.windowId,
			CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
			data.getId()
		));
	}

	@Override
	public void onDragFinished(int dx, int dy, int mx, int my) {
		PacketHandler.INSTANCE.sendToServer(new ManagerPositionPacketC2S(
			CONTROLLER.SCREEN.CONTAINER.windowId,
			CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
			data.getId(),
			this.getPosition()
		));
	}

	@Override
	public void onClicked(int mx, int my, int button) {

	}
}
