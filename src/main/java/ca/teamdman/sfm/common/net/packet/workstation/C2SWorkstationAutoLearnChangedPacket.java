package ca.teamdman.sfm.common.net.packet.workstation;

import ca.teamdman.sfm.common.container.WorkstationContainer;
import ca.teamdman.sfm.common.net.packet.C2SContainerPacket;
import ca.teamdman.sfm.common.tile.WorkstationTileEntity;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public final class C2SWorkstationAutoLearnChangedPacket extends
	C2SContainerPacket<WorkstationTileEntity, WorkstationContainer> {
	public final boolean AUTO_LEARN_ENABLED;

	public C2SWorkstationAutoLearnChangedPacket(
		int windowId,
		BlockPos tilePosition, boolean autoLearnEnabled
	) {
		super(WorkstationTileEntity.class, WorkstationContainer.class, windowId, tilePosition);
		this.AUTO_LEARN_ENABLED = autoLearnEnabled;
	}

	public static final class Handler extends C2SContainerPacketHandler<WorkstationTileEntity, WorkstationContainer, C2SWorkstationAutoLearnChangedPacket> {

		@Override
		public void finishEncode(
			C2SWorkstationAutoLearnChangedPacket msg,
			PacketBuffer buf
		) {
			buf.writeBoolean(msg.AUTO_LEARN_ENABLED);
		}

		@Override
		public C2SWorkstationAutoLearnChangedPacket finishDecode(
			int windowId, BlockPos tilePos, PacketBuffer buf
		) {
			return new C2SWorkstationAutoLearnChangedPacket(
				windowId,
				tilePos,
				buf.readBoolean()
			);
		}

		@Override
		public void handleDetailed(
			Supplier<Context> ctx,
			C2SWorkstationAutoLearnChangedPacket msg,
			WorkstationTileEntity workstationTileEntity
		) {
			workstationTileEntity.setAutoLearnEnabled(msg.AUTO_LEARN_ENABLED);
			workstationTileEntity
				.sendPacketToListeners(windowId ->
					new S2CWorkstationAutoLearnChangedPacket(
						windowId,
						msg.AUTO_LEARN_ENABLED
					));
		}
	}
}
