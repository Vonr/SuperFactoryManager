package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class ManagerFlowDataPacketC2S extends C2SManagerPacket {
	private final FlowData DATA;
	public ManagerFlowDataPacketC2S(
		int windowId, BlockPos pos, FlowData data
	) {
		super(windowId, pos);
		this.DATA = data;
	}

	public static class Handler extends C2SHandler<ManagerFlowDataPacketC2S> {

		@Override
		public void finishEncode(
			ManagerFlowDataPacketC2S msg, PacketBuffer buf
		) {
			msg.DATA.getSerializer().toBuffer(
				msg.DATA,
				buf
			);
		}

		@Override
		public ManagerFlowDataPacketC2S finishDecode(
			int windowId, BlockPos tilePos, PacketBuffer buf
		) {
			return null;
		}

		@Override
		public void handleDetailed(
			ManagerFlowDataPacketC2S managerFlowDataPacketC2S, ManagerTileEntity manager
		) {

		}
	}
}
