/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.net.packet.manager.delete;

import ca.teamdman.sfm.common.net.packet.manager.C2SManagerPacket;
import ca.teamdman.sfm.common.tile.manager.ManagerTileEntity;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class ManagerDeletePacketC2S extends C2SManagerPacket {

	private final UUID ELEMENT_ID;

	public ManagerDeletePacketC2S(int windowId, BlockPos pos, UUID elementId) {
		super(windowId, pos);
		this.ELEMENT_ID = elementId;
	}

	public static class Handler extends C2SHandler<ManagerDeletePacketC2S> {

		@Override
		public void finishEncode(
			ManagerDeletePacketC2S msg,
			PacketBuffer buf
		) {
			SFMUtil.writeUUID(msg.ELEMENT_ID, buf);
		}

		@Override
		public ManagerDeletePacketC2S finishDecode(
			int windowId, BlockPos tilePos,
			PacketBuffer buf
		) {
			return new ManagerDeletePacketC2S(
				windowId,
				tilePos,
				SFMUtil.readUUID(buf)
			);
		}

		@Override
		public void handleDetailed(ManagerDeletePacketC2S msg, ManagerTileEntity manager) {
			manager.getFlowDataContainer().remove(msg.ELEMENT_ID);
			manager.markAndNotify();
			manager.sendPacketToListeners(
				windowId -> new ManagerDeletePacketS2C(windowId, msg.ELEMENT_ID)
			);
		}
	}
}
