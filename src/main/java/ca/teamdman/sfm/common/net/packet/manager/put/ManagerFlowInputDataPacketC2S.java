/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.net.packet.manager.put;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.impl.FlowTileInputData;
import ca.teamdman.sfm.common.net.packet.manager.C2SManagerPacket;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class ManagerFlowInputDataPacketC2S extends C2SManagerPacket {
	private final Position ELEMENT_POSITION;
	private final UUID ELEMENT_ID;
	private final List<UUID> RULES;

	public ManagerFlowInputDataPacketC2S(int windowId, BlockPos pos, UUID elementId, Position elementPosition, List<UUID> rules) {
		super(windowId, pos);
		this.ELEMENT_ID = elementId;
		this.ELEMENT_POSITION = elementPosition;
		this.RULES = rules;
	}

	public static class Handler extends C2SManagerPacket.C2SHandler<ManagerFlowInputDataPacketC2S> {

		@Override
		public void finishEncode(
			ManagerFlowInputDataPacketC2S msg,
			PacketBuffer buf
		) {
			buf.writeString(msg.ELEMENT_ID.toString());
			buf.writeLong(msg.ELEMENT_POSITION.toLong());
			buf.writeInt(msg.RULES.size());
			msg.RULES.forEach(id -> buf.writeString(id.toString()));
		}

		@Override
		public ManagerFlowInputDataPacketC2S finishDecode(
			int windowId, BlockPos tilePos,
			PacketBuffer buf
		) {
			return new ManagerFlowInputDataPacketC2S(
				windowId,
				tilePos,
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong()),
				IntStream.range(0, buf.readInt())
					.mapToObj(__ -> SFMUtil.readUUID(buf))
					.collect(Collectors.toList())
			);
		}

		@Override
		public void handleDetailed(ManagerFlowInputDataPacketC2S msg, ManagerTileEntity manager) {
			FlowTileInputData data = new FlowTileInputData(
				msg.ELEMENT_ID,
				msg.ELEMENT_POSITION,
				msg.RULES
			);

			SFM.LOGGER.debug(
				SFMUtil.getMarker(getClass()),
				"C2S received, creating input with {} rules at position {} with id {}",
				msg.RULES.size(),
				msg.ELEMENT_POSITION,
				msg.ELEMENT_ID
			);

			manager.addData(data);
			manager.markAndNotify();
			manager.sendPacketToListeners(new ManagerFlowInputDataPacketS2C(
				msg.WINDOW_ID,
				msg.ELEMENT_ID,
				msg.ELEMENT_POSITION,
				msg.RULES
			));
		}
	}
}
