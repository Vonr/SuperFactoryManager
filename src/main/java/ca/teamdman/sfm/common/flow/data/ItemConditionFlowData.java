/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.core.PositionHolder;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.flow.holder.FlowDataRemovedObserver;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import ca.teamdman.sfm.common.util.SFMUtil;
import com.google.common.collect.ImmutableSet;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class ItemConditionFlowData extends FlowData implements Observer, PositionHolder {

	private final FlowDataRemovedObserver OBSERVER;
	public Position position;
	public UUID rule;

	public ItemConditionFlowData(ItemConditionFlowData other) {
		this(
			UUID.randomUUID(),
			other.position.copy(),
			other.rule
		);
	}

	public ItemConditionFlowData(UUID uuid, Position position, UUID rule) {
		super(uuid);
		this.position = position;
		this.rule = rule;
		OBSERVER = new FlowDataRemovedObserver(
			this,
			data -> data.getId().equals(rule),
			c -> c.remove(getId()) // remove this if rule gets deleted
		);
	}

	@Override
	public void addToDataContainer(BasicFlowDataContainer container) {
		super.addToDataContainer(container);
		container.addObserver(this);
	}

	@Override
	public void removeFromDataContainer(BasicFlowDataContainer container) {
		super.removeFromDataContainer(container);
		container.get(rule)
			.ifPresent(data -> data.removeFromDataContainer(container));
	}

	@Override
	public ItemConditionFlowData duplicate(
		BasicFlowDataContainer container, Consumer<FlowData> dependencyTracker
	) {
		ItemConditionFlowData dupe = new ItemConditionFlowData(this);
		container.get(dupe.rule, ItemConditionRuleFlowData.class)
			.ifPresent(data -> {
				FlowData newRule = data.duplicate(container, dependencyTracker);
				dependencyTracker.accept(newRule);
				dupe.rule = newRule.getId();
			});
		return dupe;
	}

	@Override
	public boolean isValidRelationshipTarget() {
		return true;
	}

	@Override
	public FlowComponent createController(
		FlowComponent parent
	) {
//		if (parent instanceof ManagerFlowController) {
//			return new ItemOutputFlowButton(
//				(ManagerFlowController) parent,
//				this,
//				((ManagerFlowController) parent).SCREEN.getFlowDataContainer()
//					.get(rule, ItemMovementRuleFlowData.class)
//					.orElseGet(ItemMovementRuleFlowData::new)
//			);
//		}
		return null;
	}

	@Override
	public Set<Class<?>> getDependencies() {
		return ImmutableSet.of(ItemMovementRuleFlowData.class);
	}

	@Override
	public FlowDataSerializer<ItemConditionFlowData> getSerializer() {
		return FlowDataSerializers.ITEM_CONDITION;
	}

	@Override
	public void update(Observable o, Object arg) {
		OBSERVER.update(o, arg);
	}

	@Override
	public Position getPosition() {
		return position;
	}

	public static class Serializer extends FlowDataSerializer<ItemConditionFlowData> {

		public Serializer(ResourceLocation key) {
			super(key);
		}

		@Override
		public ItemConditionFlowData fromNBT(CompoundNBT tag) {
			return new ItemConditionFlowData(
				getUUID(tag),
				new Position(tag.getCompound("pos")),
				UUID.fromString(tag.getString("tileEntityRule"))
			);
		}

		@Override
		public CompoundNBT toNBT(ItemConditionFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.put("pos", data.position.serializeNBT());
			tag.putString("tileEntityRule", data.rule.toString());
			return tag;
		}

		@Override
		public ItemConditionFlowData fromBuffer(PacketBuffer buf) {
			return new ItemConditionFlowData(
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong()),
				SFMUtil.readUUID(buf)
			);
		}

		@Override
		public void toBuffer(ItemConditionFlowData data, PacketBuffer buf) {
			SFMUtil.writeUUID(data.getId(), buf);
			buf.writeLong(data.position.toLong());
			SFMUtil.writeUUID(data.rule, buf);
		}
	}
}
