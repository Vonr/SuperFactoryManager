package ca.teamdman.sfm.block;

import ca.teamdman.sfm.registrar.TileEntityRegistrar;
import ca.teamdman.sfm.tile.ManagerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ManagerBlock extends Block {
	public ManagerBlock(final Properties props) {
		super(props);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (!worldIn.isRemote && handIn == Hand.MAIN_HAND) {
			TileEntity tile = worldIn.getTileEntity(pos);
			if (tile instanceof ManagerTileEntity) {

				player.openContainer((INamedContainerProvider) tile);
			}
		}
		return true;
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
