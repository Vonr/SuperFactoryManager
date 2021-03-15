package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.tilepositionmatcher;

import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowBlockPosPicker;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.TilePositionMatcherFlowData;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;

public class TilePositionMatcherFlowComponent extends FlowContainer implements
	FlowDataHolder<TilePositionMatcherFlowData> {

	protected final ManagerFlowController PARENT;
	protected final FlowBlockPosPicker PICKER;

	private TilePositionMatcherFlowData data;

	public TilePositionMatcherFlowComponent(
		ManagerFlowController parent,
		TilePositionMatcherFlowData data
	) {
		super(
			new Position(0, 0),
			new Size(100, 24)
		);
		this.PARENT = parent;
		this.data = data;

		addChild(new PickerActivator(this, new Position(0, 0)));

		PICKER = new MyFlowBlockPosPicker(data, parent, new Position(25,0));
		PICKER.setVisibleAndEnabled(false);
		addChild(PICKER);

		parent.SCREEN.getFlowDataContainer().addObserver(new FlowDataHolderObserver<>(
			TilePositionMatcherFlowData.class, this
		));
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
