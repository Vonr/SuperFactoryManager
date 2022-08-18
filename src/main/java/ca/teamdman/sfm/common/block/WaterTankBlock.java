package ca.teamdman.sfm.common.block;

import ca.teamdman.sfm.common.registry.SFMBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class WaterTankBlock extends BaseEntityBlock implements EntityBlock, BucketPickup, LiquidBlockContainer {
    public static final BooleanProperty IN_WATER = BooleanProperty.create("in_water");

    public WaterTankBlock() {
        super(BlockBehaviour.Properties.of(Material.PISTON).destroyTime(2).sound(SoundType.ANVIL));
        registerDefaultState(getStateDefinition().any().setValue(IN_WATER, false));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(IN_WATER);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return SFMBlockEntities.WATER_TANK_BLOCK_ENTITY.get().create(pos, state);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(
                IN_WATER,
                isActive(context.getLevel(), context.getClickedPos())
        );
    }

    public boolean isActive(LevelAccessor level, BlockPos pos) {
        int neighbourWaterCount = 0;
        for (Direction direction : Direction.values()) {
            FluidState state = level.getFluidState(pos.relative(direction));
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
            Level level,
            BlockPos pos,
            Block blockIn,
            BlockPos fromPos,
            boolean isMoving
    ) {
        if (level.isClientSide) return;
        boolean isActive = isActive(level, pos);
        if (state.getValue(IN_WATER) != isActive) {
            BlockState newState = defaultBlockState().setValue(IN_WATER, isActive);
            level.setBlock(
                    pos,
                    newState,
                    1 | 2
            );
        }
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        return state.getValue(IN_WATER) ? new ItemStack(Fluids.WATER.getBucket()) : ItemStack.EMPTY;
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Fluids.WATER.getPickupSound();
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid) {
        return fluid.isSame(Fluids.WATER);
    }

    @Override
    public boolean placeLiquid(LevelAccessor level, BlockPos pos, BlockState state, FluidState fluid) {
        return fluid.getType().isSame(Fluids.WATER);
    }
}
