package ca.teamdman.sfm.common.container;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.manager.ManagerFlowController;
import ca.teamdman.sfm.common.flowdata.IFlowData;
import ca.teamdman.sfm.common.registrar.ContainerRegistrar;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class ManagerContainer extends
	BaseContainer<ManagerTileEntity> {
	public final Marker MARKER = MarkerManager.getMarker(getClass().getSimpleName());
	public final ArrayList<IFlowData> DATA = new ArrayList<>();
	public final ManagerFlowController CONTROLLER = new ManagerFlowController(this);

	public ManagerContainer(int windowId, ManagerTileEntity tile, boolean isRemote) {
		super(ContainerRegistrar.Containers.MANAGER, windowId, tile, isRemote);
	}

	@Override
	public void init() {
		super.init();
		if (IS_REMOTE) {
			SFM.LOGGER.debug(MARKER, "Initializing with {} data entries", getSource().data.size());
			DATA.clear();
			getSource().data.forEach(data -> DATA.add(data.copy()));
			CONTROLLER.load();
		}
	}

	@Override
	public void gatherControllers(Consumer<IFlowController> c) {
		c.accept(CONTROLLER);
	}

//	@Override
//	public void detectAndSendChanges() {
//		//		super.detectAndSendChanges(); // no item slots, no need for super
//		if (this.x != getSource().x || this.y != getSource().y) {
//			this.x = getSource().x;
//			this.y = getSource().y;
//			forEachPlayerWithContainerOpened(p -> {
//				PacketHandler.INSTANCE.send(
//						PacketDistributor.PLAYER.with(() -> p),
//						new ButtonPositionPacketS2C(windowId, 0, x, y));
//			});
//		}
//	}

	public static void writeData(ManagerTileEntity tile, PacketBuffer data) {
		data.writeCompoundTag(tile.serializeNBT());
	}

	public void readData(PacketBuffer data) {
		getSource().deserializeNBT(data.readCompoundTag());
	}
}
