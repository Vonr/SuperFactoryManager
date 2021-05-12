/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm;


import ca.teamdman.sfm.client.ClientProxy;
import ca.teamdman.sfm.client.gui.screen.CrafterScreen;
import ca.teamdman.sfm.client.gui.screen.ManagerScreen;
import ca.teamdman.sfm.common.Proxy;
import ca.teamdman.sfm.common.ServerProxy;
import ca.teamdman.sfm.common.config.ConfigHolder;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.registrar.SFMBlocks;
import ca.teamdman.sfm.common.registrar.SFMContainers;
import ca.teamdman.sfm.common.registrar.SFMItems;
import ca.teamdman.sfm.common.registrar.SFMTiles;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(SFM.MOD_ID)
public class SFM {

	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "sfm";
	public static final String MOD_NAME = "Super Factory Manager";
	public static final Proxy PROXY = DistExecutor.runForDist(() -> ClientProxy::new,
		() -> ServerProxy::new
	);

	public SFM() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON,
			ConfigHolder.COMMON_SPEC
		);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER,
			ConfigHolder.SERVER_SPEC
		);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT,
			ConfigHolder.CLIENT_SPEC
		);

		bus.addListener(this::onCommonSetup);
		bus.addListener(this::onClientSetup);
		SFMBlocks.BLOCKS.register(bus);
		SFMItems.ITEMS.register(bus);
		SFMTiles.TILES.register(bus);
		SFMContainers.CONTAINER_TYPES.register(bus);

		PROXY.registerScreens();
	}

	private void onCommonSetup(FMLCommonSetupEvent e) {
		PacketHandler.setup();
	}

	public void onClientSetup(FMLClientSetupEvent e) {
		ScreenManager.registerFactory(SFMContainers.MANAGER.get(),
			ManagerScreen::new
		);
		ScreenManager.registerFactory(SFMContainers.CRAFTER.get(),
			CrafterScreen::new
		);
	}
}
