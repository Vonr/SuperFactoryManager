package ca.teamdman.sfm.block;

import ca.teamdman.sfm.registrar.TileEntityRegistrar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class ManagerBlock extends Block {
	public ManagerBlock(final Properties props) {
		super(props);
	}

	@Override
	public boolean hasTileEntity(final BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return TileEntityRegistrar.Tiles.MANAGER.create();
	}
}
