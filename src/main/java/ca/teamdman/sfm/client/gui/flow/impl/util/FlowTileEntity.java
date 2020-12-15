/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.common.flow.data.core.Position;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class FlowTileEntity extends FlowItemStack {
	public final TileEntity TILE;
	public FlowTileEntity(TileEntity tile, Position pos) {
		super(new ItemStack(tile.getBlockState().getBlock()), pos);
		this.TILE = tile;
	}
}
