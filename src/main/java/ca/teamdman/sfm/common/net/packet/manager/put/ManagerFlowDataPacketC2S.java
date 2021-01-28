package ca.teamdman.sfm.common.net.packet.manager.put;

import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.FlowDataSerializer;
import ca.teamdman.sfm.common.net.packet.manager.C2SManagerPacket;
import ca.teamdman.sfm.common.tile.manager.ManagerTileEntity;
import java.util.stream.IntStream;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class ManagerFlowDataPacketC2S extends C2SManagerPacket {
	private final FlowData[] DATA;
	public ManagerFlowDataPacketC2S(
		int windowId, BlockPos pos, FlowData... data
	) {
		super(windowId, pos);
		this.DATA = data;
	}

	public static class Handler extends C2SHandler<ManagerFlowDataPacketC2S> {

		@Override
		public void finishEncode(
			ManagerFlowDataPacketC2S msg, PacketBuffer buf
		) {
			buf.writeInt(msg.DATA.length);
			for (FlowData datum : msg.DATA) {
				FlowDataSerializer serializer = datum.getSerializer();
				buf.writeString(serializer.getRegistryName().toString(), 128);
				serializer.toBuffer(
					datum,
					buf
				);
			}
		}

		@Override
		public ManagerFlowDataPacketC2S finishDecode(
			int windowId, BlockPos tilePos, PacketBuffer buf
		) {
			return new ManagerFlowDataPacketC2S(
				windowId,
				tilePos,
				IntStream.range(0, buf.readInt())
					.mapToObj(__ -> FlowDataSerializer
						.getSerializer(buf.readString(128))
						.get()
						.fromBuffer(buf))
					.toArray(FlowData[]::new)
			);
		}

		@Override
		public void handleDetailed(
			ManagerFlowDataPacketC2S msg, ManagerTileEntity manager
		) {
			for (FlowData datum : msg.DATA) {
				datum.addToDataContainer(manager.getFlowDataContainer());
			}

			manager.sendPacketToListeners(windowId ->
				new ManagerFlowDataPacketS2C(
					windowId,
					msg.DATA
				)
			);
		}
	}
}
