/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data.core;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class FlowData {

	private final UUID uuid;

	public FlowData(UUID uuid) {
		this.uuid = uuid;
	}

	public FlowData() {
		this.uuid = UUID.randomUUID();
	}

	/**
	 * Copies all data from {@code other} into {@code this}
	 *
	 * @param other
	 */
	public abstract void merge(FlowData other);

	@Override
	public boolean equals(Object obj) {
		return obj instanceof FlowData && ((FlowData) obj).getId().equals(getId());
	}

	public UUID getId() {
		return uuid;
	}

	@Override
	public String toString() {
		return getId().toString();
	}

	@OnlyIn(Dist.CLIENT)
	@Nullable
	public abstract FlowComponent createController(
		FlowComponent parent
	);

	public abstract FlowDataSerializer getSerializer();
}
