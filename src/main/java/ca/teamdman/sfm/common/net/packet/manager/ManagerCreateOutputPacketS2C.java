/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.screen.ManagerScreen;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.impl.FlowOutputData;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;

public class ManagerCreateOutputPacketS2C extends S2CManagerPacket {

	private final Position ELEMENT_POSITION;
	private final UUID ELEMENT_ID;

	public ManagerCreateOutputPacketS2C(int windowId, UUID elementId, Position elementPosition) {
		super(windowId);
		this.ELEMENT_ID = elementId;
		this.ELEMENT_POSITION = elementPosition;
	}

	public static class Handler extends S2CHandler<ManagerCreateOutputPacketS2C> {

		@Override
		public void finishEncode(
			ManagerCreateOutputPacketS2C msg,
			PacketBuffer buf
		) {
			SFMUtil.writeUUID(msg.ELEMENT_ID, buf);
			buf.writeLong(msg.ELEMENT_POSITION.toLong());
		}

		@Override
		public ManagerCreateOutputPacketS2C finishDecode(int windowId, PacketBuffer buf) {
			return new ManagerCreateOutputPacketS2C(
				windowId,
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong())
			);
		}

		@Override
		public void handleDetailed(
			ManagerScreen screen,
			ManagerCreateOutputPacketS2C msg
		) {
			SFM.LOGGER.debug(
				SFMUtil.getMarker(getClass()),
				"S2C received, creating input at position {} with id {}",
				msg.ELEMENT_POSITION,
				msg.ELEMENT_ID
			);
			screen.addData(new FlowOutputData(
				msg.ELEMENT_ID,
				msg.ELEMENT_POSITION
			));
		}
	}
}
