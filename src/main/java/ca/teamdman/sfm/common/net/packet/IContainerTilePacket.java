package ca.teamdman.sfm.common.net.packet;

import net.minecraft.util.math.BlockPos;

public interface IContainerTilePacket extends IWindowIdProvider {
	/**
	 * @return The position of the tile entity to find
	 */
	BlockPos getTilePosition();
}
