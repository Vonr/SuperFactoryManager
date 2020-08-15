package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.manager.ManagerScreen;
import ca.teamdman.sfm.common.flowdata.InputFlowData;
import ca.teamdman.sfm.common.flowdata.Position;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;

public class ManagerCreateInputPacketS2C extends S2CManagerPacket {

	private final int X, Y;
	private final UUID ELEMENT_ID;

	public ManagerCreateInputPacketS2C(int windowId, UUID elementId, int x, int y) {
		super(windowId);
		this.ELEMENT_ID = elementId;
		this.X = x;
		this.Y = y;
	}

	public static class Handler extends S2CHandler<ManagerCreateInputPacketS2C> {

		@Override
		public void finishEncode(ManagerCreateInputPacketS2C msg,
			PacketBuffer buf) {
			SFMUtil.writeUUID(msg.ELEMENT_ID, buf);
			buf.writeInt(msg.X);
			buf.writeInt(msg.Y);
		}

		@Override
		public ManagerCreateInputPacketS2C finishDecode(int windowId, PacketBuffer buf) {
			return new ManagerCreateInputPacketS2C(
				windowId,
				SFMUtil.readUUID(buf),
				buf.readInt(),
				buf.readInt()
			);
		}

		@Override
		public void handleDetailed(ManagerScreen screen,
			ManagerCreateInputPacketS2C msg) {
			screen.addData(new InputFlowData(msg.ELEMENT_ID, new Position(msg.X, msg.Y)));
			screen.CONTROLLER.loadFromScreenData();
		}
	}
}
