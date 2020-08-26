package ca.teamdman.sfm.client.gui.impl;

import ca.teamdman.sfm.common.flowdata.Position;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class FlowTileEntity extends FlowItemStack {
	public FlowTileEntity(TileEntity tile, Position pos) {
		super(new ItemStack(tile.getBlockState().getBlock()), pos);
	}
}
