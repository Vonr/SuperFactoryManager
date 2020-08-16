package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.manager.ManagerScreen;
import ca.teamdman.sfm.common.flowdata.RelationshipFlowData;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;

public class ManagerCreateRelationshipPacketS2C extends S2CManagerPacket {

	private final UUID ELEMENT_ID, FROM_ID, TO_ID;

	public ManagerCreateRelationshipPacketS2C(
		int windowId, UUID elementId, UUID fromId,
		UUID toId
	) {
		super(windowId);
		this.ELEMENT_ID = elementId;
		this.FROM_ID = fromId;
		this.TO_ID = toId;
	}

	public static class Handler extends S2CHandler<ManagerCreateRelationshipPacketS2C> {

		@Override
		public void finishEncode(
			ManagerCreateRelationshipPacketS2C msg,
			PacketBuffer buf
		) {
			SFMUtil.writeUUID(msg.ELEMENT_ID, buf);
			SFMUtil.writeUUID(msg.FROM_ID, buf);
			SFMUtil.writeUUID(msg.TO_ID, buf);
		}

		@Override
		public ManagerCreateRelationshipPacketS2C finishDecode(int windowId, PacketBuffer buf) {
			return new ManagerCreateRelationshipPacketS2C(
				windowId,
				SFMUtil.readUUID(buf),
				SFMUtil.readUUID(buf),
				SFMUtil.readUUID(buf)
			);
		}

		@Override
		public void handleDetailed(
			ManagerScreen screen,
			ManagerCreateRelationshipPacketS2C msg
		) {
			SFM.LOGGER.debug(
				SFMUtil.getMarker(getClass()),
				"S2C Received, creating relationship between {} and {} with id {}",
				msg.FROM_ID,
				msg.TO_ID,
				msg.ELEMENT_ID
			);
			screen.addData(new RelationshipFlowData(msg.ELEMENT_ID, msg.FROM_ID, msg.TO_ID));
			screen.CONTROLLER.loadFromScreenData();
		}
	}
}
