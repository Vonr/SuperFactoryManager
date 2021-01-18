package ca.teamdman.sfm.common.tile.manager;

import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData;
import ca.teamdman.sfm.common.flow.data.TileInputFlowData;
import ca.teamdman.sfm.common.flow.data.TileOutputFlowData;
import java.util.ArrayList;
import java.util.List;

public class ExecutionFrame {

	private final List<ItemStackTileEntityRuleFlowData> INPUTS = new ArrayList<>();
	private final ManagerTileEntity TILE;
	private final FlowData CURRENT;

	public ExecutionFrame(ManagerTileEntity tile, FlowData current) {
		this.TILE = tile;
		this.CURRENT = current;
	}

	/**
	 * When multiple paths lead from a node, fork execution to preserve state
	 * @return New execution frame with a snapshot of the inputs
	 */
	public ExecutionFrame fork(FlowData next) {
		ExecutionFrame other = new ExecutionFrame(TILE, next);
		other.INPUTS.addAll(INPUTS);
		return other;
	}

	public List<ExecutionFrame> step() {
		ArrayList<ExecutionFrame> next = new ArrayList<>();
		if (CURRENT instanceof TileInputFlowData) {

		} else if (CURRENT instanceof TileOutputFlowData) {

		}
		return next;
	}
}
