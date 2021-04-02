/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;

public class BaseContainer<T> extends Container {

	public final boolean IS_REMOTE;
	private final T SOURCE;

	public BaseContainer(ContainerType type, int windowId, T source, boolean isRemote) {
		super(type, windowId);
		this.SOURCE = source;
		this.IS_REMOTE = isRemote;
	}

	public T getSource() {
		return SOURCE;
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}

}
