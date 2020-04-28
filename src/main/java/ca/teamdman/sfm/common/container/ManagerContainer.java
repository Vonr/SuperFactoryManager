package ca.teamdman.sfm.common.container;

import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.manager.ManagerFlowController;
import ca.teamdman.sfm.common.registrar.ContainerRegistrar;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;

import java.util.function.Consumer;

public class ManagerContainer extends CoreContainer<ManagerTileEntity> {
	public  int                   x,y;
	private ManagerFlowController controller;

	public ManagerContainer(int windowId, ManagerTileEntity tile, boolean isRemote) {
		super(ContainerRegistrar.Containers.MANAGER, windowId, tile, isRemote);
		this.x = tile.x;
		this.y = tile.y;
	}

	@Override
	public void gatherControllers(Consumer<IFlowController> c) {
		this.controller = new ManagerFlowController(this);
		c.accept(controller);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
	}
}
