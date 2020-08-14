package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.manager.ManagerScreen;
import ca.teamdman.sfm.common.flowdata.InputData;
import ca.teamdman.sfm.common.flowdata.Position;
import ca.teamdman.sfm.common.net.packet.IWindowIdProvider;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class ManagerCreateInputPacketS2C implements IWindowIdProvider {

	private final int WINDOW_ID, X, Y;
	private final UUID ELEMENT_ID;

	public ManagerCreateInputPacketS2C(int windowId, UUID elementId, int x, int y) {
		this.WINDOW_ID = windowId;
		this.ELEMENT_ID = elementId;
		this.X = x;
		this.Y = y;
	}

	public static void encode(ManagerCreateInputPacketS2C msg, PacketBuffer buf) {
		buf.writeInt(msg.WINDOW_ID);
		buf.writeString(msg.ELEMENT_ID.toString(), SFMUtil.UUID_STRING_LENGTH);
		buf.writeInt(msg.X);
		buf.writeInt(msg.Y);
	}

	public static void handle(ManagerCreateInputPacketS2C msg, Supplier<NetworkEvent.Context> ctx) {
		SFM.PROXY.getScreenFromPacket(msg, ctx, ManagerScreen.class).ifPresent(screen -> {
			screen.DATAS
				.put(msg.ELEMENT_ID, new InputData(msg.ELEMENT_ID, new Position(msg.X, msg.Y)));
			screen.getControllers().forEach(IFlowController::load);
		});
		ctx.get().setPacketHandled(true);
	}


	public static ManagerCreateInputPacketS2C decode(PacketBuffer packetBuffer) {
		int windowId = packetBuffer.readInt();
		UUID elementId = UUID.fromString(packetBuffer.readString(SFMUtil.UUID_STRING_LENGTH));
		int x = packetBuffer.readInt();
		int y = packetBuffer.readInt();
		return new ManagerCreateInputPacketS2C(windowId, elementId, x, y);
	}

	@Override
	public int getWindowId() {
		return WINDOW_ID;
	}
}
