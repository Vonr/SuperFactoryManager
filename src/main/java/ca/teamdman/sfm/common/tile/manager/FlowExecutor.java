package ca.teamdman.sfm.common.tile.manager;

import ca.teamdman.sfm.common.flow.data.TimerTriggerFlowData;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import net.minecraft.world.World;

public class FlowExecutor {

	private final ManagerTileEntity TILE;
	private final Deque<ExecutionStep> FRAMES = new ArrayDeque<>();
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
			.map(t -> new ExecutionStep(TILE, t, state))
			.forEach(FRAMES::add);

		while (!FRAMES.isEmpty()) {
			ExecutionStep frame = FRAMES.pop();
			List<ExecutionStep> next = frame.step();
			FRAMES.addAll(next);
		}
	}

}
