package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.flow.core.BaseContainerScreen;
import ca.teamdman.sfm.client.gui.flow.impl.manager.ManagerFlowController;
import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.flowdata.core.FlowData;
import ca.teamdman.sfm.common.flowdata.core.FlowDataHolder;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ManagerScreen extends BaseContainerScreen<ManagerContainer> implements FlowDataHolder {

	public final HashMap<UUID, FlowData> DATAS = new HashMap<>();
	public final ManagerFlowController CONTROLLER;

	public ManagerScreen(ManagerContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, 512, 256, inv, name);
		CONTROLLER = new ManagerFlowController(this);
		reloadFromManagerTileEntity();
	}

	@Override
	public Stream<FlowData> getData() {
		return DATAS.values().stream();
	}

	@Override
	public void removeData(UUID id) {
		DATAS.remove(id);
	}

	@Override
	public void clearData() {
		DATAS.clear();
	}

	@Override
	public void addData(FlowData data) {
		DATAS.put(data.getId(), data);
	}

	@Override
	public Optional<FlowData> getData(UUID id) {
		return Optional.ofNullable(DATAS.get(id));
	}

	@Override
	public boolean mouseClickedScaled(int mx, int my, int button) {
		return CONTROLLER.mousePressed(mx, my, button);
	}

	@Override
	public boolean keyPressedScaled(int keyCode, int scanCode, int modifiers, int mx, int my) {
		return CONTROLLER.keyPressed(keyCode, scanCode, modifiers, mx, my);
	}

	@Override
	public boolean keyReleasedScaled(int keyCode, int scanCode, int modifiers, int mx, int my) {
		return CONTROLLER.keyReleased(keyCode, scanCode, modifiers, mx, my);
	}

	@Override
	public boolean mouseReleasedScaled(int mx, int my, int button) {
		return CONTROLLER.mouseReleased(mx, my, button);
	}

	@Override
	public boolean onMouseDraggedScaled(int mx, int my, int button, int dmx, int dmy) {
		return CONTROLLER.mouseDragged(mx, my, button, dmx, dmy);
	}

	@Override
	public boolean mouseScrolledScaled(int mx, int my, double scroll) {
		return CONTROLLER.mouseScrolled(mx, my, scroll);
	}

	@Override
	public void drawScaled(MatrixStack matrixStack, int mx, int my, float partialTicks) {
		super.drawScaled(matrixStack, mx, my, partialTicks);
		CONTROLLER.draw(this, matrixStack, mx, my, partialTicks);
	}

	public void reloadFromManagerTileEntity() {
		SFM.LOGGER
			.debug(SFMUtil.getMarker(getClass()), "Loading {} data entries from tile",
				CONTAINER.getSource().getDataCount()
			);
		DATAS.clear();
		CONTAINER.getSource().getData().forEach(data -> DATAS.put(data.getId(), data.copy()));
		CONTROLLER.onDataChange();
	}
}
