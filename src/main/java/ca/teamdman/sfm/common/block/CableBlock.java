/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.block;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.util.SFMUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class CableBlock extends Block implements ICable {

	public CableBlock() {
		this(Block.Properties
			.create(Material.IRON)
			.hardnessAndResistance(3F, 6F)
			.sound(SoundType.METAL));
	}

	public CableBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void onNeighborChange(
		BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor
	) {
		if (world instanceof ServerWorld) {
			CableNetworkManager
				.getOrRegisterNetwork(((World) world), pos)
				.ifPresent(network -> network.rebuildAdjacentInventories(pos));
		}
	}

	@Override
	public void onBlockAdded(
		BlockState state,
		World worldIn,
		BlockPos pos,
		BlockState oldState,
		boolean isMoving
	) {
		if (!worldIn.isRemote) {
			super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
			CableNetworkManager.getOrRegisterNetwork(worldIn, pos);
			SFM.LOGGER.debug(
				SFMUtil.getMarker(getClass()),
				"{} networks now",
				CableNetworkManager.size()
			);
		}
	}

	@Override
	public void onReplaced(
		BlockState state,
		World worldIn,
		BlockPos pos,
		BlockState newState,
		boolean isMoving
	) {
		super.onReplaced(state, worldIn, pos, newState, isMoving);
		CableNetworkManager.unregister(worldIn, pos);
		SFM.LOGGER.debug(
			SFMUtil.getMarker(getClass()),
			"{} networks now",
			CableNetworkManager.size()
		);
	}
}
