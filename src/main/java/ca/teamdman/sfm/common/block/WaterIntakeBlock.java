package ca.teamdman.sfm.common.block;

import ca.teamdman.sfm.common.tile.WaterIntakeTileEntity;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class WaterIntakeBlock extends Block {

	public static final BooleanProperty IN_WATER = BooleanProperty.create(
		"in_water");

	public WaterIntakeBlock() {
		super(AbstractBlock.Properties
			.create(Material.PISTON)
			.hardnessAndResistance(3f, 6f)
			.sound(SoundType.METAL));
		setDefaultState(getStateContainer()
			.getBaseState()
			.with(IN_WATER, false));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return getDefaultState().with(
			IN_WATER,
			isActive(context.getWorld(), context.getPos())
		);
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(IN_WATER);
	}

	public boolean isActive(IWorldReader world, BlockPos pos) {
		int neighbourWaterCount = 0;
		for (Direction direction : Direction.values()) {
			FluidState state = world.getFluidState(pos.offset(direction));
			if (state.isSource() && state.isTagged(FluidTags.WATER)) {
				if (++neighbourWaterCount == 2) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void neighborChanged(
		BlockState state,
		World world,
		BlockPos pos,
		Block blockIn,
		BlockPos fromPos,
		boolean isMoving
	) {
		if (world.isRemote) return;
		boolean isActive = isActive(world, pos);
		if (state.get(IN_WATER) != isActive) {
			BlockState newState = getDefaultState().with(IN_WATER, isActive);
			world.setBlockState(
				pos,
				newState,
				1 | 2
			);
		}
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return state.get(IN_WATER);
	}

	@Nullable
	@Override
	public WaterIntakeTileEntity createTileEntity(
		BlockState state, IBlockReader world
	) {
		return new WaterIntakeTileEntity();
	}
}
