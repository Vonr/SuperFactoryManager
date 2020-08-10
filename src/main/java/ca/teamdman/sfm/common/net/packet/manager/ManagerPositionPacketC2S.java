package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.flowdata.IFlowData;
import ca.teamdman.sfm.common.flowdata.IHasPosition;
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

public class ManagerPositionPacketC2S implements IContainerTilePacket {

	private final BlockPos TILE_POSITION;
	private final int WINDOW_ID, X, Y;
	private final UUID ELEMENT_ID;

	public ManagerPositionPacketC2S(int windowId, BlockPos pos, UUID elementId, int x, int y) {
		this.WINDOW_ID = windowId;
		this.TILE_POSITION = pos;
		this.ELEMENT_ID = elementId;
		this.X = x;
		this.Y = y;
	}

	public static void encode(ManagerPositionPacketC2S msg, PacketBuffer buf) {
		buf.writeInt(msg.WINDOW_ID);
		buf.writeBlockPos(msg.TILE_POSITION);
		buf.writeString(msg.ELEMENT_ID.toString(), SFMUtil.UUID_STRING_LENGTH);
		buf.writeInt(msg.X);
		buf.writeInt(msg.Y);
	}

	public static void handle(ManagerPositionPacketC2S msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			SFMUtil.getTileFromContainerPacket(msg, ctx, ManagerContainer.class,
				ManagerTileEntity.class).ifPresent(manager -> handleDetailed(msg, manager));
		});
		ctx.get().setPacketHandled(true);
	}

	public static void handleDetailed(ManagerPositionPacketC2S msg, ManagerTileEntity manager) {
		BlockState state = manager.getWorld().getBlockState(msg.TILE_POSITION);
		if (!manager.data.containsKey(msg.ELEMENT_ID)) {
			return;
		}
		IFlowData data = manager.data.get(msg.ELEMENT_ID);
		if (!(data instanceof IHasPosition)) {
			return;
		}
		((IHasPosition) data).getPosition().setXY(msg.X, msg.Y);
		manager.markDirty();
		manager.getWorld().notifyBlockUpdate(msg.TILE_POSITION, state, state,
			Constants.BlockFlags.BLOCK_UPDATE
				& Constants.BlockFlags.NOTIFY_NEIGHBORS);
		manager.getContainerListeners().forEach(player -> PacketHandler.INSTANCE.send(
			PacketDistributor.PLAYER.with(()->player),
			new ManagerPositionPacketS2C(
				msg.WINDOW_ID,
				msg.ELEMENT_ID,
				msg.X,
				msg.Y)));

	}


	public static ManagerPositionPacketC2S decode(PacketBuffer packetBuffer) {
		int windowId = packetBuffer.readInt();
		BlockPos pos = packetBuffer.readBlockPos();
		UUID elementId = UUID.fromString(packetBuffer.readString(SFMUtil.UUID_STRING_LENGTH));
		int x = packetBuffer.readInt();
		int y = packetBuffer.readInt();
		return new ManagerPositionPacketC2S(windowId, pos, elementId, x, y);
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
