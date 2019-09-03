package ca.teamdman.sfm.config;

import net.minecraftforge.common.ForgeConfigSpec;

final class ServerConfig {
	final ForgeConfigSpec.IntValue serInt;

	ServerConfig(final ForgeConfigSpec.Builder builder) {
		builder.push("General Category");
		serInt = builder
				.comment("Common Int")
				.defineInRange("serInt", 0, 0, 1);
		builder.pop();
	}
}
