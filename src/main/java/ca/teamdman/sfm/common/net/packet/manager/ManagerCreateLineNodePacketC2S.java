package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.common.flowdata.FlowUtils;
import ca.teamdman.sfm.common.flowdata.core.Position;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class ManagerCreateLineNodePacketC2S extends C2SManagerPacket {

	private final Position ELEMENT_POSITION;
	private final UUID FROM_ID, TO_ID;

	public ManagerCreateLineNodePacketC2S(int WINDOW_ID, BlockPos TILE_POSITION, UUID from, UUID to,
		Position POSITION) {
		super(WINDOW_ID, TILE_POSITION);
		this.ELEMENT_POSITION = POSITION;
		this.FROM_ID = from;
		this.TO_ID = to;
	}

	public static class Handler extends C2SHandler<ManagerCreateLineNodePacketC2S> {

		@Override
		public void finishEncode(ManagerCreateLineNodePacketC2S msg,
			PacketBuffer buf) {
			SFMUtil.writeUUID(msg.FROM_ID, buf);
			SFMUtil.writeUUID(msg.TO_ID, buf);
			buf.writeLong(msg.ELEMENT_POSITION.toLong());
		}

		@Override
		public ManagerCreateLineNodePacketC2S finishDecode(int windowId, BlockPos tilePos,
			PacketBuffer buf) {
			return new ManagerCreateLineNodePacketC2S(
				windowId,
				tilePos,
				SFMUtil.readUUID(buf),
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong())
			);
		}

		@Override
		public void handleDetailed(
			ManagerCreateLineNodePacketC2S msg,
			ManagerTileEntity manager
		) {
			UUID nodeId = UUID.randomUUID();
			UUID fromToNodeId = UUID.randomUUID();
			UUID toToNodeId = UUID.randomUUID();
			SFM.LOGGER.debug(
				SFMUtil.getMarker(getClass()),
				"C2S received, creating relationship from {} to {}, node id {}, rel ids {} and {}",
				msg.FROM_ID,
				msg.TO_ID,
				nodeId,
				fromToNodeId,
				toToNodeId
			);
			FlowUtils.insertLineNode(
				manager,
				msg.FROM_ID,
				msg.TO_ID,
				nodeId,
				fromToNodeId,
				toToNodeId,
				msg.ELEMENT_POSITION);

			manager.sendPacketToListeners(
				new ManagerCreateLineNodePacketS2C(
					msg.WINDOW_ID,
					msg.FROM_ID,
					msg.TO_ID,
					nodeId,
					fromToNodeId,
					toToNodeId,
					msg.ELEMENT_POSITION
				)
			);
		}
	}
}
