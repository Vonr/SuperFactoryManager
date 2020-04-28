package ca.teamdman.sfm.common.container;

import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.manager.ManagerFlowController;
import ca.teamdman.sfm.common.registrar.ContainerRegistrar;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;

import java.util.function.Consumer;

public class ManagerContainer extends CoreContainer<ManagerTileEntity> {
	public  int                   myNumber;
	private ManagerFlowController controller;

	public ManagerContainer(int windowId, ManagerTileEntity tile, boolean isRemote) {
		super(ContainerRegistrar.Containers.MANAGER, windowId, tile, isRemote);
		this.myNumber = tile.getMyNumber();
	}

	@Override
	public void gatherControllers(Consumer<IFlowController> c) {
		this.controller = new ManagerFlowController();
		c.accept(controller);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
	}
}
