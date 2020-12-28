/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.net.packet.manager.put;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.screen.ManagerScreen;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.impl.TileOutputFlowData;
import ca.teamdman.sfm.common.net.packet.manager.S2CManagerPacket;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.network.PacketBuffer;

public class ManagerFlowOutputDataPacketS2C extends S2CManagerPacket {

	private final Position ELEMENT_POSITION;
	private final UUID ELEMENT_ID;
	private final List<UUID> RULES;


	public ManagerFlowOutputDataPacketS2C(int windowId, UUID elementId, Position elementPosition, List<UUID> rules) {
		super(windowId);
		this.ELEMENT_ID = elementId;
		this.ELEMENT_POSITION = elementPosition;
		this.RULES = rules;
	}

	public static class Handler extends S2CHandler<ManagerFlowOutputDataPacketS2C> {

		@Override
		public void finishEncode(
			ManagerFlowOutputDataPacketS2C msg,
			PacketBuffer buf
		) {
			SFMUtil.writeUUID(msg.ELEMENT_ID, buf);
			buf.writeLong(msg.ELEMENT_POSITION.toLong());
			buf.writeInt(msg.RULES.size());
			msg.RULES.forEach(id -> buf.writeString(id.toString()));
		}

		@Override
		public ManagerFlowOutputDataPacketS2C finishDecode(int windowId, PacketBuffer buf) {
			return new ManagerFlowOutputDataPacketS2C(
				windowId,
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong()),
				IntStream.range(0, buf.readInt())
					.mapToObj(__ -> SFMUtil.readUUID(buf))
					.collect(Collectors.toList())
			);
		}
		@Override
		public void handleDetailed(
			ManagerScreen screen,
			ManagerFlowOutputDataPacketS2C msg
		) {
			SFM.LOGGER.debug(
				SFMUtil.getMarker(getClass()),
				"S2C received, creating input with {} rules at position {} with id {}",
				msg.RULES.size(),
				msg.ELEMENT_POSITION,
				msg.ELEMENT_ID
			);

			screen.addData(new TileOutputFlowData(
				msg.ELEMENT_ID,
				msg.ELEMENT_POSITION,
				msg.RULES
			));
		}
	}
}
