package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.manager.ManagerScreen;
import ca.teamdman.sfm.common.flowdata.LineNodeFlowData;
import ca.teamdman.sfm.common.flowdata.Position;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;

public class ManagerCreateLineNodePacketS2C extends S2CManagerPacket {
	private final UUID ELEMENT_ID;
	private final Position ELEMENT_POSITION;

	public ManagerCreateLineNodePacketS2C(int windowId, UUID elementId, Position elementPos) {
		super(windowId);
		this.ELEMENT_ID = elementId;
		this.ELEMENT_POSITION = elementPos;
	}

	public static class Handler extends S2CHandler<ManagerCreateLineNodePacketS2C> {
		@Override
		public void finishEncode(ManagerCreateLineNodePacketS2C msg, PacketBuffer buf) {
			SFMUtil.writeUUID(msg.ELEMENT_ID, buf);
			buf.writeLong(msg.ELEMENT_POSITION.toLong());
		}

		@Override
		public ManagerCreateLineNodePacketS2C finishDecode(int windowId, PacketBuffer buf) {
			return new ManagerCreateLineNodePacketS2C(
				windowId,
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong())
			);
		}

		@Override
		public void handleDetailed(ManagerScreen screen, ManagerCreateLineNodePacketS2C msg) {
			screen.addData(new LineNodeFlowData(msg.ELEMENT_ID, msg.ELEMENT_POSITION));
			screen.CONTROLLER.loadFromScreenData();
		}
	}
}
