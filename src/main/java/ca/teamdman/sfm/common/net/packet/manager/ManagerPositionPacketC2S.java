package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.common.flowdata.PositionProvider;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class ManagerPositionPacketC2S extends C2SManagerPacket {

	private final int X, Y;
	private final UUID ELEMENT_ID;

	public ManagerPositionPacketC2S(int windowId, BlockPos pos, UUID elementId, int x, int y) {
		super(windowId, pos);
		this.ELEMENT_ID = elementId;
		this.X = x;
		this.Y = y;
	}

	public static class Handler extends C2SHandler<ManagerPositionPacketC2S> {

		@Override
		public void finishEncode(ManagerPositionPacketC2S msg, PacketBuffer buf) {
			SFMUtil.writeUUID(msg.ELEMENT_ID, buf);
			buf.writeInt(msg.X);
			buf.writeInt(msg.Y);
		}

		@Override
		public ManagerPositionPacketC2S finishDecode(int windowId, BlockPos tilePos,
			PacketBuffer buf) {
			return new ManagerPositionPacketC2S(windowId, tilePos, SFMUtil.readUUID(buf),
				buf.readInt(), buf.readInt());
		}

		@Override
		public void handleDetailed(ManagerPositionPacketC2S msg,
			ManagerTileEntity manager) {
			manager.getData(msg.ELEMENT_ID)
				.filter(data -> data instanceof PositionProvider)
				.map(data -> ((PositionProvider) data).getPosition())
				.ifPresent(pos -> {
					pos.setXY(msg.X, msg.Y);
					manager.markAndNotify();
					manager.sendPacketToListeners(new ManagerPositionPacketS2C(
						msg.WINDOW_ID,
						msg.ELEMENT_ID,
						msg.X,
						msg.Y));
				});
		}
	}
}
