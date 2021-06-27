package ca.teamdman.sfm.common.block;

import ca.teamdman.sfm.common.registrar.SFMContainers;
import ca.teamdman.sfm.common.registrar.SFMTiles;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemTier;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class WorkstationBlock extends CableBlock {

	public WorkstationBlock() {
		super(AbstractBlock.Properties
			.of(Material.PISTON)
			.strength(3F, 4F)
			.harvestTool(ToolType.AXE)
			.harvestLevel(ItemTier.WOOD.getLevel())
			.sound(SoundType.WOOD));
	}

	@Override
	public ActionResultType use(
		BlockState state,
		World worldIn,
		BlockPos pos,
		PlayerEntity player,
		Hand handIn,
		BlockRayTraceResult hit
	) {
		if (!worldIn.isClientSide) {
			SFMContainers.WORKSTATION.get().openGui(player, worldIn, pos);
		}
		return ActionResultType.CONSUME;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(
		BlockState state, IBlockReader world
	) {
		return SFMTiles.WORKSTATION.get().create();
	}
}
