package ca.teamdman.sfm.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class ConfigHolder {
	public static final ForgeConfigSpec COMMON_SPEC;
	public static final ForgeConfigSpec SERVER_SPEC;
	public static final ForgeConfigSpec CLIENT_SPEC;
	static final CommonConfig COMMON;
	static final ServerConfig SERVER;
	static final ClientConfig CLIENT;
	static {
		{
			final Pair<CommonConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
			COMMON = specPair.getLeft();
			COMMON_SPEC = specPair.getRight();
		}
		{
			final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
			SERVER = specPair.getLeft();
			SERVER_SPEC = specPair.getRight();
		}
		{
			final Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
			CLIENT = specPair.getLeft();
			CLIENT_SPEC = specPair.getRight();
		}
	}
}
