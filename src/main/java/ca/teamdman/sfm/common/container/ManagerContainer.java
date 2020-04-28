package ca.teamdman.sfm.common.container;

import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.manager.ManagerFlowController;
import ca.teamdman.sfm.common.registrar.ContainerRegistrar;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import net.minecraft.network.PacketBuffer;

import java.util.function.Consumer;

public class ManagerContainer extends BaseContainer<ManagerTileEntity> {
	private final ManagerFlowController CONTROLLER = new ManagerFlowController(this);
	public        int                   x, y;

	public ManagerContainer(int windowId, ManagerTileEntity tile, boolean isRemote) {
		super(ContainerRegistrar.Containers.MANAGER, windowId, tile, isRemote);
		this.x = tile.x;
		this.y = tile.y;
	}

	@Override
	public void init() {
		super.init();
		CONTROLLER.init();
	}

	@Override
	public void gatherControllers(Consumer<IFlowController> c) {
		c.accept(CONTROLLER);
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
	}

	public void writeData(PacketBuffer data) {
		data.writeInt(x);
		data.writeInt(y);
	}

	public void readData(PacketBuffer data) {
		this.x = data.readInt();
		this.y = data.readInt();
	}
}
