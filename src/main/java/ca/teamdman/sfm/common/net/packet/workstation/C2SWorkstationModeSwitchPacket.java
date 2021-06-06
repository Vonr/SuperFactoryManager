package ca.teamdman.sfm.common.net.packet.workstation;

import ca.teamdman.sfm.common.container.TileContainerType;
import ca.teamdman.sfm.common.tile.WorkstationTileEntity;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class C2SWorkstationModeSwitchPacket {

	public final int WINDOW_ID;
	public final Mode MODE;

	public C2SWorkstationModeSwitchPacket(
		int windowId,
		Mode mode
	) {
		this.WINDOW_ID = windowId;
		this.MODE = mode;
	}

	public static void encode(
		C2SWorkstationModeSwitchPacket msg,
		PacketBuffer packetBuffer
	) {
		packetBuffer.writeInt(msg.WINDOW_ID);
		packetBuffer.writeEnumValue(msg.MODE);
	}

	public static C2SWorkstationModeSwitchPacket decode(PacketBuffer packetBuffer) {
		return new C2SWorkstationModeSwitchPacket(
			packetBuffer.readInt(),
			packetBuffer.readEnumValue(Mode.class)
		);
	}

	public static void handle(
		C2SWorkstationModeSwitchPacket msg,
		Supplier<Context> contextSupplier
	) {
		contextSupplier.get().enqueueWork(() -> {
			ServerPlayerEntity sender = contextSupplier.get().getSender();
			if (sender == null) return;
			if (sender.openContainer == null) return;
			if (sender.openContainer.windowId != msg.WINDOW_ID) return;
			WorkstationTileEntity source = null;
			TileContainerType<?, WorkstationTileEntity> type = null;
//
//			if (
//				sender.openContainer instanceof WorkstationContainer
//					&& msg.MODE == Mode.LEARNING
//			) {
//				source = ((WorkstationContainer) sender.openContainer).getSource();
//				type = SFMContainers.WORKSTATION_LEARNING.get();
//			}
//
//			if (
//				sender.openContainer instanceof WorkstationLearningContainer
//					&& msg.MODE == Mode.USING
//			) {
//				source = ((WorkstationLearningContainer) sender.openContainer).getSource();
//				type = SFMContainers.WORKSTATION.get();
//			}

			if (source != null) {
				TileContainerType<?, WorkstationTileEntity> finalType = type;
				WorkstationTileEntity finalSource = source;
				finalType.openGui(
					sender,
					finalSource
				);
			}
		});
	}

	public enum Mode {
		USING,
		LEARNING;
	}
}
