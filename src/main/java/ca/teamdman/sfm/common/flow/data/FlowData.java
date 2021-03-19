/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
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

	/**
	 * Copy this flow data, and all its dependencies, assigning new IDs to anything with an ID.
	 * @return Duplicate of this FlowData
	 */
	public abstract FlowData duplicate(
		BasicFlowDataContainer container,
		Consumer<FlowData> dependencyTracker
	);

	public boolean isValidRelationshipTarget() {
		return false;
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

	public Set<Class<?>> getDependencies() {
		return Collections.emptySet();
	}

	public abstract FlowDataSerializer<?> getSerializer();
}
