/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.container;

import ca.teamdman.sfm.common.registrar.ContainerRegistrar;
import ca.teamdman.sfm.common.tile.manager.ManagerTileEntity;
import ca.teamdman.sfm.common.util.SFMUtil;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;


public class ManagerContainer extends BaseContainer<ManagerTileEntity> {

	public ManagerContainer(int windowId, ManagerTileEntity tile, boolean isRemote) {
		super(ContainerRegistrar.MANAGER.get(), windowId, tile, isRemote);
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static ManagerContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
		return SFMUtil.getClientTile(
			IWorldPosCallable.of(inv.player.world, data.readBlockPos()),
			ManagerTileEntity.class
		)
			.map(tile -> {
				ManagerContainer managerContainer = new ManagerContainer(windowId, tile, true);
				managerContainer.readData(data);
				return managerContainer;
			})
			.orElse(null);
	}

	public void readData(PacketBuffer data) {
		getSource().deserializeNBT(data.readCompoundTag());
	}

	public static void writeData(ManagerTileEntity tile, PacketBuffer data) {
		data.writeCompoundTag(tile.serializeNBT());
	}

	@SubscribeEvent
	public void onContainerOpen(PlayerContainerEvent.Open e) {
		if (!IS_REMOTE) {
			getSource().addContainerListener((ServerPlayerEntity) e.getPlayer(), e.getContainer().windowId);
		}
	}

	@SubscribeEvent
	public void onContainerClose(PlayerContainerEvent.Close e) {
		if (!IS_REMOTE) {
			getSource().removeContainerListener(((ServerPlayerEntity) e.getPlayer()));
		}
	}
}
