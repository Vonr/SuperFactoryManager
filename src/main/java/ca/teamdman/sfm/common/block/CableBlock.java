/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.block;

import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.cablenetwork.ICable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
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

    public CableBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void onNeighborChange(
            BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor
    ) {
        if (world instanceof ServerLevel) {
            CableNetworkManager
                    .getOrRegisterNetwork(((Level) world), pos)
                    .ifPresent(network -> network.rebuildAdjacentInventories(pos));
        }
    }

    @Override
    public void onPlace(
            BlockState state, Level world, BlockPos pos, BlockState oldState, boolean isMoving
    ) {
        CableNetworkManager.getOrRegisterNetwork(world, pos);
        CableNetworkManager.printDebugInfo();
    }

    @Override
    public void onRemove(
            BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving
    ) {
        super.onRemove(state, level, pos, newState, isMoving);
        CableNetworkManager.unregister(level, pos);
        CableNetworkManager.printDebugInfo();
    }
}
