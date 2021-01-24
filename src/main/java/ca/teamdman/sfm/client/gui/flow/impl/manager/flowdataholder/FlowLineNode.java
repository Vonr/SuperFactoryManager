/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder;

import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.data.LineNodeFlowData;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;

public class FlowLineNode extends FlowIconButton implements FlowDataHolder<LineNodeFlowData> {

	public final ManagerFlowController CONTROLLER;
	private LineNodeFlowData data;

	public FlowLineNode(ManagerFlowController controller, LineNodeFlowData data) {
		super(
			ButtonBackground.LINE_NODE,
			ButtonBackground.LINE_NODE,
			ButtonLabel.NONE,
			data.getPosition().copy()
		);
		this.data = data;
		this.CONTROLLER = controller;
		this.CONTROLLER.SCREEN.getFlowDataContainer().addObserver(new FlowDataHolderObserver<>(
			this,
			LineNodeFlowData.class
		));
		setDraggable(true);
	}

	@Override
	public LineNodeFlowData getData() {
		return data;
	}

	@Override
	public void setData(LineNodeFlowData data) {
		this.data = data;
		getPosition().setXY(data.getPosition());
	}

	@Override
	public boolean isDeletable() {
		return true;
	}


	@Override
	public void onDragFinished(int dx, int dy, int mx, int my) {
		data.position = getPosition();
		CONTROLLER.SCREEN.sendFlowDataToServer(data);
	}

	@Override
	public void onClicked(int mx, int my, int button) {

	}
}
