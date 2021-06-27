/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.container;

import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.registrar.SFMContainers;
import ca.teamdman.sfm.common.tile.manager.ManagerTileEntity;
import ca.teamdman.sfm.common.util.SFMUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.fml.network.NetworkHooks;


public class ManagerContainer extends BaseContainer<ManagerTileEntity> {

	public ManagerContainer(int windowId, ManagerTileEntity tile, boolean isClientSide) {
		super(SFMContainers.MANAGER.get(), windowId, tile, isClientSide);
		tile.pruneCursors();
	}

	public static ManagerContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
		return SFMUtil.getClientTile(
			IWorldPosCallable.create(inv.player.level, data.readBlockPos()),
			ManagerTileEntity.class
		)
			.map(tile -> {
				CableNetworkManager.getOrRegisterNetwork(tile.getLevel(), tile.getBlockPos()).ifPresent(network -> {
					network.rebuildNetwork(tile.getBlockPos());
				});
				ManagerContainer container = new ManagerContainer(windowId, tile, true);
				container.readData(data);
				return container;
			})
			.orElse(null);
	}

	public static void openGui(ServerPlayerEntity player, ManagerTileEntity tile) {
		NetworkHooks.openGui(
			player,
			tile,
			data -> writeData(tile, data)
		);
	}

	private void readData(PacketBuffer data) {
		getSource().deserializeNBT(data.readNbt());
	}

	private static void writeData(ManagerTileEntity tile, PacketBuffer data) {
		data.writeBlockPos(tile.getBlockPos());
		data.writeNbt(tile.serializeNBT());
	}
}
