package ca.teamdman.sfm.client.gui.flow.impl.manager.util.ruledrawer;

import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowDrawer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowPlusButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.ItemStackFlowComponent;
import ca.teamdman.sfm.common.flow.core.Position;

class EditChildrenButton extends FlowPlusButton {

	private boolean open = false;
	private FlowDrawer PARENT;

	public EditChildrenButton(FlowDrawer SELECTION_RULES_DRAWER) {
		super(
			new Position(),
			ItemStackFlowComponent.DEFAULT_SIZE.copy(),
			CONST.ADD_BUTTON
		);
		this.PARENT = SELECTION_RULES_DRAWER;
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		open = !open;
		PARENT.setEnabled(open);
		PARENT.setVisible(open);
	}
}
