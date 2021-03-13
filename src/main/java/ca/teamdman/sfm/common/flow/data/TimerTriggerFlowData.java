/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.timertrigger.TimerTriggerFlowComponent;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class TimerTriggerFlowData extends FlowData {

	public Position position;
	public int interval;
	public boolean open;

	public TimerTriggerFlowData(TimerTriggerFlowData other) {
		this(
			UUID.randomUUID(),
			other.position.copy(),
			other.interval,
			other.open
		);
	}

	public TimerTriggerFlowData(UUID uuid, Position position, int interval, boolean open) {
		super(uuid);
		this.position = position;
		this.interval = Math.max(20, interval); // enforce minimum 20 ticks
		this.open = open;
	}

	@Override
	public TimerTriggerFlowData duplicate(
		Function<UUID, Optional<FlowData>> lookupFn, Consumer<FlowData> dependencyTracker
	) {
		return new TimerTriggerFlowData(this);
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
		return new TimerTriggerFlowComponent((ManagerFlowController) parent, this);
	}

	@Override
	public FlowDataSerializer getSerializer() {
		return FlowDataSerializers.TIMER_TRIGGER;
	}

	public Position getPosition() {
		return position;
	}

	public static class Serializer extends
		FlowDataSerializer<TimerTriggerFlowData> {

		public Serializer(ResourceLocation key) {
			super(key);
		}

		@Override
		public TimerTriggerFlowData fromNBT(CompoundNBT tag) {
			return new TimerTriggerFlowData(
				UUID.fromString(tag.getString("uuid")),
				new Position(tag.getCompound("pos")),
				tag.getInt("interval"),
				tag.getBoolean("open")
			);
		}

		@Override
		public CompoundNBT toNBT(TimerTriggerFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.put("pos", data.position.serializeNBT());
			tag.putInt("interval", data.interval);
			tag.putBoolean("open", data.open);
			return tag;
		}

		@Override
		public TimerTriggerFlowData fromBuffer(PacketBuffer buf) {
			return new TimerTriggerFlowData(
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong()),
				buf.readInt(),
				buf.readBoolean()
			);
		}

		@Override
		public void toBuffer(TimerTriggerFlowData data, PacketBuffer buf) {
			SFMUtil.writeUUID(data.getId(), buf);
			buf.writeLong(data.position.toLong());
			buf.writeInt(data.interval);
			buf.writeBoolean(data.open);
		}
	}
}
