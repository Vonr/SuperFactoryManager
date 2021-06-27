/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.block;

import ca.teamdman.sfm.common.registrar.SFMTiles;
import ca.teamdman.sfm.common.tile.CrafterTileEntity;
import ca.teamdman.sfm.common.util.SFMUtil;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class CrafterBlock extends Block {

	public CrafterBlock() {
		super(Block.Properties
			.of(Material.METAL)
			.strength(3F, 6F)
			.sound(SoundType.METAL));
	}

	@SuppressWarnings("deprecation")
	@Override
	public ActionResultType use(
		BlockState state,
		World world,
		BlockPos pos,
		PlayerEntity player,
		Hand handIn,
		BlockRayTraceResult hit
	) {
		if (!world.isClientSide && handIn == Hand.MAIN_HAND) {
			SFMUtil
				.getServerTile(IWorldPosCallable.create(world, pos), CrafterTileEntity.class)
				.ifPresent(tile -> NetworkHooks.openGui((ServerPlayerEntity) player, tile, data -> {
					data.writeBlockPos(tile.getBlockPos());
				}));
		}

		return ActionResultType.CONSUME;
	}

	@Override
	public boolean hasTileEntity(final BlockState state) {
		return true;
	}

	@Nullable
	@Override
	public TileEntity createTileEntity(final BlockState state, final IBlockReader world) {
		return ((TileEntityType<?>) SFMTiles.CRAFTER.get()).create();
	}

}
