package ca.teamdman.sfm.common.block;

import ca.teamdman.sfm.common.container.factory.ManagerContainerProvider;
import ca.teamdman.sfm.common.registrar.TileEntityRegistrar;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ManagerBlock extends Block {
	public ManagerBlock(final Properties props) {
		super(props);
	}

	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (!world.isRemote && false) {
			System.out.println("Finding blocks");
			ManagerTileEntity tile = (ManagerTileEntity) world.getTileEntity(pos);
			tile.getNeighbours(tile.getPos()).forEach(p -> {
				System.out.printf("%30s %20s\n", world.getBlockState(p).getBlock().getRegistryName().toString(), p.toString());
				if (!tile.isCable(p)) {
					world.setBlockState(p, Blocks.DIAMOND_BLOCK.getDefaultState());
				}
			});
		}
		if (!world.isRemote && handIn == Hand.MAIN_HAND)
			new ManagerContainerProvider(IWorldPosCallable.of(world, pos)).openGui(player);
		return ActionResultType.CONSUME;
	}

	@Override
	public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
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
