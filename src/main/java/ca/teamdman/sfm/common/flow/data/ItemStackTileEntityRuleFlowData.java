/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstacktileentityrule.ItemStackTileEntityRuleFlowComponent;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.core.PositionHolder;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import ca.teamdman.sfm.common.util.SFMUtil;
import ca.teamdman.sfm.common.util.UUIDList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class ItemStackTileEntityRuleFlowData extends FlowData implements
	PositionHolder {

	public FilterMode filterMode;
	public String name;
	public ItemStack icon;
	public Position position;
	public UUIDList matcherIds;

	public ItemStackTileEntityRuleFlowData(

		UUID uuid,
		String name,
		ItemStack icon,
		Position position,
		FilterMode filterMode,
		List<UUID> matcherIds
	) {
		super(uuid);
		this.name = name;
		this.icon = icon;
		this.position = position;
		this.filterMode = filterMode;
		this.matcherIds = new UUIDList(matcherIds);
	}

	public ItemStack getIcon() {
		return icon;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public FlowComponent createController(
		FlowComponent parent
	) {
		if (!(parent instanceof ManagerFlowController)) {
			return null;
		}
		return new ItemStackTileEntityRuleFlowComponent((ManagerFlowController) parent, this);
	}

	@Override
	public FlowDataSerializer getSerializer() {
		return FlowDataSerializers.TILE_ENTITY_RULE;
	}


	public enum FilterMode {
		WHITELIST,
		BLACKLIST
	}


	@Override
	public Set<Class<? extends FlowData>> getDependencies() {
		return ImmutableSet.of(ItemStackComparerMatcherFlowData.class);
	}

	public static class FlowTileEntityRuleDataSerializer extends
		FlowDataSerializer<ItemStackTileEntityRuleFlowData> {

		public FlowTileEntityRuleDataSerializer(ResourceLocation registryName) {
			super(registryName);
		}

		@Override
		public ItemStackTileEntityRuleFlowData fromNBT(CompoundNBT tag) {
			return new ItemStackTileEntityRuleFlowData(
				UUID.fromString(tag.getString("uuid")),
				tag.getString("name"),
				ItemStack.read(tag.getCompound("icon")),
				new Position(tag.getCompound("pos")),
				FilterMode.valueOf(tag.getString("filterMode")),
				new UUIDList(tag, "matchers")
			);
		}

		@Override
		public CompoundNBT toNBT(ItemStackTileEntityRuleFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.put("pos", data.position.serializeNBT());
			tag.putString("name", data.name);
			tag.put("icon", data.icon.serializeNBT());
			tag.putString("filterMode", data.filterMode.name());
			tag.put("matchers", data.matcherIds.serialize());
			return tag;
		}

		@Override
		public ItemStackTileEntityRuleFlowData fromBuffer(PacketBuffer buf) {
			return new ItemStackTileEntityRuleFlowData(
				SFMUtil.readUUID(buf),
				buf.readString(),
				buf.readItemStack(),
				Position.fromLong(buf.readLong()),
				FilterMode.valueOf(buf.readString()),
				new UUIDList(buf)
			);
		}

		@Override
		public void toBuffer(ItemStackTileEntityRuleFlowData data, PacketBuffer buf) {
			buf.writeString(data.getId().toString());
			buf.writeString(data.name);
			buf.writeItemStack(data.icon);
			buf.writeLong(data.position.toLong());
			buf.writeString(data.filterMode.name());
			data.matcherIds.serialize(buf);
		}
	}
}
