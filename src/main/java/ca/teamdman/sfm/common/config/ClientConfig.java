/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

final class ClientConfig {
	final ForgeConfigSpec.IntValue cliInt;

	ClientConfig(final ForgeConfigSpec.Builder builder) {
		builder.push("General Category");
		cliInt = builder
				.comment("Client Int")
				.defineInRange("cliInt", 0, 0, 1);
		builder.pop();
	}
}
