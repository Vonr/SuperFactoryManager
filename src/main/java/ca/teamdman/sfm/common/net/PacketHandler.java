/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.net.packet.C2SContainerPacket;
import ca.teamdman.sfm.common.net.packet.C2SContainerPacket.C2SContainerPacketHandler;
import ca.teamdman.sfm.common.net.packet.S2CContainerPacket;
import ca.teamdman.sfm.common.net.packet.S2CContainerPacket.S2CContainerPacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.delete.ManagerDeletePacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.delete.ManagerDeletePacketS2C;
import ca.teamdman.sfm.common.net.packet.manager.put.ManagerCreateLineNodePacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.put.ManagerCreateLineNodePacketS2C;
import ca.teamdman.sfm.common.net.packet.manager.put.ManagerFlowDataPacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.put.ManagerFlowDataPacketS2C;
import ca.teamdman.sfm.common.net.packet.workstation.C2SWorkstationAutoLearnChangedPacket;
import ca.teamdman.sfm.common.net.packet.workstation.C2SWorkstationLearnClickPacket;
import ca.teamdman.sfm.common.net.packet.workstation.C2SWorkstationLearnRecipePacket;
import ca.teamdman.sfm.common.net.packet.workstation.S2CWorkstationAutoLearnChangedPacket;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntity;
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

	@SuppressWarnings("UnusedAssignment")
	public static void setup() {
		int i = 0;

		register(
			i++,
			ManagerCreateLineNodePacketC2S.class,
			new ManagerCreateLineNodePacketC2S.Handler()
		);

		register(
			i++,
			ManagerCreateLineNodePacketS2C.class,
			new ManagerCreateLineNodePacketS2C.Handler()
		);

		register(
			i++,
			ManagerDeletePacketC2S.class,
			new ManagerDeletePacketC2S.Handler()
		);

		register(
			i++,
			ManagerDeletePacketS2C.class,
			new ManagerDeletePacketS2C.Handler()
		);

		register(
			i++,
			ManagerFlowDataPacketC2S.class,
			new ManagerFlowDataPacketC2S.Handler()
		);

		register(
			i++,
			ManagerFlowDataPacketS2C.class,
			new ManagerFlowDataPacketS2C.Handler()
		);

		register(
			i++,
			C2SWorkstationLearnClickPacket.class,
			new C2SWorkstationLearnClickPacket.Handler()
		);

		register(
			i++,
			C2SWorkstationAutoLearnChangedPacket.class,
			new C2SWorkstationAutoLearnChangedPacket.Handler()
		);

		register(
			i++,
			S2CWorkstationAutoLearnChangedPacket.class,
			new S2CWorkstationAutoLearnChangedPacket.Handler()
		);

		register(
			i++,
			C2SWorkstationLearnRecipePacket.class,
			new C2SWorkstationLearnRecipePacket.Handler()
		);
	}

	public static <TILE extends TileEntity, CONTAINER extends Container, MSG extends C2SContainerPacket<TILE, CONTAINER>> void register(
		int id, Class<MSG> clazz,
		C2SContainerPacketHandler<TILE, CONTAINER, MSG> handler
	) {
		INSTANCE.registerMessage(
			id,
			clazz,
			handler::encode,
			handler::decode,
			handler::handle
		);
	}

	public static <SCREEN extends Screen, MSG extends S2CContainerPacket<SCREEN>> void register(
		int id, Class<MSG> clazz,
		S2CContainerPacketHandler<SCREEN, MSG> handler
	) {
		INSTANCE.registerMessage(
			id,
			clazz,
			handler::encode,
			handler::decode,
			handler::handle
		);
	}

	private static final class Handler {

	} // prevent intellij from importing handlers without qualifier
}
