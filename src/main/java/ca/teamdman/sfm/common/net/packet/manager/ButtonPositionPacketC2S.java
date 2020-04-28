package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ButtonPositionPacketC2S {
	private final BlockPos TILE_POSITION;
	private final int      ELEMENT_INDEX, X, Y;

	public ButtonPositionPacketC2S(BlockPos pos, int elementIndex, int x, int y) {
		this.TILE_POSITION = pos;
		this.ELEMENT_INDEX = elementIndex;
		this.X = x;
		this.Y = y;
	}

	public static void encode(ButtonPositionPacketC2S msg, PacketBuffer buf) {
		buf.writeBlockPos(msg.TILE_POSITION);
		buf.writeInt(msg.ELEMENT_INDEX);
		buf.writeInt(msg.X);
		buf.writeInt(msg.Y);
	}

	public static void handle(ButtonPositionPacketC2S msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			//noinspection ConstantConditions
			ServerWorld world = ctx.get().getSender().getServerWorld();
			if (world.isBlockLoaded(msg.TILE_POSITION)) {
				BlockState state = world.getBlockState(msg.TILE_POSITION);
				TileEntity tile  = world.getTileEntity(msg.TILE_POSITION);
				if (!(tile instanceof ManagerTileEntity))
					return;
				ManagerTileEntity manager = (ManagerTileEntity) tile;

				manager.x = msg.X;
				manager.y = msg.Y;
				manager.markDirty();

				world.notifyBlockUpdate(msg.TILE_POSITION, state, state, Constants.BlockFlags.BLOCK_UPDATE & Constants.BlockFlags.NOTIFY_NEIGHBORS
				);
			}
		});
		ctx.get().setPacketHandled(true);
	}


	public static ButtonPositionPacketC2S decode(PacketBuffer packetBuffer) {
		BlockPos pos          = packetBuffer.readBlockPos();
		int      elementIndex = packetBuffer.readInt();
		int      x            = packetBuffer.readInt();
		int      y            = packetBuffer.readInt();
		return new ButtonPositionPacketC2S(pos, elementIndex, x, y);
	}
}
