package ca.teamdman.sfm.common.block;

import ca.teamdman.sfm.common.blockentity.PrintingPressBlockEntity;
import ca.teamdman.sfm.common.registry.SFMBlockEntities;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.commons.lang3.NotImplementedException;

public class PrintingPressBlock extends BaseEntityBlock implements EntityBlock {

    public PrintingPressBlock() {
        super(BlockBehaviour.Properties.of().strength(5.0F, 6.0F).noOcclusion());
        this.registerDefaultState(this.defaultBlockState());
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return SFMBlockEntities.PRINTING_PRESS_BLOCK_ENTITY
                .get()
                .create(pos, state);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(
            BlockState pState,
            Level pLevel,
            BlockPos pPos,
            Block pBlock,
            BlockPos pFromPos,
            boolean pIsMoving
    ) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
        if (!pLevel.isClientSide
            && pFromPos.getY() == pPos.getY() + 1
            && pLevel.getBlockState(pFromPos).getBlock() == Blocks.PISTON_HEAD
            && pLevel.getBlockEntity(pPos) instanceof PrintingPressBlockEntity blockEntity) {
            blockEntity.performPrint();
        }
    }

    @Override
    protected MapCodec<WaterTankBlock> codec() {
        throw new NotImplementedException("This isn't used until 1.20.5 apparently");
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hit
    ) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof PrintingPressBlockEntity blockEntity) {
            var stack = player.getItemInHand(hand);
            player.setItemInHand(hand, blockEntity.acceptStack(stack));
        }
        return InteractionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (!pState.is(pNewState.getBlock())) {
            BlockEntity blockentity = pLevel.getBlockEntity(pPos);
            if (blockentity instanceof PrintingPressBlockEntity blockEntity) {
                for (ItemStack itemStack : blockEntity.getStacksToDrop()) {
                    Containers.dropItemStack(pLevel, pPos.getX(), pPos.getY(), pPos.getZ(), itemStack);
                }
                pLevel.updateNeighbourForOutputSignal(pPos, this);
            }

            super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
        }
    }


}
