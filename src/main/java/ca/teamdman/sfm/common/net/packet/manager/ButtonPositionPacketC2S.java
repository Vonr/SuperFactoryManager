package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.net.packet.IContainerTilePacket;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ButtonPositionPacketC2S implements IContainerTilePacket {
	private final BlockPos TILE_POSITION;
	private final int WINDOW_ID, ELEMENT_INDEX, X, Y;

	public ButtonPositionPacketC2S(int windowId, BlockPos pos, int elementIndex, int x, int y) {
		this.WINDOW_ID = windowId;
		this.TILE_POSITION = pos;
		this.ELEMENT_INDEX = elementIndex;
		this.X = x;
		this.Y = y;
	}

	public static void encode(ButtonPositionPacketC2S msg, PacketBuffer buf) {
		buf.writeInt(msg.WINDOW_ID);
		buf.writeBlockPos(msg.TILE_POSITION);
		buf.writeInt(msg.ELEMENT_INDEX);
		buf.writeInt(msg.X);
		buf.writeInt(msg.Y);
	}

	public static void handle(ButtonPositionPacketC2S msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			SFMUtil.getTileFromContainerPacket(msg, ctx, ManagerContainer.class, ManagerTileEntity.class).ifPresent(manager -> {
				//noinspection ConstantConditions
				BlockState state = manager.getWorld().getBlockState(msg.TILE_POSITION);
				manager.x = msg.X;
				manager.y = msg.Y;
				manager.markDirty();
				manager.getWorld().notifyBlockUpdate(msg.TILE_POSITION, state, state, Constants.BlockFlags.BLOCK_UPDATE & Constants.BlockFlags.NOTIFY_NEIGHBORS);
			});
		});
		ctx.get().setPacketHandled(true);
	}


	public static ButtonPositionPacketC2S decode(PacketBuffer packetBuffer) {
		int      windowId     = packetBuffer.readInt();
		BlockPos pos          = packetBuffer.readBlockPos();
		int      elementIndex = packetBuffer.readInt();
		int      x            = packetBuffer.readInt();
		int      y            = packetBuffer.readInt();
		return new ButtonPositionPacketC2S(windowId, pos, elementIndex, x, y);
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
