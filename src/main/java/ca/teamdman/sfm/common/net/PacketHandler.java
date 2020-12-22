/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.net.packet.manager.C2SManagerPacket;
import ca.teamdman.sfm.common.net.packet.manager.C2SManagerPacket.C2SHandler;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateLineNodePacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateLineNodePacketS2C;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateOutputPacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateOutputPacketS2C;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateRelationshipPacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateRelationshipPacketS2C;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateTileEntityRulePacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateTileEntityRulePacketS2C;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateTimerTriggerPacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateTimerTriggerPacketS2C;
import ca.teamdman.sfm.common.net.packet.manager.S2CManagerPacket;
import ca.teamdman.sfm.common.net.packet.manager.S2CManagerPacket.S2CHandler;
import ca.teamdman.sfm.common.net.packet.manager.delete.ManagerDeletePacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.delete.ManagerDeletePacketS2C;
import ca.teamdman.sfm.common.net.packet.manager.patch.ManagerPositionPacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.patch.ManagerPositionPacketS2C;
import ca.teamdman.sfm.common.net.packet.manager.patch.ManagerToggleBlockPosSelectedC2S;
import ca.teamdman.sfm.common.net.packet.manager.patch.ManagerToggleBlockPosSelectedS2C;
import ca.teamdman.sfm.common.net.packet.manager.put.ManagerFlowInputDataPacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.put.ManagerFlowInputDataPacketS2C;
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
			ManagerFlowInputDataPacketC2S.class,
			new ManagerFlowInputDataPacketC2S.Handler());

		register(i++,
			ManagerFlowInputDataPacketS2C.class,
			new ManagerFlowInputDataPacketS2C.Handler());

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
			ManagerToggleBlockPosSelectedC2S.class,
			new ManagerToggleBlockPosSelectedC2S.Handler()
		);

		register(i++,
			ManagerToggleBlockPosSelectedS2C.class,
			new ManagerToggleBlockPosSelectedS2C.Handler()
		);

		register(i++,
			ManagerCreateOutputPacketC2S.class,
			new ManagerCreateOutputPacketC2S.Handler()
		);

		register(i++,
			ManagerCreateOutputPacketS2C.class,
			new ManagerCreateOutputPacketS2C.Handler()
		);

		register(i++,
			ManagerCreateTimerTriggerPacketC2S.class,
			new ManagerCreateTimerTriggerPacketC2S.Handler()
		);

		register(i++,
			ManagerCreateTimerTriggerPacketS2C.class,
			new ManagerCreateTimerTriggerPacketS2C.Handler()
		);

		register(i++,
			ManagerCreateTileEntityRulePacketC2S.class,
			new ManagerCreateTileEntityRulePacketC2S.Handler()
		);

		register(i++,
			ManagerCreateTileEntityRulePacketS2C.class,
			new ManagerCreateTileEntityRulePacketS2C.Handler()
		);
	}

	private static final class Handler {

	} // prevent intellij from importing handlers without qualifier
}
