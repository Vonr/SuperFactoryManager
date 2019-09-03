package ca.teamdman.sfm.config;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod.EventBusSubscriber
public final class ConfigHelper {
	private static ModConfig clientConfig;
	private static ModConfig commonConfig;
	private static ModConfig serverConfig;

	public static void setValueAndSave(final ModConfig config, final String path, final Object value) {
		config.getConfigData().set(path, value);
		config.save();
	}

	@SubscribeEvent
	public static void onConfig(final ModConfig.ModConfigEvent e) {
		final ModConfig config = e.getConfig();
		if (config.getSpec() == ConfigHolder.COMMON_SPEC)
			bakeCommon(config);
		else if (config.getSpec() == ConfigHolder.SERVER_SPEC)
			bakeServer(config);
		else if (config.getSpec() == ConfigHolder.CLIENT_SPEC)
			bakeClient(config);
	}

	public static void bakeCommon(final ModConfig config) {
		commonConfig = config;
		Config.funCom = ConfigHolder.COMMON.comInt.get();
	}

	public static void bakeServer(final ModConfig config) {
		serverConfig = config;
		Config.funSer = ConfigHolder.SERVER.serInt.get();
	}

	public static void bakeClient(final ModConfig config) {
		clientConfig = config;
		Config.funCli = ConfigHolder.CLIENT.cliInt.get();
	}
}
