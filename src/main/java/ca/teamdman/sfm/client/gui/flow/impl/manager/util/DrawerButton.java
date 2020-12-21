/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.util;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowDrawer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton.ButtonLabel;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowItemStack;
import ca.teamdman.sfm.common.flow.data.core.Position;
import java.util.Optional;

public abstract class DrawerButton extends FlowContainer {

	public final ManagerFlowController CONTROLLER;
	public final FlowIconButton BUTTON;
	public final FlowDrawer DRAWER;
	private boolean open = false;

	@Override
	public Optional<FlowComponent> getElementUnderMouse(int mx, int my) {
		return super.getElementUnderMouse(mx, my).map(__ -> this);
	}

	public DrawerButton(
		ManagerFlowController controller,
		Position pos,
		ButtonLabel label
	) {
		this.CONTROLLER = controller;
		this.BUTTON = new FlowIconButton(label, pos) {
			@Override
			public void onClicked(int mx, int my, int button) {
				open = !open;
				DRAWER.setEnabled(open);
				DRAWER.setVisible(open);
			}

			@Override
			public void onDragFinished(int dx, int dy, int mx, int my) {
				DrawerButton.this.getPosition().setXY(getPosition());
				DrawerButton.this.onDragFinished(dx, dy, mx, my);
				DrawerButton.this.getPosition().setXY(0, 0);
			}
		};
		this.DRAWER = new FlowDrawer(
			pos.withConstantOffset(25, 0),
			FlowItemStack.ITEM_TOTAL_WIDTH,
			FlowItemStack.ITEM_TOTAL_HEIGHT
		);
		addChild(BUTTON);
		addChild(DRAWER);
		DRAWER.setVisible(false);
		DRAWER.setEnabled(false);
		DRAWER.update();
	}
}
