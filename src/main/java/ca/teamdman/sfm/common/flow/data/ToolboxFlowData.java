/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.FlowToolbox;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.UUID;
import java.util.function.Consumer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class ToolboxFlowData extends FlowData {

	public Position position;

	public ToolboxFlowData(ToolboxFlowData other) {
		this(
			UUID.randomUUID(),
			other.position.copy()
		);
	}

	public ToolboxFlowData(UUID uuid, Position position) {
		super(uuid);
		this.position = position;
	}

	@Override
	public void addToDataContainer(BasicFlowDataContainer container) {
		super.addToDataContainer(container);
	}

	@Override
	public ToolboxFlowData duplicate(
		BasicFlowDataContainer container, Consumer<FlowData> dependencyTracker
	) {
		return new ToolboxFlowData(this);
	}

	@Override
	public FlowComponent createController(
		FlowComponent parent
	) {
		if (!(parent instanceof ManagerFlowController)) {
			return null;
		}
		return new FlowToolbox((ManagerFlowController) parent, this);
	}

	@Override
	public ca.teamdman.sfm.common.flow.data.FlowDataSerializer getSerializer() {
		return FlowDataSerializers.TOOLBOX;
	}

	public Position getPosition() {
		return position;
	}

	public static class Serializer extends
		ca.teamdman.sfm.common.flow.data.FlowDataSerializer<ToolboxFlowData> {

		public Serializer(ResourceLocation key) {
			super(key);
		}

		@Override
		public ToolboxFlowData fromNBT(CompoundNBT tag) {
			return new ToolboxFlowData(
				UUID.fromString(tag.getString("uuid")),
				new Position(tag.getCompound("pos"))
			);
		}

		@Override
		public CompoundNBT toNBT(ToolboxFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.put("pos", data.position.serializeNBT());
			return tag;
		}

		@Override
		public ToolboxFlowData fromBuffer(PacketBuffer buf) {
			return new ToolboxFlowData(
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong())
			);
		}

		@Override
		public void toBuffer(ToolboxFlowData data, PacketBuffer buf) {
			SFMUtil.writeUUID(data.getId(), buf);
			buf.writeLong(data.position.toLong());
		}
	}
}
