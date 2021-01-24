/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.net.packet.manager.C2SManagerPacket;
import ca.teamdman.sfm.common.net.packet.manager.C2SManagerPacket.C2SHandler;
import ca.teamdman.sfm.common.net.packet.manager.S2CManagerPacket;
import ca.teamdman.sfm.common.net.packet.manager.S2CManagerPacket.S2CHandler;
import ca.teamdman.sfm.common.net.packet.manager.delete.ManagerDeletePacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.delete.ManagerDeletePacketS2C;
import ca.teamdman.sfm.common.net.packet.manager.put.ManagerCreateLineNodePacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.put.ManagerCreateLineNodePacketS2C;
import ca.teamdman.sfm.common.net.packet.manager.put.ManagerFlowDataPacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.put.ManagerFlowDataPacketS2C;
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
			ManagerFlowDataPacketC2S.class,
			new ManagerFlowDataPacketC2S.Handler()
		);

		register(i++,
			ManagerFlowDataPacketS2C.class,
			new ManagerFlowDataPacketS2C.Handler()
		);
	}

	private static final class Handler {

	} // prevent intellij from importing handlers without qualifier
}
