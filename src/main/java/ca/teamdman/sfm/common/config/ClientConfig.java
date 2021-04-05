/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class ClientConfig {

	public final ForgeConfigSpec.BooleanValue allowMultipleRuleWindows;
	public final ForgeConfigSpec.BooleanValue alwaysSnapMovementToGrid;
	public final ForgeConfigSpec.BooleanValue showRuleDrawerLabels;
	public final ForgeConfigSpec.BooleanValue allowElementsOutOfBounds;
	public final ForgeConfigSpec.BooleanValue enableRegexSearch;
	public final ForgeConfigSpec.BooleanValue hideManagerInstructions;
	public final ForgeConfigSpec.BooleanValue preventClosingManagerWithInventoryButton;
	public final ForgeConfigSpec.BooleanValue enableDebugMode;


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
		allowElementsOutOfBounds = builder
			.comment("Disable element out-of-bounds check when repositioning elements")
			.define("allowElementsOutOfBounds", false);
		enableRegexSearch = builder
			.comment("Use regular expressions when matching items during search queries")
			.define("enableRegexSearch", true);

		hideManagerInstructions = builder
			.comment("Hide the reminder text on the manager background")
			.define("hideManagerInstructions", false);

		preventClosingManagerWithInventoryButton = builder
			.comment("Prevent closing the manager gui with the inventory button")
			.define("preventClosingManagerWithInventoryButton", true);

		enableDebugMode = builder
			.comment("Enable display of debug information")
			.define("enableDebugMode", false);

		builder.pop();
	}
}
