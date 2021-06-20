package ca.teamdman.sfm.common.net.packet.workstation;

import ca.teamdman.sfm.common.container.WorkstationContainer;
import java.util.function.Supplier;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class C2SWorkstationAutoLearnChangedPacket {

	public final int WINDOW_ID;
	public final boolean AUTO_LEARN_ENABLED;

	public C2SWorkstationAutoLearnChangedPacket(
		int windowId,
		boolean autoLearnEnabled
	) {
		this.WINDOW_ID = windowId;
		this.AUTO_LEARN_ENABLED = autoLearnEnabled;
	}

	public static void encode(
		C2SWorkstationAutoLearnChangedPacket msg,
		PacketBuffer packetBuffer
	) {
		packetBuffer.writeInt(msg.WINDOW_ID);
		packetBuffer.writeBoolean(msg.AUTO_LEARN_ENABLED);
	}

	public static C2SWorkstationAutoLearnChangedPacket decode(PacketBuffer packetBuffer) {
		return new C2SWorkstationAutoLearnChangedPacket(
			packetBuffer.readInt(),
			packetBuffer.readBoolean()
		);
	}

	public static void handle(
		C2SWorkstationAutoLearnChangedPacket msg,
		Supplier<Context> contextSupplier
	) {
		contextSupplier.get().enqueueWork(() -> {
			ServerPlayerEntity sender = contextSupplier.get().getSender();
			if (sender == null) return;
			if (sender.openContainer == null) return;
			if (sender.openContainer.windowId != msg.WINDOW_ID) return;
			if (!(sender.openContainer instanceof WorkstationContainer)) {
				return;
			}
			((WorkstationContainer) sender.openContainer)
				.getSource()
				.setAutoLearnEnabled(msg.AUTO_LEARN_ENABLED);

			((WorkstationContainer) sender.openContainer)
				.getSource()
				.sendPacketToListeners(windowId ->
					new S2CWorkstationAutoLearnChangedPacket(
						windowId,
						msg.AUTO_LEARN_ENABLED
					));
		});
		contextSupplier.get().setPacketHandled(true);
	}

}
