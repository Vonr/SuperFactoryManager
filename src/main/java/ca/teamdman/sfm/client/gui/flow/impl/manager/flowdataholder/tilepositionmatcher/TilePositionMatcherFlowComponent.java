package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.tilepositionmatcher;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.BlockPosPickerFlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.TilePositionMatcherFlowData;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import com.mojang.blaze3d.matrix.MatrixStack;

public class TilePositionMatcherFlowComponent extends FlowContainer implements
	FlowDataHolder<TilePositionMatcherFlowData> {

	protected final ManagerFlowController PARENT;
	protected final BlockPosPickerFlowComponent PICKER;

	private TilePositionMatcherFlowData data;

	public TilePositionMatcherFlowComponent(
		ManagerFlowController parent,
		TilePositionMatcherFlowData data
	) {
		super(
			new Position(0, 0),
			new Size(100, 26)
		);
		this.PARENT = parent;
		this.data = data;

		addChild(new PickerActivator(this, new Position(3, 3)));

		PICKER = new MyFlowBlockPosPicker(data, parent, new Position(25,0));
		PICKER.setVisibleAndEnabled(false);
		addChild(PICKER);

		parent.SCREEN.getFlowDataContainer().addObserver(new FlowDataHolderObserver<>(
			TilePositionMatcherFlowData.class, this
		));
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		screen.clearRect(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getHeight()
		);
		screen.drawRect(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getHeight(),
			CONST.PANEL_BACKGROUND_NORMAL
		);
		screen.drawBorder(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getHeight(),
			1,
			CONST.PANEL_BACKGROUND_DARK
		);
		super.draw(screen, matrixStack, mx, my, deltaTime);
	}

	@Override
	public TilePositionMatcherFlowData getData() {
		return data;
	}

	@Override
	public void setData(TilePositionMatcherFlowData data) {
		this.data = data;
	}

	@Override
	public boolean isVisible() {
		return data.open;
	}


	@Override
	public boolean isEnabled() {
		return isVisible();
	}

	@Override
	public void setVisible(boolean visible) {
		if (data.open != visible) {
			data.open = visible;
			PARENT.SCREEN.sendFlowDataToServer(data);
		}
	}


	@Override
	public void setEnabled(boolean enabled) {
		setVisible(enabled);
	}

	@Override
	public int getZIndex() {
		return super.getZIndex() + 120;
	}

}
