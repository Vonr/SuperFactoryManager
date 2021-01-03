/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data.impl;

import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.FlowOutputButton;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.FlowDataSerializer;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.core.PositionHolder;
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

public class TileOutputFlowData extends FlowData implements PositionHolder {

	public Position position;
	public List<UUID> tileEntityRules;

	public TileOutputFlowData(UUID uuid, Position position, List<UUID> ters) {
		super(uuid);
		this.position = position;
		this.tileEntityRules = ters;
	}


	@Override
	public void merge(FlowData other) {
		if (other instanceof TileOutputFlowData) {
			position = ((TileOutputFlowData) other).position;
			tileEntityRules = ((TileOutputFlowData) other).tileEntityRules;
		}
	}

	@Override
	public FlowComponent createController(
		FlowComponent parent
	) {
		if (!(parent instanceof ManagerFlowController)) {
			return null;
		}
		return new FlowOutputButton((ManagerFlowController) parent, this);
	}

	@Override
	public Position getPosition() {
		return position;
	}

	public static class FlowOutputDataSerializer extends FlowDataSerializer<TileOutputFlowData> {

		public FlowOutputDataSerializer(ResourceLocation key) {
			super(key);
		}

		@Override
		public TileOutputFlowData fromNBT(CompoundNBT tag) {
			return new TileOutputFlowData(
				UUID.fromString(tag.getString("uuid")),
				new Position(tag.getCompound("pos")),
				tag.getList("ters", NBT.TAG_STRING).stream()
					.map(INBT::getString)
					.map(UUID::fromString)
					.collect(Collectors.toList())
			);
		}

		@Override
		public CompoundNBT toNBT(TileOutputFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.put("pos", data.position.serializeNBT());
			tag.put("ters", data.tileEntityRules.stream()
				.map(UUID::toString)
				.map(StringNBT::valueOf)
				.collect(ListNBT::new, ListNBT::add, ListNBT::addAll));
			return tag;
		}

		@Override
		public TileOutputFlowData fromBuffer(PacketBuffer buf) {
			return new TileOutputFlowData(
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong()),
				IntStream.range(0, buf.readInt())
					.mapToObj(__ -> SFMUtil.readUUID(buf))
					.collect(Collectors.toList())
			);
		}

		@Override
		public void toBuffer(TileOutputFlowData data, PacketBuffer buf) {
			buf.writeString(data.getId().toString());
			buf.writeLong(data.position.toLong());
			buf.writeInt(data.tileEntityRules.size());
			data.tileEntityRules.forEach(id -> buf.writeString(id.toString()));
		}
	}
}
