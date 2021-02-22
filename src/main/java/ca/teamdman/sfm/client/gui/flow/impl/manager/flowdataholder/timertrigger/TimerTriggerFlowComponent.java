/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.timertrigger;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.IFlowCloneable;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.TimerTriggerFlowData;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import java.util.Optional;
import java.util.UUID;

public class TimerTriggerFlowComponent extends FlowContainer implements IFlowCloneable,
	FlowDataHolder<TimerTriggerFlowData> {

	final ManagerFlowController CONTROLLER;
	final Button BUTTON;
	final EditWindow WINDOW;
	TimerTriggerFlowData data;

	public TimerTriggerFlowComponent(ManagerFlowController controller, TimerTriggerFlowData data) {
		super();
		this.data = data;
		this.CONTROLLER = controller;

		this.BUTTON = new Button(this);
		addChild(BUTTON);

		this.WINDOW = new EditWindow(this);
		addChild(WINDOW);

		controller.SCREEN.getFlowDataContainer().addObserver(new FlowDataHolderObserver<>(
			TimerTriggerFlowData.class, this
		));
	}

	@Override
	public TimerTriggerFlowData getData() {
		return data;
	}

	@Override
	public void setData(TimerTriggerFlowData data) {
		this.data = data;
		BUTTON.onDataChanged();
		WINDOW.onDataChanged();
	}

	@Override
	public Position getCentroid() {
		return BUTTON.getCentroid();
	}

	@Override
	public Position snapToEdge(Position outside) {
		return BUTTON.snapToEdge(outside);
	}

	@Override
	public Optional<FlowComponent> getElementUnderMouse(
		int mx, int my
	) {
		return super.getElementUnderMouse(mx, my).map(__ -> this);
	}

	@Override
	public void cloneWithPosition(int x, int y) {
		CONTROLLER.SCREEN.sendFlowDataToServer(
			new TimerTriggerFlowData(
				UUID.randomUUID(),
				new Position(x, y),
				data.interval,
				false
			)
		);
	}

	@Override
	public boolean isDeletable() {
		return true;
	}
}
