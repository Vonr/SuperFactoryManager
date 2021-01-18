package ca.teamdman.sfm.common.tile.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.flow.data.TimerTriggerFlowData;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import net.minecraft.world.World;

public class FlowExecutor {

	private final ManagerTileEntity TILE;
	private final Deque<ExecutionFrame> FRAMES = new ArrayDeque<>();
	int count = 0;

	public FlowExecutor(ManagerTileEntity TILE) {
		this.TILE = TILE;
	}

	public void tick() {
		World world = TILE.getWorld();
		if (world == null) {
			return;
		}
		TILE.getFlowDataContainer().get(TimerTriggerFlowData.class)
			.filter(t -> world.getGameTime() % t.interval == 0)
			.map(t -> new ExecutionFrame(TILE, t))
			.forEach(FRAMES::add);

		while (!FRAMES.isEmpty()) {
			ExecutionFrame frame = FRAMES.pop();
			List<ExecutionFrame> next = frame.step();
			FRAMES.addAll(next);
		}
			CableNetworkManager.getOrRegisterNetwork(world, TILE.getPos())
				.ifPresent(network -> {
					SFM.LOGGER.debug(
						SFMUtil.getMarker(FlowExecutor.class),
						"Manager found {} tiles",
						network.getInventories().size()
					);
				});
	}

}
