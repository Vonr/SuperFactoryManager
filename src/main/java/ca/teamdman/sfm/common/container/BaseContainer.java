/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.container;

import ca.teamdman.sfm.common.tile.ContainerListenerTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BaseContainer<T> extends Container {

	public final boolean IS_REMOTE;
	private final T SOURCE;

	public BaseContainer(
		ContainerType type,
		int windowId,
		T source,
		boolean isClientSide
	) {
		super(type, windowId);
		this.SOURCE = source;
		this.IS_REMOTE = isClientSide;
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		if (SOURCE instanceof TileEntity) {
			return stillValid(
				IWorldPosCallable.create(
					((TileEntity) SOURCE).getLevel(),
					((TileEntity) SOURCE).getBlockPos()
				),
				player,
				((TileEntity) SOURCE).getBlockState().getBlock()
			);
		} else {
			return true;
		}
	}

	@SubscribeEvent
	public void onContainerOpen(PlayerContainerEvent.Open e) {
		if (IS_REMOTE) return;
		if (e.getContainer() != this) return;
		if (!(getSource() instanceof ContainerListenerTracker)) return;
		((ContainerListenerTracker) getSource()).getListeners()
			.put((ServerPlayerEntity) e.getPlayer(), containerId);
	}

	public T getSource() {
		return SOURCE;
	}

	@SubscribeEvent
	public void onContainerClose(PlayerContainerEvent.Close e) {
		if (IS_REMOTE) return;
		if (e.getContainer() != this) return;
		if (!(getSource() instanceof ContainerListenerTracker)) return;
		((ContainerListenerTracker) getSource()).getListeners()
			.remove((ServerPlayerEntity) e.getPlayer(), containerId);
	}
}
