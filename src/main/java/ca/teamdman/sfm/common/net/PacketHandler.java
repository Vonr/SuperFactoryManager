package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateInputPacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateInputPacketS2C;
import ca.teamdman.sfm.common.net.packet.manager.PositionPacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.PositionPacketS2C;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
	private static final String        CHANNEL_NAME     = "main";
	private static final String        PROTOCOL_VERSION = "1";
	public static final  SimpleChannel INSTANCE         = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(SFM.MOD_ID, CHANNEL_NAME),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);


	@SuppressWarnings("UnusedAssignment")
	public static void setup() {
		int i = 0;
		INSTANCE.registerMessage(i++,
				PositionPacketC2S.class,
				PositionPacketC2S::encode,
				PositionPacketC2S::decode,
				PositionPacketC2S::handle);

		INSTANCE.registerMessage(i++,
				PositionPacketS2C.class,
				PositionPacketS2C::encode,
				PositionPacketS2C::decode,
				PositionPacketS2C::handle);

		INSTANCE.registerMessage(i++,
			ManagerCreateInputPacketC2S.class,
			ManagerCreateInputPacketC2S::encode,
			ManagerCreateInputPacketC2S::decode,
			ManagerCreateInputPacketC2S::handle);

		INSTANCE.registerMessage(i++,
			ManagerCreateInputPacketS2C.class,
			ManagerCreateInputPacketS2C::encode,
			ManagerCreateInputPacketS2C::decode,
			ManagerCreateInputPacketS2C::handle);
	}
}
