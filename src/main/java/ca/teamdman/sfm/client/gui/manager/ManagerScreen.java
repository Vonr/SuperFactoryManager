package ca.teamdman.sfm.client.gui.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.gui.core.BaseContainerScreen;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.flowdata.IFlowData;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class ManagerScreen extends BaseContainerScreen<ManagerContainer> {

	public final HashMap<UUID, IFlowData> DATAS = new HashMap<>();
	public final ManagerFlowController CONTROLLER;
	public final Marker MARKER = MarkerManager.getMarker(getClass().getSimpleName());

	public ManagerScreen(ManagerContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, 512, 256, inv, name);
		CONTROLLER = new ManagerFlowController(this);
		reloadFromManagerTileEntity();
	}

	@Override
	public Stream<IFlowController> getControllers() {
		return Stream.of(CONTROLLER);
	}

	@Override
	public Stream<IFlowView> getViews() {
		return Stream.of(CONTROLLER);
	}

	public void reloadFromManagerTileEntity() {
		SFM.LOGGER
			.debug(MARKER, "Loading {} data entries from tile", CONTAINER.getSource().data.size());
		CONTAINER.getSource().data.values().forEach(data -> DATAS.put(data.getId(), data.copy()));
		CONTROLLER.load();
	}
}
