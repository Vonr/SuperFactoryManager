/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.core;

import ca.teamdman.sfm.client.gui.flow.impl.config.ConfigComponent;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton.ButtonBackground;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton.ButtonLabel;
import ca.teamdman.sfm.client.gui.screen.ManagerScreen;
import ca.teamdman.sfm.common.flow.data.core.Position;

public class SettingsController extends FlowContainer {

	private final ConfigComponent CONFIG_COMPONENT;

	private boolean open = false;

	public SettingsController(ManagerScreen screen) {
		CONFIG_COMPONENT = new ConfigComponent(screen);
		CONFIG_COMPONENT.setVisible(false);
		CONFIG_COMPONENT.setEnabled(false);
		addChild(CONFIG_COMPONENT);

		addChild(new FlowIconButton(
			ButtonLabel.SETTINGS,
			new Position(5, screen.getScaledHeight() - (ButtonBackground.NORMAL.HEIGHT + 5))
		) {
			@Override
			public void onClicked(int mx, int my, int button) {
				open = !open;
				CONFIG_COMPONENT.setVisible(open);
				CONFIG_COMPONENT.setEnabled(open);
			}
		});
	}

	@Override
	public int getZIndex() {
		return super.getZIndex() + 1000;
	}
}
