package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.common.flowdata.InputFlowData;
import ca.teamdman.sfm.common.flowdata.Position;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class ManagerCreateInputPacketC2S extends C2SManagerPacket {
	private final int X, Y;
	private final UUID ELEMENT_ID;

	public ManagerCreateInputPacketC2S(int windowId, BlockPos pos, UUID elementId, int x, int y) {
		super(windowId, pos);
		this.ELEMENT_ID = elementId;
		this.X = x;
		this.Y = y;
	}

	public static class Handler extends C2SManagerPacket.C2SHandler<ManagerCreateInputPacketC2S> {

		@Override
		public void finishEncode(ManagerCreateInputPacketC2S msg,
			PacketBuffer buf) {
			SFMUtil.writeUUID(msg.ELEMENT_ID, buf);
			buf.writeInt(msg.X);
			buf.writeInt(msg.Y);
		}

		@Override
		public ManagerCreateInputPacketC2S finishDecode(int windowId, BlockPos tilePos,
			PacketBuffer buf) {
			return new ManagerCreateInputPacketC2S(
				windowId,
				tilePos,
				SFMUtil.readUUID(buf),
				buf.readInt(),
				buf.readInt()
			);
		}

		@Override
		public void handleDetailed(ManagerCreateInputPacketC2S msg, ManagerTileEntity manager) {
			InputFlowData data = new InputFlowData(msg.ELEMENT_ID, new Position(msg.X, msg.Y));
			manager.addData(data);
			manager.markAndNotify();
			manager.sendPacketToListeners(new ManagerCreateInputPacketS2C(
				msg.WINDOW_ID,
				msg.ELEMENT_ID,
				msg.X,
				msg.Y));
			SFM.LOGGER.debug("Manager tile has {} entries", manager.data.size());
		}
	}
}
