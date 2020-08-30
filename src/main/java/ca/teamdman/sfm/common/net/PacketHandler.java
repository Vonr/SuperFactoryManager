package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.net.packet.manager.C2SManagerPacket;
import ca.teamdman.sfm.common.net.packet.manager.C2SManagerPacket.C2SHandler;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateInputPacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateInputPacketS2C;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateLineNodePacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateLineNodePacketS2C;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateRelationshipPacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateRelationshipPacketS2C;
import ca.teamdman.sfm.common.net.packet.manager.ManagerDeletePacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.ManagerDeletePacketS2C;
import ca.teamdman.sfm.common.net.packet.manager.ManagerPositionPacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.ManagerPositionPacketS2C;
import ca.teamdman.sfm.common.net.packet.manager.ManagerToggleInputSelectedC2S;
import ca.teamdman.sfm.common.net.packet.manager.ManagerToggleInputSelectedS2C;
import ca.teamdman.sfm.common.net.packet.manager.S2CManagerPacket;
import ca.teamdman.sfm.common.net.packet.manager.S2CManagerPacket.S2CHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {

	private static final String CHANNEL_NAME = "main";
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
		new ResourceLocation(SFM.MOD_ID, CHANNEL_NAME),
		() -> PROTOCOL_VERSION,
		PROTOCOL_VERSION::equals,
		PROTOCOL_VERSION::equals
	);

	public static <MSG extends C2SManagerPacket> void register(int id, Class<MSG> clazz,
		C2SHandler<MSG> handler) {
		INSTANCE.registerMessage(id,
			clazz,
			handler::encode,
			handler::decode,
			handler::handle);
	}

	public static <MSG extends S2CManagerPacket> void register(int id, Class<MSG> clazz,
		S2CHandler<MSG> handler) {
		INSTANCE.registerMessage(id,
			clazz,
			handler::encode,
			handler::decode,
			handler::handle);
	}

	@SuppressWarnings("UnusedAssignment")
	public static void setup() {
		int i = 0;
		register(i++,
			ManagerPositionPacketC2S.class,
			new ManagerPositionPacketC2S.Handler());

		register(i++,
			ManagerPositionPacketS2C.class,
			new ManagerPositionPacketS2C.Handler());

		register(i++,
			ManagerCreateInputPacketC2S.class,
			new ManagerCreateInputPacketC2S.Handler());

		register(i++,
			ManagerCreateInputPacketS2C.class,
			new ManagerCreateInputPacketS2C.Handler());

		register(i++,
			ManagerCreateRelationshipPacketC2S.class,
			new ManagerCreateRelationshipPacketC2S.Handler());

		register(i++,
			ManagerCreateRelationshipPacketS2C.class,
			new ManagerCreateRelationshipPacketS2C.Handler());

		register(i++,
			ManagerCreateLineNodePacketC2S.class,
			new ManagerCreateLineNodePacketC2S.Handler());

		register(i++,
			ManagerCreateLineNodePacketS2C.class,
			new ManagerCreateLineNodePacketS2C.Handler());

		register(i++,
			ManagerDeletePacketC2S.class,
			new ManagerDeletePacketC2S.Handler()
		);

		register(i++,
			ManagerDeletePacketS2C.class,
			new ManagerDeletePacketS2C.Handler()
		);

		register(i++,
			ManagerToggleInputSelectedC2S.class,
			new ManagerToggleInputSelectedC2S.Handler()
		);

		register(i++,
			ManagerToggleInputSelectedS2C.class,
			new ManagerToggleInputSelectedS2C.Handler()
		);
	}

	private static final class Handler {

	} // prevent intellij from importing handlers without qualifier
}
