package ca.teamdman.sfm.common.tile.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.util.SFMUtil;

public class FlowExecutor {

	private final ManagerTileEntity TILE;
	int count = 0;

	public FlowExecutor(ManagerTileEntity TILE) {
		this.TILE = TILE;
	}

	public void tick() {
		count++;
		if (count%20==0)
		CableNetworkManager.getOrRegisterNetwork(TILE.getWorld(), TILE.getPos())
			.ifPresent(network -> {
				SFM.LOGGER.debug(
					SFMUtil.getMarker(getClass()),
					"Manager found {} tiles",
					network.getInventories().size()
				);
			});
	}
}
