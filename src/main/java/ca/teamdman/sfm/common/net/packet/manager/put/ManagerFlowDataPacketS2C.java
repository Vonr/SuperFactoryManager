package ca.teamdman.sfm.common.net.packet.manager.put;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.gui.screen.ManagerScreen;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.FlowDataSerializer;
import ca.teamdman.sfm.common.net.packet.manager.S2CManagerPacket;
import net.minecraft.network.PacketBuffer;

public class ManagerFlowDataPacketS2C extends S2CManagerPacket {
	public final FlowData DATA;
	public ManagerFlowDataPacketS2C(int WINDOW_ID, FlowData data) {
		super(WINDOW_ID);
		this.DATA = data;
	}

	public static class Handler extends S2CHandler<ManagerFlowDataPacketS2C> {

		@Override
		public void finishEncode(
			ManagerFlowDataPacketS2C msg, PacketBuffer buf
		) {
			FlowDataSerializer serializer = msg.DATA.getSerializer();
			buf.writeString(serializer.getRegistryName().toString());
			serializer.toBuffer(
				msg.DATA,
				buf
			);
		}

		@Override
		public ManagerFlowDataPacketS2C finishDecode(int windowId, PacketBuffer buf) {
			return new ManagerFlowDataPacketS2C(
				windowId,
				FlowDataSerializer.getSerializer(buf.readString()).get().fromBuffer(buf)
			);
		}

		@Override
		public void handleDetailed(
			ManagerScreen screen, ManagerFlowDataPacketS2C msg
		) {
			SFM.LOGGER.debug("S2C received, FlowData {}", msg.DATA);
			msg.DATA.addToDataContainer(screen.getFlowDataContainer());
		}
	}
}
