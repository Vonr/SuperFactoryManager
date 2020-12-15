/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.execution;

import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.tileentity.TileEntity;

public class ExecutionFrame {

	final ManagerTileEntity MANAGER;
	final Set<TileEntity> INPUTS;
	final Set<TileEntity> OUTPUTS;
	final FlowData DATA;

	public ExecutionFrame(
		ManagerTileEntity manager,
		Set<TileEntity> inputs,
		Set<TileEntity> outputs,
		FlowData data
	) {
		this.MANAGER = manager;
		this.INPUTS = inputs;
		this.OUTPUTS = outputs;
		this.DATA = data;
	}

	Stream<ExecutionFrame> getNextFrames() {
		return MANAGER.graph.getDescendants(DATA.getId())
			.flatMap(node -> node.outgoing.stream())
			.map(edge -> edge.TO.NODE_DATA)
			.map(data -> new ExecutionFrame(
				MANAGER,
				INPUTS,
				OUTPUTS,
				data
			));
	}
}
