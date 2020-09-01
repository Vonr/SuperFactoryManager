package ca.teamdman.sfm.client.gui.flow.impl.manager;

import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateOutputPacketC2S;

public class FlowOutputButtonSpawner extends FlowIconButton {

	private final ManagerFlowController managerFlowController;

	public FlowOutputButtonSpawner(
		ManagerFlowController managerFlowController
	) {
		super(ButtonLabel.ADD_OUTPUT, new Position(25, 65));
		this.managerFlowController = managerFlowController;
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		PacketHandler.INSTANCE.sendToServer(new ManagerCreateOutputPacketC2S(
			managerFlowController.SCREEN.CONTAINER.windowId,
			managerFlowController.SCREEN.CONTAINER.getSource().getPos(),
			new Position(0, 0)
		));
	}
}
