package ca.teamdman.sfm.client.gui.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.gui.core.BaseContainerScreen;
import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.flowdata.FlowData;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.HashMap;
import java.util.UUID;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class ManagerScreen extends BaseContainerScreen<ManagerContainer> {

	public final HashMap<UUID, FlowData> DATAS = new HashMap<>();
	public final ManagerFlowController CONTROLLER;
	public final Marker MARKER = MarkerManager.getMarker(getClass().getSimpleName());

	public ManagerScreen(ManagerContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, 512, 256, inv, name);
		CONTROLLER = new ManagerFlowController(this);
		reloadFromManagerTileEntity();
	}

	@Override
	public boolean onScaledMouseClicked(int mx, int my, int button) {
		return CONTROLLER.mousePressed(mx, my, button);
	}

	@Override
	public boolean onScaledMouseReleased(int mx, int my, int button) {
		return CONTROLLER.mouseReleased(mx, my, button);
	}

	@Override
	public boolean onScaledMouseDragged(int mx, int my, int button, int dmx, int dmy) {
		return CONTROLLER.mouseDragged(mx, my, button, dmx, dmy);
	}

	@Override
	public void draw(MatrixStack matrixStack, int mx, int my, float partialTicks) {
		super.draw(matrixStack, mx, my, partialTicks);
		CONTROLLER.draw(this, matrixStack, mx, my, partialTicks);
	}

	public void reloadFromManagerTileEntity() {
		SFM.LOGGER
			.debug(MARKER, "Loading {} data entries from tile", CONTAINER.getSource().data.size());
		DATAS.clear();
		CONTAINER.getSource().data.values().forEach(data -> DATAS.put(data.getId(), data.copy()));
		CONTROLLER.loadFromScreenData();
	}
}
