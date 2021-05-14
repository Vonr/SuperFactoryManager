/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.block;

import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.registrar.SFMTiles;
import ca.teamdman.sfm.common.tile.manager.ManagerTileEntity;
import ca.teamdman.sfm.common.util.SFMUtil;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class ManagerBlock extends CableBlock {

	public ManagerBlock() {
		super(Block.Properties
			.create(Material.IRON)
			.hardnessAndResistance(5F, 6F)
			.sound(SoundType.METAL));
	}

	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType onBlockActivated(
		BlockState state,
		World world,
		BlockPos pos,
		PlayerEntity player,
		Hand handIn,
		BlockRayTraceResult hit
	) {
		if (!world.isRemote && handIn == Hand.MAIN_HAND) {
			SFMUtil
				.getServerTile(IWorldPosCallable.of(world, pos), ManagerTileEntity.class)
				.ifPresent(tile -> ManagerContainer.openGui(((ServerPlayerEntity) player), tile));
		}
		return ActionResultType.CONSUME;
	}

	@Override
	public void onBlockHarvested(
		World worldIn, BlockPos pos, BlockState state, PlayerEntity player
	) {
		super.onBlockHarvested(worldIn, pos, state, player);

		SFMUtil
			.getServerTile(IWorldPosCallable.of(worldIn, pos), ManagerTileEntity.class)
			.ifPresent(ManagerTileEntity::closeGuiForAllListeners);
	}

	@Override
	public boolean hasTileEntity(final BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return SFMTiles.MANAGER.get().create();
	}
}
