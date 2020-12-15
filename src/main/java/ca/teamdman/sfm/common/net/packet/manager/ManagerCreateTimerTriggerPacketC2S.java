/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.impl.FlowTimerTriggerData;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class ManagerCreateTimerTriggerPacketC2S extends C2SManagerPacket {

	private final Position ELEMENT_POSITION;

	public ManagerCreateTimerTriggerPacketC2S(int WINDOW_ID, BlockPos TILE_POSITION,
		Position POSITION) {
		super(WINDOW_ID, TILE_POSITION);
		this.ELEMENT_POSITION = POSITION;
	}

	public static class Handler extends C2SHandler<ManagerCreateTimerTriggerPacketC2S> {

		@Override
		public void finishEncode(
			ManagerCreateTimerTriggerPacketC2S msg,
			PacketBuffer buf) {
			buf.writeLong(msg.ELEMENT_POSITION.toLong());
		}

		@Override
		public ManagerCreateTimerTriggerPacketC2S finishDecode(int windowId, BlockPos tilePos,
			PacketBuffer buf) {
			return new ManagerCreateTimerTriggerPacketC2S(
				windowId,
				tilePos,
				Position.fromLong(buf.readLong())
			);
		}

		@Override
		public void handleDetailed(
			ManagerCreateTimerTriggerPacketC2S msg,
			ManagerTileEntity manager
		) {
			FlowData data = new FlowTimerTriggerData(UUID.randomUUID(), msg.ELEMENT_POSITION, 20);
			SFM.LOGGER.debug(
				SFMUtil.getMarker(getClass()),
				"C2S received, creating trigger with id {}",
				data.getId()
			);
			manager.addData(data);

			manager.sendPacketToListeners(
				new ManagerCreateTimerTriggerPacketS2C(
					msg.WINDOW_ID,
					data.getId(),
					msg.ELEMENT_POSITION
				)
			);
		}
	}
}
