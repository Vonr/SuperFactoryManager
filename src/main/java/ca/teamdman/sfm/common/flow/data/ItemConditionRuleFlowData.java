package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemconditionrule.ItemConditionRuleFlowComponent;
import ca.teamdman.sfm.common.flow.core.FlowDialog;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.core.PositionHolder;
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
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;

public class ItemConditionRuleFlowData extends FlowData implements Observer, PositionHolder,
	FlowDialog {

	public static final int MAX_NAME_LENGTH = 256;
	private final FlowDataRemovedObserver OBSERVER;
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
			I18n.format("gui.sfm.flow.placeholder.default_rule_name"),
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
		this.OBSERVER = new FlowDataRemovedObserver(
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

	public ItemStack getIcon() {
		return icon;
	}

	@Override
	public Position getPosition() {
		return position;
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
	public void update(Observable o, Object arg) {
		OBSERVER.update(o, arg);
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
		MATCH_NONE,
		MATCH_ANY;
	}

	public enum TileMode {
		MATCH_ALL,
		MATCH_NONE,
		MATCH_ANY;
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
				ItemStack.read(tag.getCompound("icon")),
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
				buf.readString(MAX_NAME_LENGTH),
				buf.readItemStack(),
				Position.fromLong(buf.readLong()),
				new UUIDList(buf),
				new UUIDList(buf),
				EnumSetSerializationHelper.deserialize(buf, Direction::valueOf, Direction.class),
				new SlotsRule(buf.readString(32)),
				ItemMode.valueOf(buf.readString(16)),
				TileMode.valueOf(buf.readString(16)),
				buf.readBoolean()
			);
		}

		@Override
		public void toBuffer(ItemConditionRuleFlowData data, PacketBuffer buf) {
			SFMUtil.writeUUID(data.getId(), buf);
			buf.writeString(data.name, MAX_NAME_LENGTH);
			buf.writeItemStack(data.icon);
			buf.writeLong(data.position.toLong());
			data.itemMatcherIds.serialize(buf);
			data.tileMatcherIds.serialize(buf);
			EnumSetSerializationHelper.serialize(data.faces, buf);
			buf.writeString(data.slots.getDefinition(), 32);
			buf.writeString(data.itemMode.name(), 16);
			buf.writeString(data.tileMode.name(), 16);
			buf.writeBoolean(data.open);
		}
	}
}
