/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.client.gui.flow.core.ComponentScreen;
import ca.teamdman.sfm.client.gui.flow.impl.config.ConfigComponent;
import net.minecraft.util.text.ITextComponent;

public class ConfigScreen extends ComponentScreen<ConfigComponent> {

	//TODO: add option to hide reminder text on manager screen
	//TODO: add option to prevent closing entire manager with "E"

	public ConfigComponent CONTROLLER;

	public ConfigScreen(
		ITextComponent titleIn,
		int scaledWidth,
		int scaledHeight
	) {
		super(titleIn, scaledWidth, scaledHeight);
		CONTROLLER = new ConfigComponent(this);
	}

	@Override
	public ConfigComponent getComponent() {
		return CONTROLLER;
	}
}
