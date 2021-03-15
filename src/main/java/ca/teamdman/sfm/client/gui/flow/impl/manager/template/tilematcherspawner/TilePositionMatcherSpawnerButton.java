package ca.teamdman.sfm.client.gui.flow.impl.manager.template.tilematcherspawner;

import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemrule.ItemRuleFlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.util.ButtonLabel;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.TilePositionMatcherFlowData;
import java.util.UUID;
import net.minecraft.util.math.BlockPos;

class TilePositionMatcherSpawnerButton extends FlowIconButton {

	private final ItemRuleFlowComponent PARENT;

	public TilePositionMatcherSpawnerButton(ItemRuleFlowComponent parent) {
		super(
			ButtonLabel.PICKER_MATCHER,
			new Position()
		);
		PARENT = parent;
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		TilePositionMatcherFlowData data = new TilePositionMatcherFlowData(
			UUID.randomUUID(),
			BlockPos.ZERO,
			false
		);
		PARENT.getData().tileMatcherIds.add(data.getId());
		PARENT.CONTROLLER.SCREEN.sendFlowDataToServer(
			data,
			PARENT.getData()
		);
	}
}
