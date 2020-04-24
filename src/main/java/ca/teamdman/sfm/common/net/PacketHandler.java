package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.net.packet.ManagerUpdatePacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
	private static final String        CHANNEL_NAME     = "main";
	private static final String        PROTOCOL_VERSION = "1";
	public static final  SimpleChannel INSTANCE         = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(SFM.MOD_ID, CHANNEL_NAME),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals// todo: C2S packet, predicate always false etc
	);


	@SuppressWarnings("UnusedAssignment")
	public static void setup() {
		int i = 0;
		INSTANCE.registerMessage(i++,
				ManagerUpdatePacket.class,
				ManagerUpdatePacket::encode,
				ManagerUpdatePacket::decode,
				ManagerUpdatePacket::handle
		);
	}
}
