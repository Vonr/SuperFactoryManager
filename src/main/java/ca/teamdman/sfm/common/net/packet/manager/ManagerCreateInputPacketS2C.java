package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.screen.ManagerScreen;
import ca.teamdman.sfm.common.flowdata.core.Position;
import ca.teamdman.sfm.common.flowdata.impl.FlowInputData;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;

public class ManagerCreateInputPacketS2C extends S2CManagerPacket {

	private final Position ELEMENT_POSITION;
	private final UUID ELEMENT_ID;

	public ManagerCreateInputPacketS2C(int windowId, UUID elementId, Position elementPosition) {
		super(windowId);
		this.ELEMENT_ID = elementId;
		this.ELEMENT_POSITION = elementPosition;
	}

	public static class Handler extends S2CHandler<ManagerCreateInputPacketS2C> {

		@Override
		public void finishEncode(
			ManagerCreateInputPacketS2C msg,
			PacketBuffer buf
		) {
			SFMUtil.writeUUID(msg.ELEMENT_ID, buf);
			buf.writeLong(msg.ELEMENT_POSITION.toLong());
		}

		@Override
		public ManagerCreateInputPacketS2C finishDecode(int windowId, PacketBuffer buf) {
			return new ManagerCreateInputPacketS2C(
				windowId,
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong())
			);
		}

		@Override
		public void handleDetailed(
			ManagerScreen screen,
			ManagerCreateInputPacketS2C msg
		) {
			SFM.LOGGER.debug(
				SFMUtil.getMarker(getClass()),
				"S2C received, creating input at position {} with id {}",
				msg.ELEMENT_POSITION,
				msg.ELEMENT_ID
			);
			screen.addData(new FlowInputData(
				msg.ELEMENT_ID,
				msg.ELEMENT_POSITION
			));
			screen.CONTROLLER.onDataChange();
		}
	}
}
