package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.flow.core.IFlowController;
import ca.teamdman.sfm.client.gui.screen.ManagerScreen;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.core.PositionHolder;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;

public class ManagerPositionPacketS2C extends S2CManagerPacket {

	private final Position ELEMENT_POSITION;
	private final UUID ELEMENT_ID;

	public ManagerPositionPacketS2C(int windowId, UUID elementId, Position elementPos) {
		super(windowId);
		this.ELEMENT_ID = elementId;
		this.ELEMENT_POSITION = elementPos;
	}

	public static class Handler extends S2CHandler<ManagerPositionPacketS2C> {

		@Override
		public void finishEncode(ManagerPositionPacketS2C msg, PacketBuffer buf) {
			SFMUtil.writeUUID(msg.ELEMENT_ID, buf);
			buf.writeLong(msg.ELEMENT_POSITION.toLong());
		}

		@Override
		public ManagerPositionPacketS2C finishDecode(int windowId, PacketBuffer buf) {
			return new ManagerPositionPacketS2C(
				windowId,
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong())
			);
		}

		@Override
		public void handleDetailed(
			ManagerScreen screen,
			ManagerPositionPacketS2C msg
		) {
			SFM.LOGGER.debug(
				SFMUtil.getMarker(getClass()),
				"S2C Received, setting pos to {} for element {}",
				msg.ELEMENT_POSITION,
				msg.ELEMENT_ID
			);
			screen.getData(msg.ELEMENT_ID)
				.filter(data -> data instanceof PositionHolder)
				.map(data -> ((PositionHolder) data).getPosition())
				.ifPresent(pos -> pos.setXY(msg.ELEMENT_POSITION));
			screen.CONTROLLER.getController(msg.ELEMENT_ID)
				.ifPresent(IFlowController::onDataChange);
//			screen.CONTROLLER.loadFromScreenData();
		}
	}
}
