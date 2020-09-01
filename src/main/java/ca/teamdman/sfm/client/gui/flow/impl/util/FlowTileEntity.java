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
