/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.net.packet.manager.delete;

import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.net.packet.C2SContainerPacket;
import ca.teamdman.sfm.common.tile.manager.ManagerTileEntity;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public final class ManagerDeletePacketC2S extends
	C2SContainerPacket<ManagerTileEntity, ManagerContainer> {

	private final UUID ELEMENT_ID;

	public ManagerDeletePacketC2S(int windowId, BlockPos pos, UUID elementId) {
		super(ManagerTileEntity.class, ManagerContainer.class, windowId, pos);
		this.ELEMENT_ID = elementId;
	}

	public static final class Handler extends
		C2SContainerPacketHandler<ManagerTileEntity, ManagerContainer, ManagerDeletePacketC2S> {

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
		public void handleDetailed(
			Supplier<Context> ctx,
			ManagerDeletePacketC2S msg,
			ManagerTileEntity manager
		) {
			BasicFlowDataContainer container = manager.getFlowDataContainer();
			container.get(msg.ELEMENT_ID)
				.ifPresent(data -> data.removeFromDataContainer(container));
			manager.markAndNotify();
			manager.sendPacketToListeners(
				windowId -> new ManagerDeletePacketS2C(windowId, msg.ELEMENT_ID)
			);
		}
	}
}
