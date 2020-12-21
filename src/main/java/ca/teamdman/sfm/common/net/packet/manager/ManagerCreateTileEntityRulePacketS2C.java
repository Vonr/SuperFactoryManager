/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.screen.ManagerScreen;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.impl.FlowTileEntityRuleData;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;

public class ManagerCreateTileEntityRulePacketS2C extends S2CManagerPacket {

	private final Position ELEMENT_POSITION;
	private final UUID ELEMENT_ID;
	private final UUID OWNER_ID;

	public ManagerCreateTileEntityRulePacketS2C(int windowId, UUID elementId, UUID ownerId, Position elementPosition) {
		super(windowId);
		this.ELEMENT_ID = elementId;
		this.OWNER_ID = ownerId;
		this.ELEMENT_POSITION = elementPosition;
	}

	public static class Handler extends S2CHandler<ManagerCreateTileEntityRulePacketS2C> {

		@Override
		public void finishEncode(
			ManagerCreateTileEntityRulePacketS2C msg,
			PacketBuffer buf
		) {
			SFMUtil.writeUUID(msg.ELEMENT_ID, buf);
			SFMUtil.writeUUID(msg.OWNER_ID, buf);
			buf.writeLong(msg.ELEMENT_POSITION.toLong());
		}

		@Override
		public ManagerCreateTileEntityRulePacketS2C finishDecode(int windowId, PacketBuffer buf) {
			return new ManagerCreateTileEntityRulePacketS2C(
				windowId,
				SFMUtil.readUUID(buf),
				SFMUtil.readUUID(buf),
				Position.fromLong(buf.readLong())
			);
		}

		@Override
		public void handleDetailed(
			ManagerScreen screen,
			ManagerCreateTileEntityRulePacketS2C msg
		) {
			SFM.LOGGER.debug(
				SFMUtil.getMarker(getClass()),
				"S2C received, creating TileEntityRule at position {} with id {}",
				msg.ELEMENT_POSITION,
				msg.ELEMENT_ID
			);
			FlowTileEntityRuleData data = new FlowTileEntityRuleData(
				msg.ELEMENT_ID,
				msg.OWNER_ID,
				msg.ELEMENT_POSITION
			);
			screen.addData(data);
			screen.notifyChanged(msg.OWNER_ID);
//			screen.CONTROLLER.getChildren().stream()
//				.filter(c -> c instanceof TileEntityRuleDrawer
//					&& ((TileEntityRuleDrawer) c).getData().getId().equals(msg.OWNER_ID))
//				.forEach(c -> ((TileEntityRuleDrawer) c).addChild());
		}
	}
}
