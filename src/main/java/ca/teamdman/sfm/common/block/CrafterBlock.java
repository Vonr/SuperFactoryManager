package ca.teamdman.sfm.common.block;

import ca.teamdman.sfm.common.container.factory.CrafterContainerProvider;
import ca.teamdman.sfm.common.registrar.TileEntityRegistrar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class CrafterBlock extends Block {
	public CrafterBlock(final Properties props) {
		super(props);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (!world.isRemote && handIn == Hand.MAIN_HAND)
			new CrafterContainerProvider(IWorldPosCallable.of(world, pos)).openGui(player);
		return true;
	}

	@Override
	public boolean hasTileEntity(final BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return TileEntityRegistrar.Tiles.CRAFTER.create();
	}


}
