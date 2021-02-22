/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.AdvancedTileOutputFlowButton;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants.NBT;

public class AdvancedTileOutputFlowData extends FlowData {

	public Position position;
	public List<UUID> tileEntityRules;

	public AdvancedTileOutputFlowData(UUID uuid, Position position, List<UUID> ters) {
		super(uuid);
		this.position = position;
		this.tileEntityRules = ters;
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
		return new AdvancedTileOutputFlowButton((ManagerFlowController) parent, this);
	}

	@Override
	public FlowDataSerializer getSerializer() {
		return FlowDataSerializers.ADVANCED_OUTPUT;
	}

	public Position getPosition() {
		return position;
	}

	public static class Serializer extends FlowDataSerializer<AdvancedTileOutputFlowData> {

		public Serializer(ResourceLocation key) {
			super(key);
		}

		@Override
		public AdvancedTileOutputFlowData fromNBT(CompoundNBT tag) {
			return new AdvancedTileOutputFlowData(
				UUID.fromString(tag.getString("uuid")),
				new Position(tag.getCompound("pos")),
				tag.getList("ters", NBT.TAG_STRING).stream()
					.map(INBT::getString)
					.map(UUID::fromString)
					.collect(Collectors.toList())
			);
		}

		@Override
		public CompoundNBT toNBT(AdvancedTileOutputFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.put("pos", data.position.serializeNBT());
			tag.put("ters", data.tileEntityRules.stream()
				.map(UUID::toString)
				.map(StringNBT::valueOf)
				.collect(ListNBT::new, ListNBT::add, ListNBT::addAll));
			return tag;
		}

		@Override
		public AdvancedTileOutputFlowData fromBuffer(PacketBuffer buf) {
			return new AdvancedTileOutputFlowData(
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong()),
				IntStream.range(0, buf.readInt())
					.mapToObj(__ -> SFMUtil.readUUID(buf))
					.collect(Collectors.toList())
			);
		}

		@Override
		public void toBuffer(AdvancedTileOutputFlowData data, PacketBuffer buf) {
			SFMUtil.writeUUID(data.getId(), buf);
			buf.writeLong(data.position.toLong());
			buf.writeInt(data.tileEntityRules.size());
			data.tileEntityRules.forEach(id -> SFMUtil.writeUUID(id, buf));
		}
	}
}
