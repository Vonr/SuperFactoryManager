package ca.teamdman.sfm.common.container;

import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.manager.ManagerFlowController;
import ca.teamdman.sfm.common.flowdata.IFlowData;
import ca.teamdman.sfm.common.registrar.ContainerRegistrar;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.minecraft.network.PacketBuffer;

public class ManagerContainer extends
	BaseContainer<ManagerTileEntity> /*implements INBTSerializable<CompoundNBT>*/ {

	public final ManagerFlowController CONTROLLER = new ManagerFlowController(this);
	public final ArrayList<IFlowData> DATA = new ArrayList<>();

	public ManagerContainer(int windowId, ManagerTileEntity tile, boolean isRemote) {
		super(ContainerRegistrar.Containers.MANAGER, windowId, tile, isRemote);
		tile.data.forEach(data -> DATA.add(data.copy()));
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

//	@Override
//	public CompoundNBT serializeNBT() {
//		CompoundNBT tag = new CompoundNBT();
//		ListNBT list = new ListNBT();
//		DATA.forEach(b -> {
//			list.add(b.serializeNBT());
//		});
//		tag.put("inputs", list);
//		return tag;
//	}
//
//	@Override
//	public void deserializeNBT(CompoundNBT c) {
//		try {
//			c.getList("inputs", NBT.TAG_COMPOUND).forEach(tag -> {
//				FlowData data =
//				FlowInputButton btn = new FlowInputButton();
//				btn.deserializeNBT((CompoundNBT) tag);
//				INPUTS.add(btn);
//			});
//		} catch (Exception e) {
//			SFM.LOGGER.error("Error deserializing " + getClass().getName(), e);
//		}
//	}

//	public void writeData(PacketBuffer data) {
//		data.writeInt(x);
//		data.writeInt(y);
//	}
//
//	public void readData(PacketBuffer data) {
//		this.x = data.readInt();
//		this.y = data.readInt();
//	}
}
