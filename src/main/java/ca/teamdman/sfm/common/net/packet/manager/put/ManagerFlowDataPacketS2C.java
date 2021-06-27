package ca.teamdman.sfm.common.net.packet.manager.put;

import ca.teamdman.sfm.client.gui.screen.ManagerScreen;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.FlowDataSerializer;
import ca.teamdman.sfm.common.net.packet.S2CContainerPacket;
import java.util.stream.IntStream;
import net.minecraft.network.PacketBuffer;

public final class ManagerFlowDataPacketS2C extends
	S2CContainerPacket<ManagerScreen> {

	public final FlowData[] DATA;

	public ManagerFlowDataPacketS2C(int WINDOW_ID, FlowData... data) {
		super(ManagerScreen.class, WINDOW_ID);
		this.DATA = data;
	}

	public static final class Handler extends
		S2CContainerPacketHandler<ManagerScreen, ManagerFlowDataPacketS2C> {

		@Override
		public void finishEncode(
			ManagerFlowDataPacketS2C msg, PacketBuffer buf
		) {
			buf.writeInt(msg.DATA.length);
			for (FlowData datum : msg.DATA) {
				FlowDataSerializer serializer = datum.getSerializer();
				buf.writeUtf(serializer.getRegistryName().toString(), 128);
				serializer.toBuffer(
					datum,
					buf
				);
			}
		}

		@Override
		public ManagerFlowDataPacketS2C finishDecode(
			int windowId,
			PacketBuffer buf
		) {
			return new ManagerFlowDataPacketS2C(
				windowId,
				IntStream.range(0, buf.readInt())
					.mapToObj(__ -> FlowDataSerializer
						.getSerializer(buf.readUtf(128))
						.get()
						.fromBuffer(buf))
					.toArray(FlowData[]::new)
			);
		}

		@Override
		public void handleDetailed(
			ManagerScreen screen, ManagerFlowDataPacketS2C msg
		) {
			for (FlowData datum : msg.DATA) {
				datum.addToDataContainer(screen.getFlowDataContainer());
			}
		}
	}
}
