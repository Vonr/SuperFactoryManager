/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
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

	public void addToDataContainer(BasicFlowDataContainer container) {
		container.put(this);
	}

	public void removeFromDataContainer(BasicFlowDataContainer container) {
		container.remove(getId());
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

	public abstract <T extends FlowData> FlowDataSerializer<T> getSerializer();
}
