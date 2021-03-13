package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.timertrigger;

import ca.teamdman.sfm.client.gui.flow.impl.util.ButtonLabel;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;

class Button extends FlowIconButton {
	private final TimerTriggerFlowComponent PARENT;

	public Button(TimerTriggerFlowComponent parent) {
		super(ButtonLabel.TRIGGER, parent.data.getPosition().copy());
		this.PARENT = parent;
		setDraggable(true);
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		PARENT.data.open = !PARENT.data.open;
		PARENT.CONTROLLER.SCREEN.sendFlowDataToServer(PARENT.data);
	}

	@Override
	public void onDragFinished(int dx, int dy, int mx, int my) {
		PARENT.data.position = getPosition();
		PARENT.CONTROLLER.SCREEN.sendFlowDataToServer(PARENT.data);
	}

	@Override
	protected boolean isDepressed() {
		return super.isDepressed() || PARENT.data.open;
	}


	public void onDataChanged() {
		getPosition().setXY(PARENT.data.getPosition());
	}
}
