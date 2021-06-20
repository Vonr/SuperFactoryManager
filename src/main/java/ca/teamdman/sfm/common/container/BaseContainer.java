/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.container;

import ca.teamdman.sfm.common.tile.ContainerListenerTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BaseContainer<T> extends Container {
	public final boolean IS_REMOTE;
	private final T SOURCE;

	public BaseContainer(ContainerType type, int windowId, T source, boolean isRemote) {
		super(type, windowId);
		this.SOURCE = source;
		this.IS_REMOTE = isRemote;
		MinecraftForge.EVENT_BUS.register(this);
	}

	public T getSource() {
		return SOURCE;
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}

	@SubscribeEvent
	public void onContainerOpen(PlayerContainerEvent.Open e) {
		if (IS_REMOTE) return;
		if (e.getContainer() != this) return;
		if (!(getSource() instanceof ContainerListenerTracker)) return;
		((ContainerListenerTracker) getSource()).getListeners()
			.put((ServerPlayerEntity) e.getPlayer(), windowId);
	}

	@SubscribeEvent
	public void onContainerClose(PlayerContainerEvent.Close e) {
		if (IS_REMOTE) return;
		if (e.getContainer() != this) return;
		if (!(getSource() instanceof ContainerListenerTracker)) return;
		((ContainerListenerTracker) getSource()).getListeners()
			.remove((ServerPlayerEntity) e.getPlayer(), windowId);
	}
}
