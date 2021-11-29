package ca.teamdman.sfm.common.block;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.registry.SFMBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.material.Material;

import javax.annotation.Nullable;

public class ManagerBlock extends CableBlock implements EntityBlock {
    public ManagerBlock() {
        super(BlockBehaviour.Properties
                      .of(Material.PISTON)
                      .destroyTime(2)
                      .sound(SoundType.METAL));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return SFMBlockEntities.MANAGER_BLOCK_ENTITY
                .get()
                .create(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, SFMBlockEntities.MANAGER_BLOCK_ENTITY.get(), ManagerBlockEntity::serverTick);
    }
//
//    @Nullable
//    @Override
//    public <T extends BlockEntity> GameEventListener getListener(Level level, T blockEntity) {
//        return EntityBlock.super.getListener(level, blockEntity);
//    }
}
