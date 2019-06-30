package vswe.superfactory.config;

import net.minecraftforge.fml.config.ModConfig;

public final class ConfigHelper {
	private static ModConfig commonConfig;
	private static ModConfig serverConfig;
	private static ModConfig clientConfig;

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

	public static void setValueAndSave(final ModConfig config, final String path, final Object value) {
		config.getConfigData().set(path,value);
		config.save();
	}
}
