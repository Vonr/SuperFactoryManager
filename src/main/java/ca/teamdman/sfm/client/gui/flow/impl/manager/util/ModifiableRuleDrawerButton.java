/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.util;

import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton.ButtonLabel;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowItemStack;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowPlusButton;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.impl.FlowRuleData;
import java.util.List;

public abstract class ModifiableRuleDrawerButton extends DrawerButton {

	public final ManagerFlowController CONTROLLER;
	private final EditButton EDIT_BUTTON;

	public ModifiableRuleDrawerButton(
		ManagerFlowController CONTROLLER,
		Position pos,
		ButtonLabel label
	) {
		super(CONTROLLER, pos, label);
		this.CONTROLLER = CONTROLLER;
		this.EDIT_BUTTON = new EditButton(CONTROLLER);
		this.DRAWER.addChild(EDIT_BUTTON);
		this.DRAWER.update();
	}

	public void rebuildChildren() {
		this.DRAWER.getChildren().clear();
		this.DRAWER.addChild(EDIT_BUTTON);
		this.DRAWER.update();
	};

	public void addChild(FlowRuleData data) {
		this.DRAWER.addChild(new RuleDrawerItem(DRAWER, CONTROLLER, data));
		this.DRAWER.update();
	}

	public void removeChild(FlowRuleData data) {
		this.DRAWER.getChildren().removeIf(c -> c instanceof RuleDrawerItem
			&& ((RuleDrawerItem) c).getData().equals(data));
		this.DRAWER.update();
	}

	public abstract void onChange(List<FlowRuleData> data);

	private class EditButton extends FlowPlusButton {

		private final RuleSelectionDrawer DRAWER;
		private boolean open = false;

		public EditButton(ManagerFlowController controller) {
			super(
				new Position(),
				new Size(FlowItemStack.ITEM_TOTAL_WIDTH, FlowItemStack.ITEM_TOTAL_HEIGHT),
				CONST.SELECTED
			);
			this.DRAWER = new RuleSelectionDrawer(
				controller, ModifiableRuleDrawerButton.this.getPosition().withConstantOffset(
				() -> ModifiableRuleDrawerButton.this.DRAWER.getSize().getWidth() + 5,
				() -> 0
			)) {
				@Override
				public void onSelectionChanged(
					List<FlowRuleData> data
				) {
					onChange(data);
				}
			};
			DRAWER.setEnabled(false);
			DRAWER.setVisible(false);
			addChild(DRAWER);
		}

		@Override
		public void onClicked(int mx, int my, int button) {
			open = !open;
			DRAWER.setEnabled(open);
			DRAWER.setVisible(open);
		}
	}
}
