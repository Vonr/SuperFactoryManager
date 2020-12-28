/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ClientConfig {
	public final ForgeConfigSpec.BooleanValue allowMultipleRuleWindows;
	public final ForgeConfigSpec.BooleanValue alwaysSnapMovementToGrid;
	public final ForgeConfigSpec.BooleanValue showRuleDrawerLabels;


	ClientConfig(final ForgeConfigSpec.Builder builder) {
		builder.push("General Category");
		allowMultipleRuleWindows = builder
			.comment("Allow multiple rule windows to be visible at once")
			.define("allowMultipleRuleWindows", false);
		showRuleDrawerLabels = builder
			.comment("Show the helper labels on rule drawers")
			.define("showRuleDrawerLabels", true);
		alwaysSnapMovementToGrid = builder
			.comment("Always snap movement to grid")
			.define("alwaysSnapMovementToGrid", false);
		builder.pop();
	}
}
