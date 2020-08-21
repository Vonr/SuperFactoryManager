package ca.teamdman.sfm.common.block;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public interface ICable {
	default boolean isCableEnabled(
		BlockState state, IWorldReader worldIn, BlockPos pos
	) {
		return true;
	}
}
