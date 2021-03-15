package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.tilepositionmatcher;

import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowBlockPosPicker;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.TilePositionMatcherFlowData;
import net.minecraft.util.math.BlockPos;

class MyFlowBlockPosPicker extends FlowBlockPosPicker {

	private final TilePositionMatcherFlowData data;
	private final ManagerFlowController parent;

	public MyFlowBlockPosPicker(
		TilePositionMatcherFlowData data,
		ManagerFlowController parent,
		Position position
	) {
		super(position);
		this.data = data;
		this.parent = parent;
	}

	@Override
	public void onPicked(BlockPos pos) {
		if (!data.position.equals(pos)) {
			data.position = pos;
			parent.SCREEN.sendFlowDataToServer(data);
		}
		setVisibleAndEnabled(false);
	}
}
