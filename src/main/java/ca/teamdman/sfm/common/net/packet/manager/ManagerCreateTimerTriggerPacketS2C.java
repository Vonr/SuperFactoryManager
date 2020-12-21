/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.screen.ManagerScreen;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.impl.FlowTimerTriggerData;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;

public class ManagerCreateTimerTriggerPacketS2C extends S2CManagerPacket {

	private final UUID ELEMENT_ID;
	private final Position ELEMENT_POSITION;

	public ManagerCreateTimerTriggerPacketS2C(
		int windowId,
		UUID elementId,
		Position elementPos
	) {
		super(windowId);
		this.ELEMENT_ID = elementId;
		this.ELEMENT_POSITION = elementPos;
	}

	public static class Handler extends S2CHandler<ManagerCreateTimerTriggerPacketS2C> {

		@Override
		public void finishEncode(ManagerCreateTimerTriggerPacketS2C msg, PacketBuffer buf) {
			SFMUtil.writeUUID(msg.ELEMENT_ID, buf);
			buf.writeLong(msg.ELEMENT_POSITION.toLong());
		}

		@Override
		public ManagerCreateTimerTriggerPacketS2C finishDecode(int windowId, PacketBuffer buf) {
			return new ManagerCreateTimerTriggerPacketS2C(
				windowId,
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong())
			);
		}

		@Override
		public void handleDetailed(ManagerScreen screen, ManagerCreateTimerTriggerPacketS2C msg) {
			SFM.LOGGER.debug(
				SFMUtil.getMarker(getClass()),
				"S2C received, creating trigger with id {}",
				msg.ELEMENT_ID
			);
			screen.addData(new FlowTimerTriggerData(
				msg.ELEMENT_ID,
				msg.ELEMENT_POSITION,
				20
			));
		}
	}
}
