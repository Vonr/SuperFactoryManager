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
import ca.teamdman.sfm.common.flow.data.core.PositionHolder;
import ca.teamdman.sfm.common.registrar.FlowDataFactoryRegistrar.FlowDataFactories;
import java.util.UUID;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class FlowTileEntityRuleData extends FlowRuleData implements PositionHolder {

	public Position position;


	public FlowTileEntityRuleData(
		UUID uuid, String name, ItemStack icon, Position position
	) {
		super(uuid, name, icon);
		this.position = position;
	}

	public FlowTileEntityRuleData(CompoundNBT tag) {
		this(null, null, null, null);
		deserializeNBT(tag);
	}

	public ItemStack getIcon() {
		return new ItemStack(Items.DIAMOND_AXE);
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT tag = super.serializeNBT();
		tag.put("pos", position.serializeNBT());
		tag.putString("name", name);
		tag.put("icon", icon.serializeNBT());
		FlowDataFactories.TILE_ENTITY_RULE.stampNBT(tag);
		return tag;
	}

	@Override
	public void merge(FlowData other) {
		if (other instanceof FlowTileEntityRuleData) {
			position = ((FlowTileEntityRuleData) other).position;
		}
	}

	@Override
	public void deserializeNBT(CompoundNBT tag) {
		super.deserializeNBT(tag);
		this.position.deserializeNBT(tag.getCompound("pos"));
		this.name = tag.getString("name");
		this.icon = ItemStack.read(tag.getCompound("icon"));
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

	@Override
	public Position getPosition() {
		return position;
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
