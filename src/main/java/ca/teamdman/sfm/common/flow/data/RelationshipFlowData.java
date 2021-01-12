/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.FlowRelationship;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange.ChangeType;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.Objects;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class RelationshipFlowData extends FlowData implements Observer {

	public UUID from, to;

	public RelationshipFlowData(UUID uuid, UUID from, UUID to) {
		super(uuid);
		this.from = from;
		this.to = to;
	}

	@Override
	public void addToDataContainer(BasicFlowDataContainer container) {
		if (from == null || to == null) return;
		if (from.equals(to)) return;
		if (container.getAncestors(this, true)
			.map(FlowData::getId)
			.anyMatch(to::equals)) return;
		super.addToDataContainer(container);
		container.addObserver(this);
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
	public FlowDataSerializer getSerializer() {
		return FlowDataSerializers.RELATIONSHIP;
	}

	@Override
	public int hashCode() {
		return Objects.hash(from, to);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof FlowDataContainerChange && o instanceof BasicFlowDataContainer) {
			FlowDataContainerChange change = (FlowDataContainerChange) arg;
			BasicFlowDataContainer container = ((BasicFlowDataContainer) o);
			if (change.CHANGE == ChangeType.REMOVED) {

				// Remove this when the things it touches get removed
				if (change.DATA.getId().equals(from) || change.DATA.getId().equals(to)) {
					container.remove(getId());
					o.deleteObserver(this);
				}
			}
		}
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
