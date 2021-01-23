/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.FlowTimerTrigger;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.core.PositionHolder;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class TimerTriggerFlowData extends FlowData implements PositionHolder {

	public Position position;
	public int interval;

	public TimerTriggerFlowData(UUID uuid, Position position, int interval) {
		super(uuid);
		this.position = position;
		this.interval = interval;
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
		return new FlowTimerTrigger((ManagerFlowController) parent, this);
	}

	@Override
	public FlowDataSerializer getSerializer() {
		return FlowDataSerializers.TIMER_TRIGGER;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	public static class FlowTimerTriggerDataSerializer extends
		FlowDataSerializer<TimerTriggerFlowData> {

		public FlowTimerTriggerDataSerializer(ResourceLocation key) {
			super(key);
		}

		@Override
		public TimerTriggerFlowData fromNBT(CompoundNBT tag) {
			return new TimerTriggerFlowData(
				UUID.fromString(tag.getString("uuid")),
				new Position(tag.getCompound("pos")),
				tag.getInt("interval")
			);
		}

		@Override
		public CompoundNBT toNBT(TimerTriggerFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.put("pos", data.position.serializeNBT());
			tag.putInt("interval", data.interval);
			return tag;
		}

		@Override
		public TimerTriggerFlowData fromBuffer(PacketBuffer buf) {
			return new TimerTriggerFlowData(
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong()),
				buf.readInt()
			);
		}

		@Override
		public void toBuffer(TimerTriggerFlowData data, PacketBuffer buf) {
			SFMUtil.writeUUID(data.getId(), buf);
			buf.writeLong(data.position.toLong());
			buf.writeInt(data.interval);
		}
	}
}
