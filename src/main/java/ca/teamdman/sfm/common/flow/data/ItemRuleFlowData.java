/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */

package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemrule.ItemRuleFlowComponent;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.flow.core.ItemMatcher;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.core.PositionHolder;
import ca.teamdman.sfm.common.flow.core.TileMatcher;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.flow.holder.FlowDataRemovedObserver;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import ca.teamdman.sfm.common.tile.manager.ExecutionState;
import ca.teamdman.sfm.common.util.EnumSetSerializationHelper;
import ca.teamdman.sfm.common.util.SFMUtil;
import ca.teamdman.sfm.common.util.SlotsRule;
import ca.teamdman.sfm.common.util.UUIDList;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ItemRuleFlowData extends FlowData implements
	Observer, PositionHolder {

	public static final int MAX_NAME_LENGTH = 256;

	private final FlowDataRemovedObserver OBSERVER;
	public FilterMode filterMode;
	public String name;
	public ItemStack icon;
	public Position position;
	public UUIDList itemMatcherIds;
	public UUIDList tileMatcherIds;
	public EnumSet<Direction> faces;
	public SlotsRule slots;
	public boolean open;

	public ItemRuleFlowData() {
		this(
			UUID.randomUUID(),
			I18n.format("gui.sfm.flow.placeholder.default_rule_name"),
			ItemStack.EMPTY,
			new Position(0, 0),
			FilterMode.WHITELIST,
			Collections.emptyList(),
			Collections.emptyList(),
			EnumSet.allOf(Direction.class),
			new SlotsRule(""),
			false
		);
	}

	public ItemRuleFlowData(
		UUID uuid,
		String name,
		ItemStack icon,
		Position position,
		FilterMode filterMode,
		Collection<UUID> itemMatcherIds,
		Collection<UUID> tileMatcherIds,
		EnumSet<Direction> faces,
		SlotsRule slots,
		boolean open
	) {
		super(uuid);
		this.name = name;
		this.icon = icon;
		this.position = position;
		this.filterMode = filterMode;
		this.itemMatcherIds = new UUIDList(itemMatcherIds);
		this.faces = faces;
		this.slots = slots;
		this.open = open;
		this.OBSERVER = new FlowDataRemovedObserver(
			this,
			data -> this.itemMatcherIds.remove(data.getId())
				|| this.tileMatcherIds.remove(data.getId())
		);
		this.tileMatcherIds = new UUIDList(tileMatcherIds);
	}

	/**
	 * Copy this rule, but with a new ID
	 */
	public ItemRuleFlowData(ItemRuleFlowData other) {
		this(
			UUID.randomUUID(),
			other.name,
			other.icon.copy(),
			other.position.copy(),
			other.filterMode,
			new UUIDList(other.itemMatcherIds),
			other.tileMatcherIds, EnumSet.copyOf(other.faces),
			other.slots.copy(),
			other.open
		);
	}

	/**
	 * @return maximum amount allowed through according to this rule
	 */
	public Optional<ItemMatcher> getBestItemMatcher(
		BasicFlowDataContainer container,
		ItemStack stack,
		ExecutionState state
	) {
		return itemMatcherIds.stream()
			.map(id -> container.get(id, ItemMatcher.class))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.filter(m -> m.matches(stack))
			.max(Comparator
				.comparingInt(m -> state.getRemainingQuantity(this, m))); // Most remaining first
	}

	public Stream<TileEntity> getTiles(BasicFlowDataContainer container, CableNetwork network) {
		List<TileMatcher> matchers = tileMatcherIds.stream()
			.map(id -> container.get(id, TileMatcher.class))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toList());

		Predicate<TileEntity> matches = tile -> matchers.stream()
			.anyMatch(m -> m.matches(tile));

		return network.getInventories().stream()
			.filter(matches);
	}

	public List<IItemHandler> getItemHandlers(
		BasicFlowDataContainer container,
		CableNetwork network
	) {
		return this.getTiles(container, network)
			.flatMap(tile -> faces.stream()
				.map(face -> tile.getCapability(
					CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
					face
				)))
			.filter(LazyOptional::isPresent)
			.map(LazyOptional::resolve)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.toList());
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
	public ItemRuleFlowData duplicate(
		BasicFlowDataContainer container, Consumer<FlowData> dependencyTracker
	) {
		ItemRuleFlowData newRule = new ItemRuleFlowData(this);
		dependencyTracker.accept(newRule);

		newRule.itemMatcherIds.clear();
		itemMatcherIds.stream()
			.map(container::get)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.filter(ItemMatcher.class::isInstance)
			.map(data -> data.duplicate(container, dependencyTracker)) // copy matcher data
			.peek(dependencyTracker) // add new matcher to dependency tracker
			.map(FlowData::getId)
			.forEach(newRule.itemMatcherIds::add); // add matcher to rule id list

		newRule.tileMatcherIds.clear();
		tileMatcherIds.stream()
			.map(container::get)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.filter(TileMatcher.class::isInstance)
			.map(data -> data.duplicate(container, dependencyTracker))
			.peek(dependencyTracker)
			.map(FlowData::getId)
			.forEach(newRule.tileMatcherIds::add);

		return newRule;
	}

	@Override
	public FlowComponent createController(
		FlowComponent parent
	) {
		if (!(parent instanceof ManagerFlowController)) {
			return null;
		}
		return new ItemRuleFlowComponent((ManagerFlowController) parent, this);
	}

	@Override
	public Set<Class<?>> getDependencies() {
		return ImmutableSet.of(ItemMatcher.class, TileMatcher.class);
	}

	@Override
	public FlowDataSerializer<ItemRuleFlowData> getSerializer() {
		return FlowDataSerializers.ITEM_RULE;
	}

	@Override
	public void update(Observable o, Object arg) {
		OBSERVER.update(o, arg);
	}

	public enum FilterMode {
		WHITELIST,
		BLACKLIST
	}

	public static class Serializer extends
		FlowDataSerializer<ItemRuleFlowData> {

		public Serializer(ResourceLocation registryName) {
			super(registryName);
		}

		@Override
		public ItemRuleFlowData fromNBT(CompoundNBT tag) {
			return new ItemRuleFlowData(
				getUUID(tag),
				tag.getString("name"),
				ItemStack.read(tag.getCompound("icon")),
				new Position(tag.getCompound("pos")),
				FilterMode.valueOf(tag.getString("filterMode")),
				new UUIDList(tag, "itemMatchers"),
				new UUIDList(tag, "tileMatchers"),
				EnumSetSerializationHelper.deserialize(tag, "faces", Direction::valueOf),
				new SlotsRule(tag.getString("slots")),
				tag.getBoolean("open")
			);
		}

		@Override
		public CompoundNBT toNBT(ItemRuleFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.put("pos", data.position.serializeNBT());
			tag.putString("name", data.name);
			tag.put("icon", data.icon.serializeNBT());
			tag.putString("filterMode", data.filterMode.name());
			tag.put("itemMatchers", data.itemMatcherIds.serialize());
			tag.put("faces", EnumSetSerializationHelper.serialize(data.faces));
			tag.putString("slots", data.slots.getDefinition());
			tag.putBoolean("open", data.open);
			tag.put("tileMatchers", data.tileMatcherIds.serialize());
			return tag;
		}

		@Override
		public ItemRuleFlowData fromBuffer(PacketBuffer buf) {
			return new ItemRuleFlowData(
				SFMUtil.readUUID(buf),
				buf.readString(MAX_NAME_LENGTH),
				buf.readItemStack(),
				Position.fromLong(buf.readLong()),
				FilterMode.valueOf(buf.readString(16)),
				new UUIDList(buf),
				new UUIDList(buf),
				EnumSetSerializationHelper.deserialize(buf, Direction::valueOf),
				new SlotsRule(buf.readString(32)),
				buf.readBoolean()
			);
		}

		@Override
		public void toBuffer(ItemRuleFlowData data, PacketBuffer buf) {
			SFMUtil.writeUUID(data.getId(), buf);
			buf.writeString(data.name, MAX_NAME_LENGTH);
			buf.writeItemStack(data.icon);
			buf.writeLong(data.position.toLong());
			buf.writeString(data.filterMode.name(), 16);
			data.itemMatcherIds.serialize(buf);
			data.tileMatcherIds.serialize(buf);
			EnumSetSerializationHelper.serialize(data.faces, buf);
			buf.writeString(data.slots.getDefinition(), 32);
			buf.writeBoolean(data.open);
		}
	}
}
