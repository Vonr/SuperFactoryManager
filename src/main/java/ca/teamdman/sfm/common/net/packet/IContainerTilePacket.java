/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.net.packet;

import net.minecraft.util.math.BlockPos;

public interface IContainerTilePacket extends IWindowIdProvider {
	/**
	 * @return The position of the tile entity to find
	 */
	BlockPos getTilePosition();
}
