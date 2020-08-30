package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.common.flowdata.impl.FlowInputData;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class ManagerToggleInputSelectedC2S extends C2SManagerPacket {

	private final UUID DATA_ID;
	private final BlockPos BLOCK_POS;
	private final boolean SELECTED;

	public ManagerToggleInputSelectedC2S(
		int WINDOW_ID, BlockPos TILE_POSITION, UUID DATA_ID, BlockPos BLOCK_POS, boolean SELECTED
	) {
		super(WINDOW_ID, TILE_POSITION);
		this.DATA_ID = DATA_ID;
		this.BLOCK_POS = BLOCK_POS;
		this.SELECTED = SELECTED;
	}

	public static class Handler extends C2SHandler<ManagerToggleInputSelectedC2S> {

		@Override
		public void finishEncode(ManagerToggleInputSelectedC2S msg, PacketBuffer buf) {
			SFMUtil.writeUUID(msg.DATA_ID, buf);
			buf.writeBlockPos(msg.BLOCK_POS);
			buf.writeBoolean(msg.SELECTED);
		}

		@Override
		public ManagerToggleInputSelectedC2S finishDecode(
			int windowId, BlockPos tilePos,
			PacketBuffer buf
		) {
			return new ManagerToggleInputSelectedC2S(windowId, tilePos,
				SFMUtil.readUUID(buf),
				buf.readBlockPos(),
				buf.readBoolean()
			);
		}

		@Override
		public void handleDetailed(
			ManagerToggleInputSelectedC2S msg,
			ManagerTileEntity manager
		) {
			SFM.LOGGER.debug(
				SFMUtil.getMarker(getClass()),
				"C2S Received, setting selected for data {} pos {} to {}",
				msg.DATA_ID,
				msg.BLOCK_POS,
				msg.SELECTED
			);
			manager.getData(msg.DATA_ID, FlowInputData.class).ifPresent(data -> {
				data.setSelected(msg.BLOCK_POS, msg.SELECTED);
				manager.sendPacketToListeners(new ManagerToggleInputSelectedS2C(
					msg.WINDOW_ID,
					msg.DATA_ID,
					msg.BLOCK_POS,
					msg.SELECTED
				));
			});
		}
	}
}
