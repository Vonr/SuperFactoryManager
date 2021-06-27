package ca.teamdman.sfm.common.block;

import ca.teamdman.sfm.common.tile.WaterIntakeTileEntity;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class WaterIntakeBlock extends Block implements IBucketPickupHandler {

	public static final BooleanProperty IN_WATER = BooleanProperty.create(
		"in_water");

	public WaterIntakeBlock() {
		super(AbstractBlock.Properties
			.of(Material.PISTON)
			.strength(3f, 6f)
			.sound(SoundType.METAL));
		
		registerDefaultState(getStateDefinition()
			.any()
			.setValue(IN_WATER, false));
	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return defaultBlockState().setValue(
			IN_WATER,
			isActive(context.getLevel(), context.getClickedPos())
		);
	}

	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		builder.add(IN_WATER);
	}

	public boolean isActive(IWorldReader world, BlockPos pos) {
		int neighbourWaterCount = 0;
		for (Direction direction : Direction.values()) {
			FluidState state = world.getFluidState(pos.relative(direction));
			if (state.isSource() && state.is(FluidTags.WATER)) {
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
		if (world.isClientSide) return;
		boolean isActive = isActive(world, pos);
		if (state.getValue(IN_WATER) != isActive) {
			BlockState newState = defaultBlockState().setValue(IN_WATER, isActive);
			world.setBlock(
				pos,
				newState,
				1 | 2
			);
		}
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return state.getValue(IN_WATER);
	}

	@Nullable
	@Override
	public WaterIntakeTileEntity createTileEntity(
		BlockState state, IBlockReader world
	) {
		return new WaterIntakeTileEntity();
	}

	@Override
	public Fluid takeLiquid(
		IWorld worldIn, BlockPos pos, BlockState state
	) {
		return state.getValue(IN_WATER) ? Fluids.WATER : Fluids.EMPTY;
	}
}
