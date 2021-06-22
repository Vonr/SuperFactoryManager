package ca.teamdman.sfm.common.net.packet;

import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.function.Supplier;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class C2SContainerPacket<TILE extends TileEntity, CONTAINER extends Container> implements
	IContainerTilePacket {

	public final Class<TILE> TILE_CLASS;
	public final Class<CONTAINER> CONTAINER_CLASS;
	public final BlockPos TILE_POSITION;
	public final int WINDOW_ID;

	public C2SContainerPacket(
		Class<TILE> tile_class,
		Class<CONTAINER> container_class,
		int WINDOW_ID,
		BlockPos TILE_POSITION
	) {
		TILE_CLASS = tile_class;
		CONTAINER_CLASS = container_class;
		this.TILE_POSITION = TILE_POSITION;
		this.WINDOW_ID = WINDOW_ID;
	}

	@Override
	public BlockPos getTilePosition() {
		return TILE_POSITION;
	}

	@Override
	public int getWindowId() {
		return WINDOW_ID;
	}

	public abstract static class C2SContainerPacketHandler<TILE extends TileEntity, CONTAINER extends Container, MSG extends C2SContainerPacket<TILE, CONTAINER>> {
		public void encode(MSG msg, PacketBuffer buf) {
			buf.writeInt(msg.WINDOW_ID);
			buf.writeBlockPos(msg.TILE_POSITION);
			finishEncode(msg, buf);
		}

		public abstract void finishEncode(MSG msg, PacketBuffer buf);

		public MSG decode(PacketBuffer buf) {
			return finishDecode(
				buf.readInt(),
				buf.readBlockPos(),
				buf
			);
		}

		public abstract MSG finishDecode(
			int windowId,
			BlockPos tilePos,
			PacketBuffer buf
		);

		public void handle(MSG msg, Supplier<Context> ctx) {
			ctx.get().enqueueWork(() ->
				SFMUtil.getTileFromContainerPacket(
					msg,
					ctx,
					msg.CONTAINER_CLASS,
					msg.TILE_CLASS
				).ifPresent(tile -> handleDetailed(ctx, msg, tile)));
			ctx.get().setPacketHandled(true);
		}

		public abstract void handleDetailed(
			Supplier<Context> ctx,
			MSG msg,
			TILE tile
		);
	}
}
