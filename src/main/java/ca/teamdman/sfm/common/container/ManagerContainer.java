package ca.teamdman.sfm.common.container;

import ca.teamdman.sfm.common.registrar.ContainerRegistrar;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;

public class ManagerContainer extends CoreContainer<ManagerTileEntity> {
	public ManagerContainer(int windowId, ManagerTileEntity tile) {
		super(ContainerRegistrar.Containers.MANAGER, windowId, tile);
	}
}
