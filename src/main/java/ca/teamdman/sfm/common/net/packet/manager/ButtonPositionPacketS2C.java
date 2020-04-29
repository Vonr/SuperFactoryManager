package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.ClientProxy;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.manager.ManagerScreen;
import ca.teamdman.sfm.common.net.packet.IWindowIdProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ButtonPositionPacketS2C implements IWindowIdProvider {
	private final int WINDOW_ID, ELEMENT_INDEX, X, Y;

	public ButtonPositionPacketS2C(int windowId, int elementIndex, int x, int y) {
		this.WINDOW_ID = windowId;
		this.ELEMENT_INDEX = elementIndex;
		this.X = x;
		this.Y = y;
	}

	public static void encode(ButtonPositionPacketS2C msg, PacketBuffer buf) {
		buf.writeInt(msg.WINDOW_ID);
		buf.writeInt(msg.ELEMENT_INDEX);
		buf.writeInt(msg.X);
		buf.writeInt(msg.Y);
	}

	public static void handle(ButtonPositionPacketS2C msg, Supplier<NetworkEvent.Context> ctx) {
		SFM.PROXY.getScreenFromPacket(msg, ctx, ManagerScreen.class).ifPresent(screen -> {
			screen.CONTAINER.x = msg.X;
			screen.CONTAINER.y = msg.Y;
			screen.CONTAINER.getControllers().forEach(IFlowController::load);
		});
		ctx.get().setPacketHandled(true);
	}


	public static ButtonPositionPacketS2C decode(PacketBuffer packetBuffer) {
		int windowId     = packetBuffer.readInt();
		int elementIndex = packetBuffer.readInt();
		int x            = packetBuffer.readInt();
		int y            = packetBuffer.readInt();
		return new ButtonPositionPacketS2C(windowId, elementIndex, x, y);
	}

	@Override
	public int getWindowId() {
		return WINDOW_ID;
	}
}
