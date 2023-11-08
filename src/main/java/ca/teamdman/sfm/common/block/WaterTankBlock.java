package ca.teamdman.sfm.common.block;

import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.blockentity.WaterTankBlockEntity;
import ca.teamdman.sfm.common.registry.SFMBlockEntities;
import ca.teamdman.sfm.common.util.SFMUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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

import java.util.List;
import java.util.Optional;

@SuppressWarnings("deprecation")

public class WaterTankBlock extends BaseEntityBlock implements EntityBlock, BucketPickup, LiquidBlockContainer {
    public static final BooleanProperty      IN_WATER = BooleanProperty.create("in_water");


    public WaterTankBlock() {
        super(BlockBehaviour.Properties.of(Material.PISTON).destroyTime(2).sound(SoundType.WOOD));
        registerDefaultState(getStateDefinition().any().setValue(IN_WATER, false));
    }


    @Override
    @SuppressWarnings("deprecation")
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
        for (Direction direction : Direction.values()) {
            recount(pLevel, pPos.offset(direction.getNormal()));
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        for (Direction direction : Direction.values()) {
            recount(pLevel, pPos.offset(direction.getNormal()));
        }
    }

    @Override
    public void appendHoverText(
            ItemStack pStack,
            @Nullable BlockGetter pLevel,
            List<Component> pTooltip,
            TooltipFlag pFlag
    ) {
        pTooltip.add(Constants.LocalizationKeys.WATER_TANK_ITEM_TOOLTIP_1
                             .getComponent()
                             .withStyle(ChatFormatting.GRAY));
        pTooltip.add(Constants.LocalizationKeys.WATER_TANK_ITEM_TOOLTIP_2
                             .getComponent()
                             .withStyle(ChatFormatting.GRAY));
    }

    public void recount(Level level, BlockPos pos) {
        if (!(level.getBlockEntity(pos) instanceof WaterTankBlockEntity be)) return;
        var tanks = SFMUtils.getRecursiveStream((current, next, results) -> {
            results.accept(current);
            for (var d : Direction.values()) {
                var offset = current.getBlockPos().offset(d.getNormal());
                if (!(level.getBlockEntity(offset) instanceof WaterTankBlockEntity blockEntity)) continue;
                next.accept(blockEntity);
            }
        }, be).toList();
        tanks.forEach(t -> t.setConnectedCount(tanks.size()));
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
    @SuppressWarnings("deprecation")
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
