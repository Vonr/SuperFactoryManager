package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.CursorFlowData;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;

public class FlowCursor extends FlowComponent implements FlowDataHolder<CursorFlowData> {

	private final ManagerFlowController CONTROLLER;
	private CursorFlowData data;
	private int debounce = 0;

	public FlowCursor(
		ManagerFlowController CONTROLLER,
		CursorFlowData data
	) {
		super(new Position(0, 0), new Size(8, 8));
		this.CONTROLLER = CONTROLLER;
		this.data = data;
		CONTROLLER.SCREEN.getFlowDataContainer().addObserver(new FlowDataHolderObserver<>(
			this,
			CursorFlowData.class
		));
	}

	@Override
	public boolean mousePressed(int mx, int my, int button) {
		return false; // don't swallow events with our invisible cursor
	}

	@Override
	public void drawTooltip(
		BaseScreen screen,
		MatrixStack matrixStack,
		int mx,
		int my,
		float deltaTime
	) {
		// draw during tooltip step to ensure on-top
		screen.drawRect(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getHeight(),
			CONST.CURSOR
		);

		super.drawTooltip(screen, matrixStack, mx, my, deltaTime);
	}

	@Override
	public List<? extends ITextProperties> getTooltip() {
		List<ITextProperties> rtn = new ArrayList<>();
		rtn.add(new StringTextComponent(data.playerName));
		return rtn;
	}

	@Override
	public boolean isVisible() {
		return !belongsToLocalPlayer(); // only show others' cursors
	}

	@Override
	public boolean isEnabled() {
		return true; // always enabled, even though we can't see our own cursor
	}

	@Override
	public void tick() {
		if (debounce > 0) {
			debounce--;
		}
	}

	@Override
	public int getZIndex() {
		return super.getZIndex() + 32000;
	}

	@Override
	public boolean mouseMoved(int mx, int my, boolean consumed) {
		if (belongsToLocalPlayer() && debounce == 0) {
			data.position.setXY(mx, my);
			CONTROLLER.SCREEN.sendFlowDataToServer(data);
			debounce = 2; // don't spam packets!
		}
		return super.mouseMoved(mx, my, consumed) && isVisible(); // only consume event if visible
	}

	public boolean belongsToLocalPlayer() {
		ClientPlayerEntity player = CONTROLLER.SCREEN.getMinecraft().player;
		if (player == null) {
			return false;
		}
		return Objects.equals(data.getId(), player.getUniqueID());
	}

	@Override
	public CursorFlowData getData() {
		return data;
	}

	@Override
	public void setData(CursorFlowData data) {
		this.data = data;
		getPosition().setXY(data.position);
	}

}
