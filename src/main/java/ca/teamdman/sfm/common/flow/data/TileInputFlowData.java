/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.FlowInputButton;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.core.PositionHolder;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange.ChangeType;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class TileInputFlowData extends FlowData implements PositionHolder, Observer {

	public Position position;
	public List<UUID> tileEntityRules;

	public TileInputFlowData(UUID uuid, Position position, List<UUID> ters) {
		super(uuid);
		this.position = position;
		this.tileEntityRules = ters;
	}

	@Override
	public FlowComponent createController(
		FlowComponent parent
	) {
		if (!(parent instanceof ManagerFlowController)) {
			return null;
		}
		return new FlowInputButton((ManagerFlowController) parent, this);
	}

	@Override
	public void addToDataContainer(BasicFlowDataContainer container) {
		super.addToDataContainer(container);
		container.addObserver(this);
	}

	@Override
	public FlowDataSerializer getSerializer() {
		return FlowDataSerializers.INPUT;
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof FlowDataContainerChange && o instanceof BasicFlowDataContainer) {
			FlowDataContainerChange change = (FlowDataContainerChange) arg;
			BasicFlowDataContainer container = (BasicFlowDataContainer) o;
			if (change.CHANGE == ChangeType.REMOVED) {
				if (tileEntityRules.remove(change.DATA.getId())) {
					// If the deleted item was a rule associated with this item
					// then it gets removed and we notify that we have updated
					container.notifyChanged(this);
				}
			}
		}
	}

	@Override
	public Position getPosition() {
		return position;
	}

	public static class FlowInputDataSerializer extends FlowDataSerializer<TileInputFlowData> {

		public FlowInputDataSerializer(ResourceLocation key) {
			super(key);
		}

		@Override
		public TileInputFlowData fromNBT(CompoundNBT tag) {
			return new TileInputFlowData(
				UUID.fromString(tag.getString("uuid")),
				new Position(tag.getCompound("pos")),
				SFMUtil.deserializeUUIDList(tag, "ters")
			);
		}

		@Override
		public CompoundNBT toNBT(TileInputFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.put("pos", data.position.serializeNBT());
			tag.put("ters", SFMUtil.serializeUUIDList(data.tileEntityRules));
			return tag;
		}

		@Override
		public TileInputFlowData fromBuffer(PacketBuffer buf) {
			return new TileInputFlowData(
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong()),
				SFMUtil.deserializeUUIDList(buf)
			);
		}

		@Override
		public void toBuffer(TileInputFlowData data, PacketBuffer buf) {
			buf.writeString(data.getId().toString());
			buf.writeLong(data.position.toLong());
			SFMUtil.serializeUUIDList(data.tileEntityRules, buf);
		}
	}
}
