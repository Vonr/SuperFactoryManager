package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.impl.FlowInputData;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class ManagerCreateInputPacketC2S extends C2SManagerPacket {

	private final Position ELEMENT_POSITION;

	public ManagerCreateInputPacketC2S(int windowId, BlockPos pos, Position elementPosition) {
		super(windowId, pos);
		this.ELEMENT_POSITION = elementPosition;
	}

	public static class Handler extends C2SManagerPacket.C2SHandler<ManagerCreateInputPacketC2S> {

		@Override
		public void finishEncode(
			ManagerCreateInputPacketC2S msg,
			PacketBuffer buf
		) {
			buf.writeLong(msg.ELEMENT_POSITION.toLong());
		}

		@Override
		public ManagerCreateInputPacketC2S finishDecode(
			int windowId, BlockPos tilePos,
			PacketBuffer buf
		) {
			return new ManagerCreateInputPacketC2S(
				windowId,
				tilePos,
				Position.fromLong(buf.readLong())
			);
		}

		@Override
		public void handleDetailed(ManagerCreateInputPacketC2S msg, ManagerTileEntity manager) {
			FlowInputData data = new FlowInputData(
				UUID.randomUUID(),
				msg.ELEMENT_POSITION
			);

			SFM.LOGGER.debug(
				SFMUtil.getMarker(getClass()),
				"C2S received, creating input at position {} with id {}",
				msg.ELEMENT_POSITION,
				data.getId()
			);

			manager.addData(data);
			manager.markAndNotify();
			manager.sendPacketToListeners(new ManagerCreateInputPacketS2C(
				msg.WINDOW_ID,
				data.getId(),
				msg.ELEMENT_POSITION
			));
		}
	}
}
