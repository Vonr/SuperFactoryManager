package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.common.flowdata.LineNodeFlowData;
import ca.teamdman.sfm.common.flowdata.Position;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class ManagerCreateLineNodePacketC2S extends C2SManagerPacket {

	private final UUID ELEMENT_ID;
	private final Position ELEMENT_POSITION;

	public ManagerCreateLineNodePacketC2S(int WINDOW_ID, BlockPos TILE_POSITION,
		UUID ELEMENT_ID, Position POSITION) {
		super(WINDOW_ID, TILE_POSITION);
		this.ELEMENT_ID = ELEMENT_ID;
		this.ELEMENT_POSITION = POSITION;
	}

	public static class Handler extends C2SHandler<ManagerCreateLineNodePacketC2S> {

		@Override
		public void finishEncode(ManagerCreateLineNodePacketC2S msg,
			PacketBuffer buf) {
			SFMUtil.writeUUID(msg.ELEMENT_ID, buf);
			buf.writeLong(msg.ELEMENT_POSITION.toLong());
		}

		@Override
		public ManagerCreateLineNodePacketC2S finishDecode(int windowId, BlockPos tilePos,
			PacketBuffer buf) {
			return new ManagerCreateLineNodePacketC2S(
				windowId,
				tilePos,
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong())
			);
		}

		@Override
		public void handleDetailed(ManagerCreateLineNodePacketC2S msg,
			ManagerTileEntity manager) {
			LineNodeFlowData data = new LineNodeFlowData(msg.ELEMENT_ID, msg.ELEMENT_POSITION);
			manager.addData(data);
			manager.markAndNotify();
			manager.sendPacketToListeners(new ManagerCreateLineNodePacketS2C(
				msg.WINDOW_ID,
				msg.ELEMENT_ID,
				msg.ELEMENT_POSITION));
			SFM.LOGGER.debug("Manager tile has {} entries", manager.data.size());
		}
	}
}
