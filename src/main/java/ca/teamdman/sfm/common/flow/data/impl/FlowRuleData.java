/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data.impl;

import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.core.PositionHolder;
import ca.teamdman.sfm.common.registrar.FlowDataFactoryRegistrar.FlowDataFactories;
import java.util.UUID;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;

public abstract class FlowRuleData extends FlowData implements PositionHolder {

	public String name;
	public ItemStack icon;
	public Position position;


	public FlowRuleData(UUID uuid, String name, ItemStack icon, Position position) {
		super(uuid);
		this.name = name;
		this.icon = icon;
		this.position = position;
	}


	public ItemStack getIcon() {
		return icon;
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
	public void deserializeNBT(CompoundNBT tag) {
		super.deserializeNBT(tag);
		this.position = new Position(tag.getCompound("pos"));
		this.name = tag.getString("name");
		this.icon = ItemStack.read(tag.getCompound("icon"));
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public void merge(FlowData other) {
		if (other instanceof FlowRuleData) {
			name = ((FlowRuleData) other).name;
			icon = ((FlowRuleData) other).icon;
			position = ((FlowTileEntityRuleData) other).position;
		}
	}
}
