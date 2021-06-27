package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.tiletypematcher;

import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.BlockPosPickerFlowComponent;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.TileTypeMatcherFlowData;
import net.minecraft.util.math.BlockPos;

class Picker extends BlockPosPickerFlowComponent {

	private final TileTypeMatcherFlowData data;
	private final ManagerFlowController PARENT;

	public Picker(
		TileTypeMatcherFlowData data,
		ManagerFlowController parent,
		Position position
	) {
		super(position);
		this.data = data;
		this.PARENT = parent;
		rebuildSuggestions();
	}

	public void rebuildSuggestions() {
	}

	@Override
	public void onPicked(BlockPos pos) {

	}

	/*public void rebuildSuggestions() {
		// gather list of existing selected positions for this
		BasicFlowDataContainer container = PARENT.SCREEN.getFlowDataContainer();
		Set<BlockPos> ignore = container.get(ItemMovementRuleFlowData.class)
			.filter(data -> data.tileMatcherIds.contains(this.data.getId()))
			.flatMap(data -> data.tileMatcherIds.stream())
			.map(id -> container.get(id, TileTypeMatcherFlowData.class))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.map(data -> data.position)
			.collect(Collectors.toSet());

		// add suggestions for positions not already selected
		CableNetworkManager
			.getOrRegisterNetwork(PARENT.SCREEN.getMenu().getSource())
			.ifPresent(net -> rebuildFromNetwork(
				net,
				p -> !ignore.contains(p)
			));
	}

	@Override
	public void onPicked(BlockPos pos) {
		if (!data.position.equals(pos)) {
			data.position = pos;
			PARENT.SCREEN.sendFlowDataToServer(data);
		}
		setVisibleAndEnabled(false);
	}*/
}
