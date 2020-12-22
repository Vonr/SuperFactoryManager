/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.net.packet.manager.patch;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.screen.ManagerScreen;
import ca.teamdman.sfm.common.flow.data.core.FlowDataContainer.ChangeType;
import ca.teamdman.sfm.common.flow.data.core.SelectionHolder;
import ca.teamdman.sfm.common.net.packet.manager.S2CManagerPacket;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class ManagerToggleBlockPosSelectedS2C extends S2CManagerPacket {

	private final UUID DATA_ID;
	private final BlockPos BLOCK_POS;
	private final boolean SELECTED;
	public ManagerToggleBlockPosSelectedS2C(
		int windowId,
		UUID DATA_ID,
		BlockPos BLOCK_POS,
		boolean SELECTED
	) {
		super(windowId);
		this.DATA_ID = DATA_ID;
		this.BLOCK_POS = BLOCK_POS;
		this.SELECTED = SELECTED;
	}

	public static class Handler extends S2CHandler<ManagerToggleBlockPosSelectedS2C> {

		@Override
		public void finishEncode(ManagerToggleBlockPosSelectedS2C msg, PacketBuffer buf) {
			SFMUtil.writeUUID(msg.DATA_ID, buf);
			buf.writeBlockPos(msg.BLOCK_POS);
			buf.writeBoolean(msg.SELECTED);
		}

		@Override
		public ManagerToggleBlockPosSelectedS2C finishDecode(int windowId, PacketBuffer buf) {
			return new ManagerToggleBlockPosSelectedS2C(
				windowId,
				SFMUtil.readUUID(buf),
				buf.readBlockPos(),
				buf.readBoolean()
			);
		}

		@Override
		public void handleDetailed(ManagerScreen screen, ManagerToggleBlockPosSelectedS2C msg) {
			SFM.LOGGER.debug(
				SFMUtil.getMarker(getClass()),
				"S2C received, toggling input selected for {} pos {} value {}",
				msg.DATA_ID,
				msg.BLOCK_POS,
				msg.SELECTED
			);
			screen.getData(msg.DATA_ID)
				.filter(data -> data instanceof SelectionHolder
					&& ((SelectionHolder<?>) data).getSelectionType() == BlockPos.class)
				.ifPresent(data ->
					((SelectionHolder<BlockPos>) data).setSelected(msg.BLOCK_POS, msg.SELECTED));
			screen.notifyChanged(msg.DATA_ID, ChangeType.UPDATED);
		}
	}
}
