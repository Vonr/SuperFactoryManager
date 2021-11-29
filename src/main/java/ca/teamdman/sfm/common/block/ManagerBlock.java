package ca.teamdman.sfm.common.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

public class ManagerBlock extends Block {
	public ManagerBlock() {
		super(BlockBehaviour.Properties.of(Material.PISTON)
					  .destroyTime(2)
					  .sound(SoundType.METAL));
	}
}
