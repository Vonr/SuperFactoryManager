package ca.teamdman.sfm.common.net.packet;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class ManagerUpdatePacket {
	private String payload;

	public ManagerUpdatePacket(String payload) {
		this.payload = payload;
	}

	public static void encode(ManagerUpdatePacket msg, PacketBuffer buf) {
		buf.writeString(msg.payload);
	}

	public static void handle(ManagerUpdatePacket msg, Supplier<NetworkEvent.Context> ctx) {
		System.out.printf("Received payload %s on side %s!\n",
				msg.payload,
				Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER
						? "SERVER"
						: "CLIENT");
		ctx.get().setPacketHandled(true);
	}

	public static ManagerUpdatePacket decode(PacketBuffer packetBuffer) {
		return new ManagerUpdatePacket(packetBuffer.readString());
	}
}
