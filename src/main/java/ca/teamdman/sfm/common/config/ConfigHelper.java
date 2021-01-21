/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.config;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.config.Config.Client;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Bus.MOD)
public final class ConfigHelper {

	@SuppressWarnings("FieldCanBeLocal")
	public static ModConfig clientConfig;
	@SuppressWarnings("FieldCanBeLocal")
	public static ModConfig commonConfig;
	@SuppressWarnings("FieldCanBeLocal")
	public static ModConfig serverConfig;

	public static void setValueAndSave(
		final ModConfig config,
		final String path,
		final Object value
	) {
		config.getConfigData().set(path, value);
		config.save();
		bakeConfig(config);
	}

	public static void setValueAndSave(ModConfig config, ConfigValue<?> field, Object value) {
		setValueAndSave(
			config,
			String.join(".", field.getPath()),
			value
		);
	}

	@SubscribeEvent
	public static void onConfig(final ModConfig.ModConfigEvent e) {
		bakeConfig(e.getConfig());
	}

	public static void bakeConfig(ModConfig config) {
		if (config.getSpec() == ConfigHolder.COMMON_SPEC) {
			bakeCommon(config);
		} else if (config.getSpec() == ConfigHolder.SERVER_SPEC) {
			bakeServer(config);
		} else if (config.getSpec() == ConfigHolder.CLIENT_SPEC) {
			bakeClient(config);
		}
	}

	public static void bakeCommon(final ModConfig config) {
		commonConfig = config;
	}

	public static void bakeServer(final ModConfig config) {
		serverConfig = config;
	}

	public static void bakeClient(final ModConfig config) {
		clientConfig = config;
		Client.allowMultipleRuleWindows = ConfigHolder.CLIENT.allowMultipleRuleWindows.get();
		Client.showRuleDrawerLabels = ConfigHolder.CLIENT.showRuleDrawerLabels.get();
		Client.alwaysSnapMovementToGrid = ConfigHolder.CLIENT.alwaysSnapMovementToGrid.get();
		Client.allowElementsOutOfBounds = ConfigHolder.CLIENT.allowElementsOutOfBounds.get();
		Client.enableRegexSearch = ConfigHolder.CLIENT.enableRegexSearch.get();
		Client.hideManagerInstructions = ConfigHolder.CLIENT.hideManagerInstructions.get();
		Client.preventClosingManagerWithInventoryButton = ConfigHolder.CLIENT.preventClosingManagerWithInventoryButton.get();
	}
}
