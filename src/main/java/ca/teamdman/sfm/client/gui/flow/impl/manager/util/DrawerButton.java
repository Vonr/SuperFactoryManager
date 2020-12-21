/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.util;

import ca.teamdman.sfm.client.gui.flow.core.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowDrawer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton.ButtonLabel;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowItemStack;

public abstract class DrawerButton extends FlowContainer {

	public final ManagerFlowController CONTROLLER;
	public final FlowIconButton BUTTON;
	public final FlowDrawer DRAWER;
	private boolean open = false;

	public DrawerButton(
		ManagerFlowController controller,
		ButtonLabel label
	) {
		this.CONTROLLER = controller;
		this.BUTTON = new FlowIconButton(label) {
			@Override
			public void onClicked(int mx, int my, int button) {
				open = !open;
				DRAWER.setEnabled(open);
				DRAWER.setVisible(open);
			}
		};
		this.DRAWER = new FlowDrawer(
			FlowItemStack.ITEM_TOTAL_WIDTH,
			FlowItemStack.ITEM_TOTAL_HEIGHT
		);
		addChild(BUTTON);
		addChild(DRAWER);
	}
}
