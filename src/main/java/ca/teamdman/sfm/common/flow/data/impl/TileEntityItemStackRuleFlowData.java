/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package ca.teamdman.sfm.common.flow.data.impl;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.FlowTileEntityRule;
import ca.teamdman.sfm.common.flow.data.core.FlowDataSerializer;
import ca.teamdman.sfm.common.flow.data.core.Position;
import java.util.UUID;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class TileEntityItemStackRuleFlowData extends RuleFlowData {
	public FilterMode filterMode;

	public TileEntityItemStackRuleFlowData(CompoundNBT tag) {
		this(null, null, null, null);
		deserializeNBT(tag);
	}

	public TileEntityItemStackRuleFlowData(
		UUID uuid, String name, ItemStack icon, Position position
	) {
		super(uuid, name, icon, position);
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

	enum FilterMode {
		WHITELIST,
		BLACKLIST
	}

	public static class FlowTileEntityRuleDataSerializer extends
		FlowDataSerializer<TileEntityItemStackRuleFlowData> {

		@Override
		public CompoundNBT toNBT(TileEntityItemStackRuleFlowData data) {
			return super.toNBT(data);
		}

		@Override
		public TileEntityItemStackRuleFlowData fromBuffer(PacketBuffer buf) {
			return null;
		}

		@Override
		public void toBuffer(TileEntityItemStackRuleFlowData data, PacketBuffer buf) {

		}

		public FlowTileEntityRuleDataSerializer(ResourceLocation registryName) {
			super(registryName);
		}

		@Override
		public TileEntityItemStackRuleFlowData fromNBT(CompoundNBT tag) {
			return new TileEntityItemStackRuleFlowData(tag);
		}
	}
}
