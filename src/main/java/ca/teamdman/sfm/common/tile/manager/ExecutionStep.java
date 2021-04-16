package ca.teamdman.sfm.common.tile.manager;

import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.ItemMovementRuleFlowData;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExecutionStep {

	public final List<ItemMovementRuleFlowData> INPUTS = new ArrayList<>();
	public final ManagerTileEntity TILE;
	public final FlowData CURRENT;
	public final ExecutionState STATE;

	public ExecutionStep(
		ManagerTileEntity tile,
		FlowData current,
		ExecutionState state
	) {
		this.TILE = tile;
		this.CURRENT = current;
		this.STATE = state;
	}

	public List<ExecutionStep> step() {
		BasicFlowDataContainer container = TILE.getFlowDataContainer();
		CURRENT.execute(this);
		return CURRENT.getNextUsingRelationships(container)
			.map(this::fork)
			.collect(Collectors.toList());
	}


	/**
	 * When multiple paths lead from a node, fork execution to preserve state
	 *
	 * @return New execution frame with a snapshot of the inputs
	 */
	public ExecutionStep fork(FlowData next) {
		ExecutionStep other = new ExecutionStep(TILE, next, STATE);
		other.INPUTS.addAll(INPUTS);
		return other;
	}
}
