package ca.teamdman.sfm.common.tile.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.flow.data.TimerTriggerFlowData;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import net.minecraft.world.World;

public class FlowExecutor {

	private final ManagerTileEntity TILE;
	private final Deque<ExecutionFrame> FRAMES = new ArrayDeque<>();
	private int tick = 0;

	public FlowExecutor(ManagerTileEntity TILE) {
		this.TILE = TILE;
	}

	public void tick() {
		World world = TILE.getWorld();
		if (world == null || world.isRemote) {
			return;
		}
		tick++;
		ExecutionState state = new ExecutionState();
		TILE.getFlowDataContainer().get(TimerTriggerFlowData.class)
			.filter(t -> tick % t.interval == 0)
			.map(t -> new ExecutionFrame(TILE, t, state))
			.forEach(FRAMES::add);

		if (!FRAMES.isEmpty()) {
			SFM.LOGGER.debug(
				SFMUtil.getMarker(getClass()),
				"Executing trigger"
			);
		}

		while (!FRAMES.isEmpty()) {
			ExecutionFrame frame = FRAMES.pop();
			List<ExecutionFrame> next = frame.step();
			FRAMES.addAll(next);
		}
	}

}
