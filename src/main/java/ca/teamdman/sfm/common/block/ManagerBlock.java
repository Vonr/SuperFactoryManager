/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.block;

import ca.teamdman.sfm.common.container.factory.ManagerContainerProvider;
import ca.teamdman.sfm.common.registrar.TileEntityRegistrar;
import ca.teamdman.sfm.common.tile.manager.ManagerTileEntity;
import ca.teamdman.sfm.common.util.SFMUtil;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
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

	public ManagerBlock(final Properties props) {
		super(props);
	}

	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType onBlockActivated(
		BlockState state, World world, BlockPos pos,
		PlayerEntity player, Hand handIn, BlockRayTraceResult hit
	) {
		if (!world.isRemote && handIn == Hand.MAIN_HAND) {
			new ManagerContainerProvider(IWorldPosCallable.of(world, pos)).openGui(player);
		}
		return ActionResultType.CONSUME;
	}

	@Override
	public void onBlockHarvested(
		World worldIn, BlockPos pos, BlockState state,
		PlayerEntity player
	) {
		super.onBlockHarvested(worldIn, pos, state, player);

		SFMUtil.getServerTile(IWorldPosCallable.of(worldIn, pos), ManagerTileEntity.class)
			.ifPresent(manager -> {
				for (ServerPlayerEntity p : manager.getContainerListeners()
					.toArray(ServerPlayerEntity[]::new)) {
					p.closeScreen();
				}
			});
	}

	@Override
	public boolean hasTileEntity(final BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return TileEntityRegistrar.Tiles.MANAGER.create();
	}
}
