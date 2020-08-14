package ca.teamdman.sfm.common.net.packet.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.manager.ManagerScreen;
import ca.teamdman.sfm.common.flowdata.FlowRelationshipData;
import ca.teamdman.sfm.common.net.packet.IWindowIdProvider;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class ManagerCreateRelationshipPacketS2C implements IWindowIdProvider {

	private final int WINDOW_ID;
	private final UUID ELEMENT_ID, FROM_ID, TO_ID;

	public ManagerCreateRelationshipPacketS2C(int windowId, UUID elementId, UUID fromId, UUID toId) {
		this.WINDOW_ID = windowId;
		this.ELEMENT_ID = elementId;
		this.FROM_ID = fromId;
		this.TO_ID = toId;
	}

	public static void encode(ManagerCreateRelationshipPacketS2C msg, PacketBuffer buf) {
		buf.writeInt(msg.WINDOW_ID);
		buf.writeString(msg.ELEMENT_ID.toString(), SFMUtil.UUID_STRING_LENGTH);
		buf.writeString(msg.FROM_ID.toString(), SFMUtil.UUID_STRING_LENGTH);
		buf.writeString(msg.TO_ID.toString(), SFMUtil.UUID_STRING_LENGTH);
	}

	public static void handle(ManagerCreateRelationshipPacketS2C msg, Supplier<NetworkEvent.Context> ctx) {
		SFM.PROXY.getScreenFromPacket(msg, ctx, ManagerScreen.class).ifPresent(screen -> {
			screen.DATAS
				.put(msg.ELEMENT_ID, new FlowRelationshipData(msg.ELEMENT_ID, msg.FROM_ID, msg.TO_ID));
			screen.CONTROLLER.loadFromScreenData();
		});
		ctx.get().setPacketHandled(true);
	}


	public static ManagerCreateRelationshipPacketS2C decode(PacketBuffer packetBuffer) {
		int windowId = packetBuffer.readInt();
		UUID elementId = UUID.fromString(packetBuffer.readString(SFMUtil.UUID_STRING_LENGTH));
		UUID fromId = UUID.fromString(packetBuffer.readString(SFMUtil.UUID_STRING_LENGTH));
		UUID toId = UUID.fromString(packetBuffer.readString(SFMUtil.UUID_STRING_LENGTH));
		return new ManagerCreateRelationshipPacketS2C(windowId, elementId, fromId, toId);
	}

	@Override
	public int getWindowId() {
		return WINDOW_ID;
	}
}
