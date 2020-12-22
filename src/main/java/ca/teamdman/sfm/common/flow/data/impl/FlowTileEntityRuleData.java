/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package ca.teamdman.sfm.common.flow.data.impl;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.FlowTileEntityRule;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.FlowDataFactory;
import ca.teamdman.sfm.common.flow.data.core.Position;
import java.util.UUID;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class FlowTileEntityRuleData extends FlowRuleData {

	public FlowTileEntityRuleData(CompoundNBT tag) {
		this(null, null, null, null);
		deserializeNBT(tag);
	}

	public FlowTileEntityRuleData(
		UUID uuid, String name, ItemStack icon, Position position
	) {
		super(uuid, name, icon, position);
	}

	@Override
	public FlowData copy() {
		return new FlowTileEntityRuleData(getId(), name, icon, getPosition());
	}

	@Override
	public FlowComponent createController(
		FlowComponent parent
	) {
		if (!(parent instanceof ManagerFlowController)) {
			return null;
		}
		return new FlowTileEntityRule((ManagerFlowController) parent, this);
	}

	public static class FlowTileEntityRuleDataFactory extends
		FlowDataFactory<FlowTileEntityRuleData> {

		public FlowTileEntityRuleDataFactory(ResourceLocation registryName) {
			super(registryName);
		}

		@Override
		public FlowTileEntityRuleData fromNBT(CompoundNBT tag) {
			return new FlowTileEntityRuleData(tag);
		}
	}
}
