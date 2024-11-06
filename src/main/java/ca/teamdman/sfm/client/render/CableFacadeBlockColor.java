package ca.teamdman.sfm.client.render;

import ca.teamdman.sfm.common.blockentity.CableFacadeBlockEntity;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CableFacadeBlockColor implements BlockColor {

    @Override
    public int getColor(BlockState blockState, @Nullable BlockAndTintGetter blockAndTintGetter, @Nullable BlockPos blockPos, int tintIndex) {
        if (blockAndTintGetter == null || blockPos == null) return -1;

        CableFacadeBlockEntity cableFacadeBlockEntity = (CableFacadeBlockEntity) blockAndTintGetter.getBlockEntity(blockPos);
        if (cableFacadeBlockEntity == null) return -1;
        BlockState facadeState = cableFacadeBlockEntity.getFacadeState();
        if (facadeState.getBlock() == SFMBlocks.CABLE_FACADE_BLOCK.get()) return -1;

        return Minecraft.getInstance().getBlockColors().getColor(facadeState, blockAndTintGetter, blockPos, tintIndex);
    }
}
