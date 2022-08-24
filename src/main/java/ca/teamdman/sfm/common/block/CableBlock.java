package ca.teamdman.sfm.common.block;

import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.cablenetwork.ICable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

public class CableBlock extends Block implements ICable {

    public CableBlock() {
        super(Block.Properties
                      .of(Material.METAL)
                      .destroyTime(1f)
                      .sound(SoundType.METAL));
    }

    @Override
    public void neighborChanged(
            BlockState state,
            Level level,
            BlockPos pos,
            Block block,
            BlockPos fromPos,
            boolean movedByPiston
    ) {
        if (!(level instanceof ServerLevel)) return;
        // reassess neighbours of the CABLE's position
        CableNetworkManager
                .getOrRegisterNetwork(level, pos)
                .ifPresent(network -> network.rebuildAdjacentInventories(pos));
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving) {
        CableNetworkManager.getOrRegisterNetwork(world, pos);
        CableNetworkManager.printDebugInfo();
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        CableNetworkManager.unregister(level, pos);
        CableNetworkManager.printDebugInfo();
    }
}
