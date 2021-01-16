/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstacktileentityrule.ItemStackTileEntityRuleFlowComponent;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.core.PositionHolder;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.flow.holder.FlowDataRemovedObserver;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import ca.teamdman.sfm.common.util.BlockPosList;
import ca.teamdman.sfm.common.util.EnumSetSerializationHelper;
import ca.teamdman.sfm.common.util.SFMUtil;
import ca.teamdman.sfm.common.util.SlotsRule;
import ca.teamdman.sfm.common.util.UUIDList;
import com.google.common.collect.ImmutableSet;
import java.util.EnumSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.UUID;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class ItemStackTileEntityRuleFlowData extends FlowData implements
	PositionHolder, Observer {

	public FilterMode filterMode;
	public String name;
	public ItemStack icon;
	public Position position;
	public UUIDList matcherIds;
	public BlockPosList tilePositions;
	public EnumSet<Direction> faces;
	public SlotsRule slots;
	private final FlowDataRemovedObserver OBSERVER;

	public ItemStackTileEntityRuleFlowData(
		UUID uuid,
		String name,
		ItemStack icon,
		Position position,
		FilterMode filterMode,
		List<UUID> matcherIds,
		List<BlockPos> tilePositions,
		EnumSet<Direction> faces,
		SlotsRule slots
	) {
		super(uuid);
		this.name = name;
		this.icon = icon;
		this.position = position;
		this.filterMode = filterMode;
		this.matcherIds = new UUIDList(matcherIds);
		this.tilePositions = new BlockPosList(tilePositions);
		this.faces = faces;
		this.slots = slots;
		this.OBSERVER = new FlowDataRemovedObserver(
			this,
			data -> this.matcherIds.remove(data.getId())
		);
	}

	public ItemStack getIcon() {
		return icon;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public void addToDataContainer(BasicFlowDataContainer container) {
		super.addToDataContainer(container);
		container.addObserver(this);
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
	public Set<Class<? extends FlowData>> getDependencies() {
		return ImmutableSet.of(ItemStackComparerMatcherFlowData.class);
	}

	@Override
	public FlowDataSerializer getSerializer() {
		return FlowDataSerializers.TILE_ENTITY_RULE;
	}

	@Override
	public void update(Observable o, Object arg) {
		OBSERVER.update(o, arg);
	}

	public enum FilterMode {
		WHITELIST,
		BLACKLIST
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
				new UUIDList(tag, "matchers"),
				new BlockPosList(tag, "tiles"),
				EnumSetSerializationHelper.deserialize(tag, "faces", Direction::valueOf),
				new SlotsRule(tag.getString("slots"))
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
			tag.put("tiles", data.tilePositions.serialize());
			tag.put("faces", EnumSetSerializationHelper.serialize(data.faces));
			tag.putString("slots", data.slots.getDefinition());
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
				new UUIDList(buf),
				new BlockPosList(buf),
				EnumSetSerializationHelper.deserialize(buf, Direction::valueOf),
				new SlotsRule(buf.readString())
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
			data.tilePositions.serialize(buf);
			EnumSetSerializationHelper.serialize(data.faces, buf);
			buf.writeString(data.slots.getDefinition());
		}
	}
}
