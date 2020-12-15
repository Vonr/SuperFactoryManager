/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.screen.ManagerScreen;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;

public class ManagerDeletePacketS2C extends S2CManagerPacket {
	private final UUID ELEMENT_ID;

	public ManagerDeletePacketS2C(int windowId, UUID elementId) {
		super(windowId);
		this.ELEMENT_ID = elementId;
	}

	public static class Handler extends S2CHandler<ManagerDeletePacketS2C> {

		@Override
		public void finishEncode(
			ManagerDeletePacketS2C msg,
			PacketBuffer buf
		) {
			SFMUtil.writeUUID(msg.ELEMENT_ID, buf);
		}

		@Override
		public ManagerDeletePacketS2C finishDecode(int windowId, PacketBuffer buf) {
			return new ManagerDeletePacketS2C(
				windowId,
				SFMUtil.readUUID(buf)
			);
		}

		@Override
		public void handleDetailed(
			ManagerScreen screen,
			ManagerDeletePacketS2C msg
		) {
			SFM.LOGGER.debug(
				SFMUtil.getMarker(getClass()),
				"S2C received, deleting element with id {}",
				msg.ELEMENT_ID
			);
			screen.removeData(msg.ELEMENT_ID);
			screen.CONTROLLER.onDataChange();
		}
	}
}
