/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data.impl;

import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.FlowLineNode;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.FlowDataSerializer;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.core.PositionHolder;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class LineNodeFlowData extends FlowData implements PositionHolder {

	public Position position;

	public LineNodeFlowData(UUID uuid, Position position) {
		super(uuid);
		this.position = position;
	}

	@Override
	public void merge(FlowData other) {
		if (other instanceof LineNodeFlowData) {
			position = ((LineNodeFlowData) other).position;
		}
	}

	@Override
	public FlowComponent createController(
		FlowComponent parent
	) {
		if (!(parent instanceof ManagerFlowController)) {
			return null;
		}
		return new FlowLineNode((ManagerFlowController) parent, this);
	}

	@Override
	public FlowDataSerializer getSerializer() {
		return FlowDataSerializers.LINE_NODE;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	public static class LineNodeFlowDataSerializer extends FlowDataSerializer<LineNodeFlowData> {

		public LineNodeFlowDataSerializer(ResourceLocation key) {
			super(key);
		}

		@Override
		public LineNodeFlowData fromNBT(CompoundNBT tag) {
			return new LineNodeFlowData(
				UUID.fromString(tag.getString("uuid")),
				new Position(tag.getCompound("pos"))
			);
		}

		@Override
		public CompoundNBT toNBT(LineNodeFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.put("pos", data.position.serializeNBT());
			return tag;
		}

		@Override
		public LineNodeFlowData fromBuffer(PacketBuffer buf) {
			return new LineNodeFlowData(
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong())
			);
		}

		@Override
		public void toBuffer(LineNodeFlowData data, PacketBuffer buf) {
			buf.writeString(data.getId().toString());
			buf.writeLong(data.position.toLong());
		}
	}
}
