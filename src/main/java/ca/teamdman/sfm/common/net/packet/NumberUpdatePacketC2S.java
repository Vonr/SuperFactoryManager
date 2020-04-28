package ca.teamdman.sfm.common.net.packet;

import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class NumberUpdatePacketC2S {
	private final BlockPos POSITION;
	private final int      PAYLOAD;

	public NumberUpdatePacketC2S(BlockPos pos, int payload) {
		this.POSITION = pos;
		this.PAYLOAD = payload;
	}

	public static void encode(NumberUpdatePacketC2S msg, PacketBuffer buf) {
		buf.writeBlockPos(msg.POSITION);
		buf.writeInt(msg.PAYLOAD);
	}

	public static void handle(NumberUpdatePacketC2S msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			//noinspection ConstantConditions
			ServerWorld world = ctx.get().getSender().getServerWorld();
			if (world.isBlockLoaded(msg.POSITION)) {
				BlockState state = world.getBlockState(msg.POSITION);
				TileEntity tile = world.getTileEntity(msg.POSITION);
				if (!(tile instanceof ManagerTileEntity)) return;
				ManagerTileEntity manager = (ManagerTileEntity) tile;
				manager.x = msg.PAYLOAD;
				manager.markDirty();
				world.notifyBlockUpdate(msg.POSITION, state, state, Constants.BlockFlags.BLOCK_UPDATE & Constants.BlockFlags.NOTIFY_NEIGHBORS
				);
			}
		});
		ctx.get().setPacketHandled(true);
	}

	public static NumberUpdatePacketC2S decode(PacketBuffer packetBuffer) {
		return new NumberUpdatePacketC2S(
				packetBuffer.readBlockPos(),
				packetBuffer.readInt());
	}
}
