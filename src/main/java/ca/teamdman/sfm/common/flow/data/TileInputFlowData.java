/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.FlowInputButton;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.flow.holder.FlowDataRemovedObserver;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import ca.teamdman.sfm.common.util.SFMUtil;
import ca.teamdman.sfm.common.util.UUIDList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class TileInputFlowData extends FlowData implements Observer {

	public Position position;
	public UUIDList tileEntityRules;
	private final FlowDataRemovedObserver OBSERVER;

	public TileInputFlowData(UUID uuid, Position position, List<UUID> ters) {
		super(uuid);
		this.position = position;
		this.tileEntityRules = new UUIDList(ters);
		OBSERVER = new FlowDataRemovedObserver(
			this,
			data -> this.tileEntityRules.remove(data.getId())
		);
	}

	@Override
	public void addToDataContainer(BasicFlowDataContainer container) {
		super.addToDataContainer(container);
		container.addObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
		OBSERVER.update(o, arg);
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
		return new FlowInputButton((ManagerFlowController) parent, this);
	}

	@Override
	public Set<Class<? extends FlowData>> getDependencies() {
		return ImmutableSet.of(ItemStackTileEntityRuleFlowData.class);
	}

	@Override
	public FlowDataSerializer getSerializer() {
		return FlowDataSerializers.INPUT;
	}

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
				new UUIDList(tag, "ters")
			);
		}

		@Override
		public CompoundNBT toNBT(TileInputFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.put("pos", data.position.serializeNBT());
			tag.put("ters", data.tileEntityRules.serialize());
			return tag;
		}

		@Override
		public TileInputFlowData fromBuffer(PacketBuffer buf) {
			return new TileInputFlowData(
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong()),
				new UUIDList(buf)
			);
		}

		@Override
		public void toBuffer(TileInputFlowData data, PacketBuffer buf) {
			SFMUtil.writeUUID(data.getId(), buf);
			buf.writeLong(data.position.toLong());
			data.tileEntityRules.serialize(buf);
		}
	}
}
