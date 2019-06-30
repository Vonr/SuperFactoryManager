package ca.teamdman.sfm.config;

import net.minecraftforge.common.ForgeConfigSpec;

final class CommonConfig {
	final ForgeConfigSpec.IntValue comInt;

	CommonConfig(final ForgeConfigSpec.Builder builder) {
		builder.push("General Category");
		comInt = builder
				.comment("Common Int")
				.defineInRange("comInt",0,0,1);
		builder.pop();
	}
}
