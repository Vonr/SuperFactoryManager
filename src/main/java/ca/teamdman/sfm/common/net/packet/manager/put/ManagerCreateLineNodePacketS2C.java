/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.net.packet.manager.put;

import ca.teamdman.sfm.client.gui.screen.ManagerScreen;
import ca.teamdman.sfm.common.flow.FlowUtils;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.net.packet.S2CContainerPacket;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;

public final class ManagerCreateLineNodePacketS2C extends S2CContainerPacket<ManagerScreen> {

	private final UUID FROM_ID, TO_ID, NODE_ID, FROM_TO_NODE_ID, TO_TO_NODE_ID;
	private final Position ELEMENT_POSITION;

	public ManagerCreateLineNodePacketS2C(
		int windowId,
		UUID fromId,
		UUID toId,
		UUID nodeId,
		UUID fromToNodeId,
		UUID toToNodeID,
		Position elementPos
	) {
		super(ManagerScreen.class, windowId);
		this.FROM_ID = fromId;
		this.TO_ID = toId;
		this.NODE_ID = nodeId;
		this.FROM_TO_NODE_ID = fromToNodeId;
		this.TO_TO_NODE_ID = toToNodeID;
		this.ELEMENT_POSITION = elementPos;
	}

	public static final class Handler extends
		S2CContainerPacketHandler<ManagerScreen, ManagerCreateLineNodePacketS2C> {

		@Override
		public void finishEncode(ManagerCreateLineNodePacketS2C msg, PacketBuffer buf) {
			SFMUtil.writeUUID(msg.FROM_ID, buf);
			SFMUtil.writeUUID(msg.TO_ID, buf);
			SFMUtil.writeUUID(msg.NODE_ID, buf);
			SFMUtil.writeUUID(msg.FROM_TO_NODE_ID, buf);
			SFMUtil.writeUUID(msg.TO_TO_NODE_ID, buf);
			buf.writeLong(msg.ELEMENT_POSITION.toLong());
		}

		@Override
		public ManagerCreateLineNodePacketS2C finishDecode(int windowId, PacketBuffer buf) {
			return new ManagerCreateLineNodePacketS2C(
				windowId,
				SFMUtil.readUUID(buf),
				SFMUtil.readUUID(buf),
				SFMUtil.readUUID(buf),
				SFMUtil.readUUID(buf),
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong())
			);
		}

		@Override
		public void handleDetailed(ManagerScreen screen, ManagerCreateLineNodePacketS2C msg) {
			FlowUtils.insertLineNode(
				screen.getFlowDataContainer(),
				msg.FROM_ID,
				msg.TO_ID,
				msg.NODE_ID,
				msg.FROM_TO_NODE_ID,
				msg.TO_TO_NODE_ID,
				msg.ELEMENT_POSITION
			);
		}
	}
}
