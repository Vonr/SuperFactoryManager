package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.manager.ManagerScreen;
import ca.teamdman.sfm.common.flowdata.IFlowData;
import ca.teamdman.sfm.common.flowdata.IHasPosition;
import ca.teamdman.sfm.common.net.packet.IWindowIdProvider;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class ManagerPositionPacketS2C implements IWindowIdProvider {

	private final int WINDOW_ID, X, Y;
	private final UUID ELEMENT_ID;

	public ManagerPositionPacketS2C(int windowId, UUID elementId, int x, int y) {
		this.WINDOW_ID = windowId;
		this.ELEMENT_ID = elementId;
		this.X = x;
		this.Y = y;
	}

	public static void encode(ManagerPositionPacketS2C msg, PacketBuffer buf) {
		buf.writeInt(msg.WINDOW_ID);
		buf.writeString(msg.ELEMENT_ID.toString(), SFMUtil.UUID_STRING_LENGTH);
		buf.writeInt(msg.X);
		buf.writeInt(msg.Y);
	}

	public static void handle(ManagerPositionPacketS2C msg, Supplier<NetworkEvent.Context> ctx) {
		SFM.PROXY.getScreenFromPacket(msg, ctx, ManagerScreen.class).ifPresent(screen -> {
			IFlowData data = screen.DATAS.get(msg.ELEMENT_ID);
			if (data instanceof IHasPosition) {
				((IHasPosition) data).getPosition().setXY(msg.X, msg.Y);
			}
			screen.getControllers().forEach(IFlowController::load);
		});
		ctx.get().setPacketHandled(true);
	}


	public static ManagerPositionPacketS2C decode(PacketBuffer packetBuffer) {
		int windowId = packetBuffer.readInt();
		UUID elementId = UUID.fromString(packetBuffer.readString(SFMUtil.UUID_STRING_LENGTH));
		int x = packetBuffer.readInt();
		int y = packetBuffer.readInt();
		return new ManagerPositionPacketS2C(windowId, elementId, x, y);
	}

	@Override
	public int getWindowId() {
		return WINDOW_ID;
	}
}
