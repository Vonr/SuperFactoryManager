/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.ItemInputFlowButton;
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

public class ItemInputFlowData extends FlowData implements Observer, PositionHolder {

	private final FlowDataRemovedObserver OBSERVER;
	public Position position;
	public UUID tileEntityRule;

	public ItemInputFlowData(ItemInputFlowData other) {
		this(
			UUID.randomUUID(),
			other.position.copy(),
			other.tileEntityRule
		);
	}

	public ItemInputFlowData(UUID uuid, Position position, UUID tileEntityRule) {
		super(uuid);
		this.position = position;
		this.tileEntityRule = tileEntityRule;
		OBSERVER = new FlowDataRemovedObserver(
			this,
			data -> data.getId().equals(tileEntityRule),
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
		container.get(tileEntityRule)
			.ifPresent(data -> data.removeFromDataContainer(container));
	}

	@Override
	public ItemInputFlowData duplicate(
		BasicFlowDataContainer container, Consumer<FlowData> dependencyTracker
	) {
		ItemInputFlowData newInput = new ItemInputFlowData(this);
		container.get(newInput.tileEntityRule, ItemRuleFlowData.class).ifPresent(data -> {
			FlowData newData = data.duplicate(container, dependencyTracker);
			dependencyTracker.accept(newData);
			newInput.tileEntityRule = newData.getId();
		});
		return newInput;
	}

	@Override
	public boolean isValidRelationshipTarget() {
		return true;
	}

	@Override
	public FlowComponent createController(
		FlowComponent parent
	) {
		if (parent instanceof ManagerFlowController) {
			return new ItemInputFlowButton(
				(ManagerFlowController) parent,
				this,
				((ManagerFlowController) parent).SCREEN.getFlowDataContainer()
					.get(tileEntityRule, ItemRuleFlowData.class)
					.orElseGet(ItemRuleFlowData::new)
			);
		}
		return null;
	}

	@Override
	public Set<Class<?>> getDependencies() {
		return ImmutableSet.of(ItemRuleFlowData.class);
	}

	@Override
	public FlowDataSerializer<ItemInputFlowData> getSerializer() {
		return FlowDataSerializers.BASIC_INPUT;
	}

	@Override
	public void update(Observable o, Object arg) {
		OBSERVER.update(o, arg);
	}

	@Override
	public Position getPosition() {
		return position;
	}

	public static class Serializer extends FlowDataSerializer<ItemInputFlowData> {

		public Serializer(ResourceLocation key) {
			super(key);
		}

		@Override
		public ItemInputFlowData fromNBT(CompoundNBT tag) {
			return new ItemInputFlowData(
				getUUID(tag),
				new Position(tag.getCompound("pos")),
				UUID.fromString(tag.getString("tileEntityRule"))
			);
		}

		@Override
		public CompoundNBT toNBT(ItemInputFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.put("pos", data.position.serializeNBT());
			tag.putString("tileEntityRule", data.tileEntityRule.toString());
			return tag;
		}

		@Override
		public ItemInputFlowData fromBuffer(PacketBuffer buf) {
			return new ItemInputFlowData(
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong()),
				SFMUtil.readUUID(buf)
			);
		}

		@Override
		public void toBuffer(ItemInputFlowData data, PacketBuffer buf) {
			SFMUtil.writeUUID(data.getId(), buf);
			buf.writeLong(data.position.toLong());
			SFMUtil.writeUUID(data.tileEntityRule, buf);
		}
	}
}
