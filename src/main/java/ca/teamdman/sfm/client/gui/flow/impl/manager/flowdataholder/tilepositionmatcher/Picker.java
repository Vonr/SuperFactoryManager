package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.tilepositionmatcher;

import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.BlockPosPickerFlowComponent;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.ItemRuleFlowData;
import ca.teamdman.sfm.common.flow.data.TilePositionMatcherFlowData;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.util.math.BlockPos;

class Picker extends BlockPosPickerFlowComponent {

	private final TilePositionMatcherFlowData data;
	private final ManagerFlowController PARENT;

	public Picker(
		TilePositionMatcherFlowData data,
		ManagerFlowController parent,
		Position position
	) {
		super(position);
		this.data = data;
		this.PARENT = parent;
		rebuildSuggestions();
	}

	public void rebuildSuggestions() {
		// gather list of existing selected positions for this
		BasicFlowDataContainer container = PARENT.SCREEN.getFlowDataContainer();
		Set<BlockPos> ignore = container.get(ItemRuleFlowData.class)
			.filter(data -> data.tileMatcherIds.contains(data.getId()))
			.flatMap(data -> data.tileMatcherIds.stream())
			.map(id -> container.get(id, TilePositionMatcherFlowData.class))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.map(data -> data.position)
			.collect(Collectors.toSet());

		// add suggestions for positions not already selected
		CableNetworkManager
			.getOrRegisterNetwork(PARENT.SCREEN.getContainer().getSource())
			.ifPresent(net -> rebuildFromNetwork(net, p -> !ignore.contains(p)));
	}

	@Override
	public void onPicked(BlockPos pos) {
		if (!data.position.equals(pos)) {
			data.position = pos;
			PARENT.SCREEN.sendFlowDataToServer(data);
		}
		setVisibleAndEnabled(false);
	}
}
