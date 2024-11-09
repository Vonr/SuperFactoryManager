package ca.teamdman.sfm.common.block;

import ca.teamdman.sfm.common.blockentity.TunneledManagerBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.item.DiskItem;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import ca.teamdman.sfm.common.program.ProgramLinter;
import ca.teamdman.sfm.common.registry.SFMBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

import javax.annotation.Nullable;

public class TunneledManagerBlock extends ManagerBlock {
    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(
            BlockState state,
            Level level,
            BlockPos pos,
            Block block,
            BlockPos neighbourPos,
            boolean movedByPiston
    ) {
        if (!(level.getBlockEntity(pos) instanceof TunneledManagerBlockEntity mgr)) return;
        if (!(level instanceof ServerLevel)) return;
        { // check redstone for triggers
            var isPowered = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
            var debounce = state.getValue(TRIGGERED);
            if (isPowered && !debounce) {
                mgr.trackRedstonePulseUnprocessed();
                level.setBlock(pos, state.setValue(TRIGGERED, true), 4);
            } else if (!isPowered && debounce) {
                level.setBlock(pos, state.setValue(TRIGGERED, false), 4);
            }
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return SFMBlockEntities.TUNNELED_MANAGER_BLOCK_ENTITY
                .get()
                .create(pos, state);
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
        if (level.getBlockEntity(pos) instanceof TunneledManagerBlockEntity manager && player instanceof ServerPlayer sp) {
            // update warnings on disk as we open the gui
            manager
                    .getDisk()
                    .ifPresent(disk -> manager
                            .getProgram()
                            .ifPresent(program -> DiskItem.setWarnings(disk, ProgramLinter.gatherWarnings(program,
                                                                                                          LabelPositionHolder.from(disk), manager))));
            NetworkHooks.openScreen(sp, manager, buf -> ManagerContainerMenu.encode(manager, buf));
            return InteractionResult.CONSUME;
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type
    ) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, SFMBlockEntities.TUNNELED_MANAGER_BLOCK_ENTITY.get(), TunneledManagerBlockEntity::serverTick);
    }
}
