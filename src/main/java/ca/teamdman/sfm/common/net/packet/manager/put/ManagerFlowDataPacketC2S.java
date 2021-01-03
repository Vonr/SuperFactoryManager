package ca.teamdman.sfm.common.net.packet.manager.put;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.FlowDataSerializer;
import ca.teamdman.sfm.common.net.packet.manager.C2SManagerPacket;
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
			FlowDataSerializer serializer = msg.DATA.getSerializer();
			buf.writeString(serializer.getRegistryName().toString());
			serializer.toBuffer(
				msg.DATA,
				buf
			);
		}

		@Override
		public ManagerFlowDataPacketC2S finishDecode(
			int windowId, BlockPos tilePos, PacketBuffer buf
		) {
			return new ManagerFlowDataPacketC2S(
				windowId,
				tilePos,
				FlowDataSerializer.getSerializer(buf.readString()).get().fromBuffer(buf)
			);
		}

		@Override
		public void handleDetailed(
			ManagerFlowDataPacketC2S msg, ManagerTileEntity manager
		) {
			SFM.LOGGER.debug("C2S received, FlowData {}", msg.DATA);
			msg.DATA.addToDataContainer(manager.getFlowDataContainer());
			manager.sendPacketToListeners(
				new ManagerFlowDataPacketS2C(
					msg.WINDOW_ID,
					msg.DATA
				)
			);
		}
	}
}
