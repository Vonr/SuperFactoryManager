package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.flowdata.RelationshipFlowData;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.IContainerTilePacket;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class ManagerCreateRelationshipPacketC2S implements IContainerTilePacket {

	private final BlockPos TILE_POSITION;
	private final int WINDOW_ID;
	private final UUID ELEMENT_ID, FROM_ID, TO_ID;

	public ManagerCreateRelationshipPacketC2S(int WINDOW_ID, BlockPos TILE_POSITION,
		UUID ELEMENT_ID, UUID FROM_ID, UUID TO_ID) {
		this.TILE_POSITION = TILE_POSITION;
		this.WINDOW_ID = WINDOW_ID;
		this.ELEMENT_ID = ELEMENT_ID;
		this.FROM_ID = FROM_ID;
		this.TO_ID = TO_ID;
	}

	public static void encode(ManagerCreateRelationshipPacketC2S msg, PacketBuffer buf) {
		buf.writeInt(msg.WINDOW_ID);
		buf.writeBlockPos(msg.TILE_POSITION);
		buf.writeString(msg.ELEMENT_ID.toString(), SFMUtil.UUID_STRING_LENGTH);
		buf.writeString(msg.FROM_ID.toString(), SFMUtil.UUID_STRING_LENGTH);
		buf.writeString(msg.TO_ID.toString(), SFMUtil.UUID_STRING_LENGTH);
	}

	public static void handle(ManagerCreateRelationshipPacketC2S msg,
		Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> SFMUtil
			.getTileFromContainerPacket(msg, ctx, ManagerContainer.class, ManagerTileEntity.class)
			.ifPresent(manager -> handleDetailed(msg, manager)));
		ctx.get().setPacketHandled(true);
	}

	public static void handleDetailed(ManagerCreateRelationshipPacketC2S msg,
		ManagerTileEntity manager) {
		BlockState state = manager.getWorld().getBlockState(msg.TILE_POSITION);
		RelationshipFlowData data = new RelationshipFlowData(msg.ELEMENT_ID, msg.FROM_ID, msg.TO_ID);
		manager.data.put(msg.ELEMENT_ID, data);
		manager.markDirty();
		manager.getWorld().notifyBlockUpdate(msg.TILE_POSITION, state, state,
			Constants.BlockFlags.BLOCK_UPDATE & Constants.BlockFlags.NOTIFY_NEIGHBORS);
		manager.getContainerListeners().forEach(player -> PacketHandler.INSTANCE.send(
			PacketDistributor.PLAYER.with(() -> player),
			new ManagerCreateRelationshipPacketS2C(
				msg.WINDOW_ID,
				msg.ELEMENT_ID,
				msg.FROM_ID,
				msg.TO_ID)));
		SFM.LOGGER.debug("Manager tile has {} entries", manager.data.size());
	}


	public static ManagerCreateRelationshipPacketC2S decode(PacketBuffer packetBuffer) {
		int windowId = packetBuffer.readInt();
		BlockPos pos = packetBuffer.readBlockPos();
		UUID elementId = UUID.fromString(packetBuffer.readString(SFMUtil.UUID_STRING_LENGTH));
		UUID fromId = UUID.fromString(packetBuffer.readString(SFMUtil.UUID_STRING_LENGTH));
		UUID toId = UUID.fromString(packetBuffer.readString(SFMUtil.UUID_STRING_LENGTH));
		return new ManagerCreateRelationshipPacketC2S(windowId, pos, elementId, fromId, toId);
	}

	@Override
	public int getWindowId() {
		return WINDOW_ID;
	}

	@Override
	public BlockPos getTilePosition() {
		return TILE_POSITION;
	}
}
