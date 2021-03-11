/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.template;

import ca.teamdman.sfm.client.gui.flow.impl.config.ConfigComponent;
import ca.teamdman.sfm.client.gui.flow.impl.util.ButtonBackground;
import ca.teamdman.sfm.client.gui.flow.impl.util.ButtonLabel;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.client.gui.screen.ManagerScreen;
import ca.teamdman.sfm.common.flow.core.Position;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TranslationTextComponent;

public class SettingsFlowButton extends FlowContainer {

	private final ConfigComponent CONFIG_COMPONENT;

	private boolean open = false;

	public SettingsFlowButton(ManagerScreen screen) {
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

			@Override
			public List<? extends ITextProperties> getTooltip() {
				List<ITextComponent> list = new ArrayList<>();
				list.add(new TranslationTextComponent(
					open
						? "gui.sfm.flow.tooltip.settings_button.close"
						: "gui.sfm.flow.tooltip.settings_button.open"
				));
				return list;
			}
		});
	}

	@Override
	public int getZIndex() {
		return super.getZIndex() + 1000;
	}
}
