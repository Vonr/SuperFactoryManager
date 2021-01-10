/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder;

import ca.teamdman.sfm.client.gui.flow.core.IFlowCloneable;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.data.TimerTriggerFlowData;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.patch.ManagerPositionPacketC2S;

public class FlowTimerTrigger extends FlowIconButton implements IFlowCloneable,
	FlowDataHolder<TimerTriggerFlowData> {

	public final ManagerFlowController CONTROLLER;
	private TimerTriggerFlowData data;

	public FlowTimerTrigger(ManagerFlowController controller, TimerTriggerFlowData data) {
		super(ButtonLabel.TRIGGER, data.getPosition().copy());
		this.CONTROLLER = controller;
		this.data = data;
		setDraggable(true);
	}

	@Override
	public TimerTriggerFlowData getData() {
		return data;
	}

	@Override
	public void setData(TimerTriggerFlowData data) {
		this.data = data;
		getPosition().setXY(data.getPosition());
	}

	@Override
	public void cloneWithPosition(int x, int y) {
		//todo: switch to timertrigger flowdata
//		PacketHandler.INSTANCE.sendToServer(new ManagerFlowInputDataPacketC2S(
//			CONTROLLER.SCREEN.getContainer().windowId,
//			CONTROLLER.SCREEN.getContainer().getSource().getPos(),
//			UUID.randomUUID(),
//			new Position(x, y),
//			Collections.emptyList()
//		));
	}

	@Override
	public boolean isDeletable() {
		return true;
	}

	@Override
	public void onDragFinished(int dx, int dy, int mx, int my) {
		PacketHandler.INSTANCE.sendToServer(new ManagerPositionPacketC2S(
			CONTROLLER.SCREEN.getContainer().windowId,
			CONTROLLER.SCREEN.getContainer().getSource().getPos(),
			data.getId(),
			this.getPosition()
		));
	}

	@Override
	public void onClicked(int mx, int my, int button) {

	}
}
