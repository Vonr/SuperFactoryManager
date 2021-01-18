/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstacktileentityrule.ItemStackTileEntityRuleFlowComponent;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.flow.core.ItemStackMatcher;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.core.PositionHolder;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.flow.holder.FlowDataRemovedObserver;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import ca.teamdman.sfm.common.tile.manager.ItemStackInputCandidate;
import ca.teamdman.sfm.common.tile.manager.ManagerTileEntity;
import ca.teamdman.sfm.common.util.BlockPosList;
import ca.teamdman.sfm.common.util.EnumSetSerializationHelper;
import ca.teamdman.sfm.common.util.SFMUtil;
import ca.teamdman.sfm.common.util.SlotsRule;
import ca.teamdman.sfm.common.util.UUIDList;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

public class ItemStackTileEntityRuleFlowData extends FlowData implements
	PositionHolder, Observer {

	private final FlowDataRemovedObserver OBSERVER;
	public FilterMode filterMode;
	public String name;
	public ItemStack icon;
	public Position position;
	public UUIDList matcherIds;
	public BlockPosList tilePositions;
	public EnumSet<Direction> faces;
	public SlotsRule slots;

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

	public List<ItemStackInputCandidate> getInput(ManagerTileEntity tile) {
		List<ItemStackInputCandidate> rtn = new ArrayList<>();
		if (tile.getWorld() == null) {
			return rtn;
		}
		CableNetworkManager.getOrRegisterNetwork(tile).ifPresent(network -> {
			List<TileEntity> tiles = tilePositions.stream()
				.filter(network::contains)
				.map(tpos -> tile.getWorld().getTileEntity(tpos))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

			List<ItemStackMatcher> matchers = matcherIds.stream()
				.map(id -> tile.getFlowDataContainer().get(id,ItemStackMatcher.class))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList());

			List<SlotItemStack> items = tiles.stream()
				.flatMap(t -> faces.stream()
					.map(face -> t.getCapability(
						CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
						face
					))
				.filter(LazyOptional::isPresent)
				.map(LazyOptional::resolve)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.flatMap(cap -> this.slots.getSlots(cap.getSlots())
					.mapToObj(slot -> new ItemStackInputCandidate(
						() -> cap.getStackInSlot(slot),
						i -> cap.extractItem(slot, i, false)
					)))
				.filter(sis -> keep(sis, matchers))
				.collect(Collectors.toList());

			return items.stream()
				.map(sis -> new ItemStackInputCandidate(
					() -> items.stream().map(sis -> sis.STACK).collect(Collectors.toList()),
					() -> items.stream().map(sis -> )
				))
				.collect(Collectors.toList());
		});
		return rtn;
	}

	public boolean keep(SlotItemStack item, List<ItemStackMatcher> matchers) {
		if (filterMode == FilterMode.WHITELIST) {
			return matchers.stream().anyMatch(m -> m.matches(item.STACK));
		} else /*if (filterMode == FilterMode.BLACKLIST)*/ {
			return matchers.stream().noneMatch(m -> m.matches(item.STACK));
		}
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

	private static class SlotItemStack {
		private final int SLOT;
		private final ItemStack STACK;

		public SlotItemStack(int slot, ItemStack stack) {
			this.SLOT = slot;
			this.STACK = stack;
		}
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
