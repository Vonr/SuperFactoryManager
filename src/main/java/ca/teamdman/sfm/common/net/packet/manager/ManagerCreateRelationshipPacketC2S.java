/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class ManagerCreateRelationshipPacketC2S extends C2SManagerPacket {

	private final UUID FROM_ID, TO_ID;

	public ManagerCreateRelationshipPacketC2S(
		int WINDOW_ID, BlockPos TILE_POSITION, UUID FROM_ID, UUID TO_ID
	) {
		super(WINDOW_ID, TILE_POSITION);
		this.FROM_ID = FROM_ID;
		this.TO_ID = TO_ID;
	}

	public static class Handler extends C2SHandler<ManagerCreateRelationshipPacketC2S> {

		@Override
		public void finishEncode(ManagerCreateRelationshipPacketC2S msg, PacketBuffer buf) {
			SFMUtil.writeUUID(msg.FROM_ID, buf);
			SFMUtil.writeUUID(msg.TO_ID, buf);
		}

		@Override
		public ManagerCreateRelationshipPacketC2S finishDecode(
			int windowId, BlockPos tilePos,
			PacketBuffer buf
		) {
			return new ManagerCreateRelationshipPacketC2S(windowId, tilePos,
				SFMUtil.readUUID(buf),
				SFMUtil.readUUID(buf)
			);
		}

		@Override
		public void handleDetailed(
			ManagerCreateRelationshipPacketC2S msg,
			ManagerTileEntity manager
		) {
			SFM.LOGGER.debug(
				SFMUtil.getMarker(getClass()),
				"C2S Received, creating relationship between {} and {}",
				msg.FROM_ID,
				msg.TO_ID
			);
			manager.addRelationship(msg.FROM_ID, msg.TO_ID).ifPresent(relId ->
				manager.sendPacketToListeners(new ManagerCreateRelationshipPacketS2C(
					msg.WINDOW_ID,
					relId,
					msg.FROM_ID,
					msg.TO_ID
				))
			);
		}
	}
}
