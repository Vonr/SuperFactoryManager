package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.screen.ManagerScreen;
import ca.teamdman.sfm.common.flowdata.impl.FlowInputData;
import java.util.UUID;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class ManagerToggleInputSelectedS2C extends S2CManagerPacket {

	private final UUID DATA_ID;
	private final BlockPos BLOCK_POS;
	private final boolean SELECTED;
	public ManagerToggleInputSelectedS2C(
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

	public static class Handler extends S2CHandler<ManagerToggleInputSelectedS2C> {

		@Override
		public void finishEncode(ManagerToggleInputSelectedS2C msg, PacketBuffer buf) {
			SFMUtil.writeUUID(msg.DATA_ID, buf);
			buf.writeBlockPos(msg.BLOCK_POS);
			buf.writeBoolean(msg.SELECTED);
		}

		@Override
		public ManagerToggleInputSelectedS2C finishDecode(int windowId, PacketBuffer buf) {
			return new ManagerToggleInputSelectedS2C(
				windowId,
				SFMUtil.readUUID(buf),
				buf.readBlockPos(),
				buf.readBoolean()
			);
		}

		@Override
		public void handleDetailed(ManagerScreen screen, ManagerToggleInputSelectedS2C msg) {
			SFM.LOGGER.debug(
				SFMUtil.getMarker(getClass()),
				"S2C received, toggling input selected for {} pos {} value {}",
				msg.DATA_ID,
				msg.BLOCK_POS,
				msg.SELECTED
			);
			screen.getData(msg.DATA_ID, FlowInputData.class)
				.ifPresent(data -> data.setSelected(msg.BLOCK_POS, msg.SELECTED));
			screen.CONTROLLER.onDataChange();
		}
	}
}
