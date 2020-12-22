/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data.core;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;

public abstract class FlowData implements INBTSerializable<CompoundNBT>, ICopyable<FlowData> {

	private UUID uuid;

	public FlowData(UUID uuid) {
		this.uuid = uuid;
	}

	public FlowData() {
		this.uuid = UUID.randomUUID();
	}

	public UUID getId() {
		return uuid;
	}

	public void setId(UUID uuid) {
		this.uuid = uuid;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT tag = new CompoundNBT();
		tag.putString("uuid", uuid.toString());
		return tag;
	}

	/**
	 * Copies all data from {@code other} into {@code this}
	 * @param other
	 */
	public abstract void merge(FlowData other);

	@Override
	public abstract FlowData copy();

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.uuid = UUID.fromString(nbt.getString("uuid"));
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof FlowData && ((FlowData) obj).getId().equals(getId());
	}

	@Override
	public String toString() {
		return getId().toString();
	}

	@OnlyIn(Dist.CLIENT)
	public abstract FlowComponent createController(
		FlowComponent parent
	);
}
