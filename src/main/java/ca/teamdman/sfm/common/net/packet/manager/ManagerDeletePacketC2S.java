package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class ManagerDeletePacketC2S extends C2SManagerPacket {
	private final UUID ELEMENT_ID;

	public ManagerDeletePacketC2S(int windowId, BlockPos pos, UUID elementId) {
		super(windowId, pos);
		this.ELEMENT_ID = elementId;
	}

	public static class Handler extends C2SHandler<ManagerDeletePacketC2S> {

		@Override
		public void finishEncode(
			ManagerDeletePacketC2S msg,
			PacketBuffer buf) {
			SFMUtil.writeUUID(msg.ELEMENT_ID, buf);
		}

		@Override
		public ManagerDeletePacketC2S finishDecode(int windowId, BlockPos tilePos,
			PacketBuffer buf) {
			return new ManagerDeletePacketC2S(
				windowId,
				tilePos,
				SFMUtil.readUUID(buf)
			);
		}

		@Override
		public void handleDetailed(ManagerDeletePacketC2S msg, ManagerTileEntity manager) {
			SFM.LOGGER.debug(
				SFMUtil.getMarker(getClass()),
				"C2S received, deleting element with id {}",
				msg.ELEMENT_ID
			);
			manager.removeData(msg.ELEMENT_ID);
			manager.markAndNotify();
			manager.sendPacketToListeners(new ManagerDeletePacketS2C(
				msg.WINDOW_ID,
				msg.ELEMENT_ID));
		}
	}
}
