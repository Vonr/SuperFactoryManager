/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data.impl;

import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.FlowRelationship;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.FlowDataSerializer;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class RelationshipFlowData extends FlowData {

	public UUID from, to;

	public RelationshipFlowData(UUID uuid, UUID from, UUID to) {
		super(uuid);
		this.from = from;
		this.to = to;
	}


	@Override
	public void merge(FlowData other) {
		if (other instanceof RelationshipFlowData) {
			from = ((RelationshipFlowData) other).from;
			to = ((RelationshipFlowData) other).to;
		}
	}

	@Override
	public FlowDataSerializer getSerializer() {
		return FlowDataSerializers.RELATIONSHIP;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		RelationshipFlowData that = (RelationshipFlowData) o;
		return Objects.equals(from, that.from) && Objects.equals(to, that.to);
	}

	@Override
	public FlowComponent createController(
		FlowComponent parent
	) {
		if (!(parent instanceof ManagerFlowController)) {
			return null;
		}
		return new FlowRelationship((ManagerFlowController) parent, this);
	}

	@Override
	public int hashCode() {
		return Objects.hash(from, to);
	}

	public static class FlowRelationshipDataSerializer extends
		FlowDataSerializer<RelationshipFlowData> {

		public FlowRelationshipDataSerializer(ResourceLocation registryName) {
			super(registryName);
		}

		@Override
		public RelationshipFlowData fromNBT(CompoundNBT tag) {
			return new RelationshipFlowData(
				UUID.fromString(tag.getString("uuid")),
				UUID.fromString(tag.getString("from")),
				UUID.fromString(tag.getString("to"))
			);
		}

		@Override
		public CompoundNBT toNBT(RelationshipFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.putString("from", data.from.toString());
			tag.putString("to", data.to.toString());
			return tag;
		}

		@Override
		public RelationshipFlowData fromBuffer(PacketBuffer buf) {
			return new RelationshipFlowData(
				SFMUtil.readUUID(buf),
				SFMUtil.readUUID(buf),
				SFMUtil.readUUID(buf)
			);
		}

		@Override
		public void toBuffer(RelationshipFlowData data, PacketBuffer buf) {
			buf.writeString(data.getId().toString());
			buf.writeString(data.from.toString());
			buf.writeString(data.to.toString());

		}
	}
}
