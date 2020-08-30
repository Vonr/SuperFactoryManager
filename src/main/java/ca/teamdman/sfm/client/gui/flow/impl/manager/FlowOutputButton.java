package ca.teamdman.sfm.client.gui.flow.impl.manager;

import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.util.CableInventoryDrawerButton;
import ca.teamdman.sfm.common.flowdata.impl.FlowOutputData;

public class FlowOutputButton extends CableInventoryDrawerButton<FlowOutputData> {

	public FlowOutputButton(
		ManagerFlowController controller,
		FlowOutputData data
	) {
		super(controller, data, ButtonLabel.OUTPUT);
	}
}
