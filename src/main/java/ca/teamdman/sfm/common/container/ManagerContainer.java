package ca.teamdman.sfm.common.container;

import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.manager.ManagerFlowController;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.ButtonPositionPacketS2C;
import ca.teamdman.sfm.common.registrar.ContainerRegistrar;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Consumer;

public class ManagerContainer extends BaseContainer<ManagerTileEntity> {
	public final ManagerFlowController CONTROLLER = new ManagerFlowController(this);
	public        int                   x, y;

	public ManagerContainer(int windowId, ManagerTileEntity tile, boolean isRemote) {
		super(ContainerRegistrar.Containers.MANAGER, windowId, tile, isRemote);
		this.x = tile.x;
		this.y = tile.y;
	}

	@Override
	public void init() {
		super.init();
		CONTROLLER.load();
	}

	@Override
	public void gatherControllers(Consumer<IFlowController> c) {
		c.accept(CONTROLLER);
	}

	@Override
	public void detectAndSendChanges() {
		//		super.detectAndSendChanges(); // no item slots, no need for super
		if (this.x != getSource().x || this.y != getSource().y) {
			this.x = getSource().x;
			this.y = getSource().y;
			forEachPlayerWithContainerOpened(p -> {
				PacketHandler.INSTANCE.send(
						PacketDistributor.PLAYER.with(() -> p),
						new ButtonPositionPacketS2C(windowId, 0, x, y));
			});
		}
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
