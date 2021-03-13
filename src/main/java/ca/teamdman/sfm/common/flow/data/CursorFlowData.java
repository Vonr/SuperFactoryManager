package ca.teamdman.sfm.common.flow.data;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.FlowCursor;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.registrar.FlowDataSerializerRegistrar.FlowDataSerializers;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

public class CursorFlowData extends FlowData {

	public String playerName;
	public Position position;

	public CursorFlowData(UUID uuid, String playerName, Position position) {
		super(uuid);
		this.playerName = playerName;
		this.position = position;
	}

	public CursorFlowData(CursorFlowData other) {
		this(
			UUID.randomUUID(),
			other.playerName,
			other.position.copy()
		);
	}

	@Override
	public FlowData duplicate(
		BasicFlowDataContainer container, Consumer<FlowData> dependencyTracker
	) {
		return new CursorFlowData(this);
	}

	@Nullable
	@Override
	public FlowComponent createController(FlowComponent parent) {
		if (parent instanceof ManagerFlowController) {
			return new FlowCursor(((ManagerFlowController) parent), this);
		}
		return null;
	}

	@Override
	public FlowDataSerializer getSerializer() {
		return FlowDataSerializers.CURSOR;
	}

	public static class Serializer extends
		FlowDataSerializer<CursorFlowData> {

		public Serializer(ResourceLocation key) {
			super(key);
		}

		@Override
		public CursorFlowData fromNBT(CompoundNBT tag) {
			return new CursorFlowData(
				UUID.fromString(tag.getString("uuid")),
				tag.getString("player"),
				new Position(tag.getCompound("position"))
			);
		}

		@Override
		public CompoundNBT toNBT(CursorFlowData data) {
			CompoundNBT tag = super.toNBT(data);
			tag.putString("player", data.playerName);
			tag.put("position", data.position.serializeNBT());
			return tag;
		}

		@Override
		public CursorFlowData fromBuffer(PacketBuffer buf) {
			return new CursorFlowData(
				SFMUtil.readUUID(buf),
				buf.readString(64),
				Position.fromLong(buf.readLong())
			);
		}

		@Override
		public void toBuffer(CursorFlowData data, PacketBuffer buf) {
			SFMUtil.writeUUID(data.getId(), buf);
			buf.writeString(data.playerName, 64);
			buf.writeLong(data.position.toLong());
		}
	}
}
