package ca.teamdman.sfm.common.net.packet.workstation;

import ca.teamdman.sfm.client.gui.screen.WorkstationScreen;
import ca.teamdman.sfm.common.net.packet.S2CContainerPacket;
import net.minecraft.network.PacketBuffer;

public final class S2CWorkstationAutoLearnChangedPacket extends S2CContainerPacket<WorkstationScreen> {
	public final boolean AUTO_LEARN_ENABLED;

	public S2CWorkstationAutoLearnChangedPacket(
		int windowId,
		boolean autoLearnEnabled
	) {
		super(WorkstationScreen.class, windowId);
		this.AUTO_LEARN_ENABLED = autoLearnEnabled;
	}

	public static final class Handler extends S2CContainerPacketHandler<WorkstationScreen, S2CWorkstationAutoLearnChangedPacket> {

		@Override
		public void finishEncode(
			S2CWorkstationAutoLearnChangedPacket msg,
			PacketBuffer buf
		) {
			buf.writeBoolean(msg.AUTO_LEARN_ENABLED);
		}

		@Override
		public S2CWorkstationAutoLearnChangedPacket finishDecode(
			int windowId, PacketBuffer buf
		) {
			return new S2CWorkstationAutoLearnChangedPacket(
				windowId,
				buf.readBoolean()
			);
		}

		@Override
		public void handleDetailed(
			WorkstationScreen screen,
			S2CWorkstationAutoLearnChangedPacket msg
		) {
			screen
				.getMenu()
				.getSource()
				.setAutoLearnEnabled(msg.AUTO_LEARN_ENABLED);

			screen.init(
				screen.getMinecraft(),
				screen.width,
				screen.height
			);
		}
	}
}
