package ca.teamdman.sfm.common.net.packet.workstation;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.gui.screen.WorkstationScreen;
import ca.teamdman.sfm.common.net.packet.IWindowIdProvider;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class S2CWorkstationAutoLearnChangedPacket implements IWindowIdProvider {

	public final int WINDOW_ID;
	public final boolean AUTO_LEARN_ENABLED;

	public S2CWorkstationAutoLearnChangedPacket(
		int windowId,
		boolean autoLearnEnabled
	) {
		this.WINDOW_ID = windowId;
		this.AUTO_LEARN_ENABLED = autoLearnEnabled;
	}

	public static void encode(
		S2CWorkstationAutoLearnChangedPacket msg,
		PacketBuffer packetBuffer
	) {
		packetBuffer.writeInt(msg.WINDOW_ID);
		packetBuffer.writeBoolean(msg.AUTO_LEARN_ENABLED);
	}

	public static S2CWorkstationAutoLearnChangedPacket decode(PacketBuffer packetBuffer) {
		return new S2CWorkstationAutoLearnChangedPacket(
			packetBuffer.readInt(),
			packetBuffer.readBoolean()
		);
	}

	public static void handle(
		S2CWorkstationAutoLearnChangedPacket msg,
		Supplier<Context> contextSupplier
	) {
		contextSupplier.get().enqueueWork(() -> {
			SFM.PROXY.getScreenFromPacket(
				msg,
				contextSupplier,
				WorkstationScreen.class
			)
				.ifPresent(screen -> {
					screen
						.getContainer()
						.getSource()
						.setAutoLearnEnabled(msg.AUTO_LEARN_ENABLED);
					screen.init(
						screen.getMinecraft(),
						screen.width,
						screen.height
					);
				});
		});
		contextSupplier.get().setPacketHandled(true);
	}

	@Override
	public int getWindowId() {
		return WINDOW_ID;
	}
}
