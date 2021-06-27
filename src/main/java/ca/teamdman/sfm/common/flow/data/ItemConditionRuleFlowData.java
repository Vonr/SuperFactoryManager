package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemconditionrule.ItemConditionRuleFlowComponent;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.flow.core.FlowDialog;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.core.PositionHolder;
import ca.teamdman.sfm.common.flow.core.TileMatcher;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.flow.holder.FlowDataRemovedObserver;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import ca.teamdman.sfm.common.util.EnumSetSerializationHelper;
import ca.teamdman.sfm.common.util.SFMUtil;
import ca.teamdman.sfm.common.util.SlotsRule;
import ca.teamdman.sfm.common.util.UUIDList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
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

public class ItemConditionRuleFlowData extends FlowData implements PositionHolder,
	FlowDialog {

	public static final int MAX_NAME_LENGTH = 256;
	private final FlowDataRemovedObserver DATA_REMOVED_OBSERVER;
	public String name;
	public ItemStack icon;
	public Position position;
	public UUIDList itemMatcherIds;
	public UUIDList tileMatcherIds;
	public ItemMode itemMode;
	public TileMode tileMode;
	public EnumSet<Direction> faces;
	public SlotsRule slots;
	public boolean open;

	public ItemConditionRuleFlowData() {
		this(
			UUID.randomUUID(),
			I18n.get("gui.sfm.flow.placeholder.default_rule_name"),
			ItemStack.EMPTY,
			new Position(0, 0),
			Collections.emptyList(),
			Collections.emptyList(),
			EnumSet.allOf(Direction.class),
			new SlotsRule(""),
			ItemMode.MATCH_ALL,
			TileMode.MATCH_ALL,
			false
		);
	}

	public ItemConditionRuleFlowData(
		UUID uuid,
		String name,
		ItemStack icon,
		Position position,
		Collection<UUID> itemMatcherIds,
		Collection<UUID> tileMatcherIds,
		EnumSet<Direction> faces,
		SlotsRule slots,
		ItemMode itemMode,
		TileMode tileMode,
		boolean open
	) {
		super(uuid);
		this.name = name;
		this.icon = icon;
		this.position = position;
		this.itemMatcherIds = new UUIDList(itemMatcherIds);
		this.faces = faces;
		this.slots = slots;
		this.open = open;
		this.DATA_REMOVED_OBSERVER = new FlowDataRemovedObserver(
			this,
			data -> this.itemMatcherIds.remove(data.getId())
				|| this.tileMatcherIds.remove(data.getId())
		);
		this.tileMatcherIds = new UUIDList(tileMatcherIds);
		this.itemMode = itemMode;
		this.tileMode = tileMode;
	}

	public ItemConditionRuleFlowData(ItemConditionRuleFlowData other) {
		this(
			UUID.randomUUID(),
			other.name,
			other.icon.copy(),
			other.position.copy(),
			new UUIDList(other.itemMatcherIds),
			new UUIDList(other.tileMatcherIds),
			EnumSet.copyOf(other.faces),
			other.slots.copy(),
			other.itemMode,
			other.tileMode,
			other.open
		);
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
		container.addObserver(DATA_REMOVED_OBSERVER);
	}

	@Override
	public void removeFromDataContainer(BasicFlowDataContainer container) {
		super.removeFromDataContainer(container);
		Stream.concat(itemMatcherIds.stream(), tileMatcherIds.stream())
			.map(container::get)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.forEach(data -> data.removeFromDataContainer(container));
	}

	@Override
	public ItemConditionRuleFlowData duplicate(
		BasicFlowDataContainer container, Consumer<FlowData> dependencyTracker
	) {
		return new ItemConditionRuleFlowData(this);
	}

	@Nullable
	@Override
	public FlowComponent createController(FlowComponent parent) {
		if (!(parent instanceof ManagerFlowController)) {
			return null;
		}
		return new ItemConditionRuleFlowComponent((ManagerFlowController) parent, this);
	}

	@Override
	public FlowDataSerializer<?> getSerializer() {
		return FlowDataSerializers.ITEM_CONDITION_RULE;
	}

	@Override
	public boolean isOpen() {
		return open;
	}

	@Override
	public void setOpen(boolean value) {
		open = value;
	}

	public enum ItemMode {
		MATCH_ALL,
		MATCH_ANY;
	}

	public enum TileMode {
		MATCH_ALL,
		MATCH_ANY;
	}

	public enum Result {
		ACCEPTED("gui.sfm.flow.tooltip.condition_accepted", true),
		REJECTED("gui.sfm.flow.tooltip.condition_rejected", false);

		public final String UNLOCALIZED_DISPLAY_NAME;
		public final boolean RESULT;

		Result(String unlocalizedName, boolean result) {
			UNLOCALIZED_DISPLAY_NAME = unlocalizedName;
			RESULT = result;
		}
	}

	public static class Serializer extends
		FlowDataSerializer<ItemConditionRuleFlowData> {

		public Serializer(ResourceLocation registryName) {
			super(registryName);
		}

		@Override
		public ItemConditionRuleFlowData fromNBT(CompoundNBT tag) {
			return new ItemConditionRuleFlowData(
				getUUID(tag),
				tag.getString("name"),
				ItemStack.of(tag.getCompound("icon")),
				new Position(tag.getCompound("pos")),
				new UUIDList(tag, "itemMatchers"),
				new UUIDList(tag, "tileMatchers"),
				EnumSetSerializationHelper.deserialize(tag, "faces", Direction::valueOf),
				new SlotsRule(tag.getString("slots")),
				ItemMode.valueOf(tag.getString("itemMode")),
				TileMode.valueOf(tag.getString("tileMode")),
				tag.getBoolean("open")
			);
		}

		@Override
		public CompoundNBT toNBT(ItemConditionRuleFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.putString("name", data.name);
			tag.put("icon", data.icon.serializeNBT());
			tag.put("pos", data.position.serializeNBT());
			tag.put("itemMatchers", data.itemMatcherIds.serialize());
			tag.put("tileMatchers", data.tileMatcherIds.serialize());
			tag.put("faces", EnumSetSerializationHelper.serialize(data.faces));
			tag.putString("slots", data.slots.getDefinition());
			tag.putString("itemMode", data.itemMode.name());
			tag.putString("tileMode", data.tileMode.name());
			tag.putBoolean("open", data.open);
			return tag;
		}

		@Override
		public ItemConditionRuleFlowData fromBuffer(PacketBuffer buf) {
			return new ItemConditionRuleFlowData(
				SFMUtil.readUUID(buf),
				buf.readUtf(MAX_NAME_LENGTH),
				buf.readItem(),
				Position.fromLong(buf.readLong()),
				new UUIDList(buf),
				new UUIDList(buf),
				EnumSetSerializationHelper.deserialize(buf, Direction::valueOf, Direction.class),
				new SlotsRule(buf.readUtf(32)),
				ItemMode.valueOf(buf.readUtf(16)),
				TileMode.valueOf(buf.readUtf(16)),
				buf.readBoolean()
			);
		}

		@Override
		public void toBuffer(ItemConditionRuleFlowData data, PacketBuffer buf) {
			SFMUtil.writeUUID(data.getId(), buf);
			buf.writeUtf(data.name, MAX_NAME_LENGTH);
			buf.writeItem(data.icon);
			buf.writeLong(data.position.toLong());
			data.itemMatcherIds.serialize(buf);
			data.tileMatcherIds.serialize(buf);
			EnumSetSerializationHelper.serialize(data.faces, buf);
			buf.writeUtf(data.slots.getDefinition(), 32);
			buf.writeUtf(data.itemMode.name(), 16);
			buf.writeUtf(data.tileMode.name(), 16);
			buf.writeBoolean(data.open);
		}
	}
}
