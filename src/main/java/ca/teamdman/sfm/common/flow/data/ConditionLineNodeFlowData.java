/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.ConditionLineNodeFlowComponent;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.core.PositionHolder;
import ca.teamdman.sfm.common.flow.data.ItemConditionRuleFlowData.Result;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange.ChangeType;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class ConditionLineNodeFlowData extends FlowData implements Observer, PositionHolder {

	public Position position;
	public Result responsibility;

	public ConditionLineNodeFlowData(Result resp) {
		this(
			UUID.randomUUID(),
			new Position(),
			resp
		);
	}

	public ConditionLineNodeFlowData(ConditionLineNodeFlowData other) {
		this(
			UUID.randomUUID(),
			other.position.copy(),
			other.responsibility
		);
	}

	public ConditionLineNodeFlowData(UUID uuid, Position position, Result resp) {
		super(uuid);
		this.position = position;
		this.responsibility = resp;
	}

	@Override
	public void addToDataContainer(BasicFlowDataContainer container) {
		super.addToDataContainer(container);
		container.addObserver(this);
	}

	@Override
	public ConditionLineNodeFlowData duplicate(
		BasicFlowDataContainer container, Consumer<FlowData> dependencyTracker
	) {
		return new ConditionLineNodeFlowData(this);
	}

	@Override
	public boolean isValidRelationshipTarget() {
		return true;
	}

	@Override
	public FlowComponent createController(
		FlowComponent parent
	) {
		if (!(parent instanceof ManagerFlowController)) {
			return null;
		}
		return new ConditionLineNodeFlowComponent((ManagerFlowController) parent, this);
	}

	@Override
	public FlowDataSerializer<ConditionLineNodeFlowData> getSerializer() {
		return FlowDataSerializers.CONDITION_LINE_NODE;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof FlowDataContainerChange && o instanceof BasicFlowDataContainer) {
			FlowDataContainerChange change = (FlowDataContainerChange) arg;
			BasicFlowDataContainer container = ((BasicFlowDataContainer) o);
			if (change.CHANGE == ChangeType.REMOVED) {
				// No previous item (parent condition), so this node is orphaned and should be pruned
				if (!container.getAncestors(getId(), false).findAny().isPresent()) {
					container.remove(getId());
					o.deleteObserver(this);
				}
			}
		}
	}

	public static class Serializer extends FlowDataSerializer<ConditionLineNodeFlowData> {

		public Serializer(ResourceLocation key) {
			super(key);
		}

		@Override
		public ConditionLineNodeFlowData fromNBT(CompoundNBT tag) {
			return new ConditionLineNodeFlowData(
				getUUID(tag),
				new Position(tag.getCompound("pos")),
				Result.valueOf(tag.getString("resp"))
			);
		}

		@Override
		public CompoundNBT toNBT(ConditionLineNodeFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.put("pos", data.position.serializeNBT());
			tag.putString("resp", data.responsibility.name());
			return tag;
		}

		@Override
		public ConditionLineNodeFlowData fromBuffer(PacketBuffer buf) {
			return new ConditionLineNodeFlowData(
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong()),
				Result.valueOf(buf.readUtf(12))
			);
		}

		@Override
		public void toBuffer(ConditionLineNodeFlowData data, PacketBuffer buf) {
			SFMUtil.writeUUID(data.getId(), buf);
			buf.writeLong(data.position.toLong());
			buf.writeUtf(data.responsibility.name(), 12);
		}
	}
}
