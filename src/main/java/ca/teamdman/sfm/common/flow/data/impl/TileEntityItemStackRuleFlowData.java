/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package ca.teamdman.sfm.common.flow.data.impl;

import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.FlowTileEntityRule;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.FlowDataSerializer;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.core.PositionHolder;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import java.util.UUID;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class TileEntityItemStackRuleFlowData extends FlowData implements
	PositionHolder {

	public FilterMode filterMode;
	public String name;
	public ItemStack icon;
	public Position position;


	public TileEntityItemStackRuleFlowData(
		UUID uuid, String name, ItemStack icon, Position position, FilterMode filterMode
	) {
		super(uuid);
		this.name = name;
		this.icon = icon;
		this.position = position;
		this.filterMode = filterMode;
	}

	public ItemStack getIcon() {
		return icon;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public void merge(FlowData other) {
		if (other instanceof TileEntityItemStackRuleFlowData) {
			name = ((TileEntityItemStackRuleFlowData) other).name;
			icon = ((TileEntityItemStackRuleFlowData) other).icon;
			position = ((TileEntityItemStackRuleFlowData) other).position;
		}
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
	public FlowDataSerializer getSerializer() {
		return FlowDataSerializers.TILE_ENTITY_RULE;
	}

	public enum FilterMode {
		WHITELIST,
		BLACKLIST
	}

	public static class FlowTileEntityRuleDataSerializer extends
		FlowDataSerializer<TileEntityItemStackRuleFlowData> {

		public FlowTileEntityRuleDataSerializer(ResourceLocation registryName) {
			super(registryName);
		}

		@Override
		public TileEntityItemStackRuleFlowData fromNBT(CompoundNBT tag) {
			return new TileEntityItemStackRuleFlowData(
				UUID.fromString(tag.getString("uuid")),
				tag.getString("name"),
				ItemStack.read(tag.getCompound("icon")),
				new Position(tag.getCompound("pos")),
				FilterMode.valueOf(tag.getString("filterMode"))
			);
		}

		@Override
		public CompoundNBT toNBT(TileEntityItemStackRuleFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.put("pos", data.position.serializeNBT());
			tag.putString("name", data.name);
			tag.put("icon", data.icon.serializeNBT());
			tag.putString("filterMode", data.filterMode.name());
			return tag;
		}

		@Override
		public TileEntityItemStackRuleFlowData fromBuffer(PacketBuffer buf) {
			return new TileEntityItemStackRuleFlowData(
				SFMUtil.readUUID(buf),
				buf.readString(),
				buf.readItemStack(),
				Position.fromLong(buf.readLong()),
				FilterMode.valueOf(buf.readString())
			);
		}

		@Override
		public void toBuffer(TileEntityItemStackRuleFlowData data, PacketBuffer buf) {
			buf.writeString(data.getId().toString());
			buf.writeString(data.name);
			buf.writeItemStack(data.icon);
			buf.writeLong(data.position.toLong());
			buf.writeString(data.filterMode.name());
		}
	}
}
