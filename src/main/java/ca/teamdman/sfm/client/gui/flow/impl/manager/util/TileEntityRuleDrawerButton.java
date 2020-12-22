/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.util;

import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.IFlowController;
import ca.teamdman.sfm.client.gui.flow.core.IFlowTangible;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.FlowTileEntityRule;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton.ButtonLabel;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowItemStack;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowPlusButton;
import ca.teamdman.sfm.common.config.Config;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.impl.FlowTileEntityRuleData;

public abstract class TileEntityRuleDrawerButton extends DrawerButton {

	public final ManagerFlowController CONTROLLER;
	private final AddTileEntityRuleButton ADD_BUTTON;

	public TileEntityRuleDrawerButton(
		ManagerFlowController CONTROLLER,
		Position pos,
		ButtonLabel label
	) {
		super(CONTROLLER, pos, label);
		this.CONTROLLER = CONTROLLER;
		this.ADD_BUTTON = new AddTileEntityRuleButton(
			CONTROLLER, new Position(0, 0), new Size(24, 24));
		this.DRAWER.addChild(ADD_BUTTON);
		// discover existing children
		this.CONTROLLER.SCREEN.getData(FlowTileEntityRuleData.class).forEach(this::addChild);
	}

	public void addChild(FlowTileEntityRuleData data) {
		this.DRAWER.addChild(new TileEntityRuleDrawerElement(this, data));
		this.DRAWER.update();
	}

	public void removeChild(FlowTileEntityRuleData data) {
		this.DRAWER.getChildren().removeIf(c -> c instanceof TileEntityRuleDrawerElement
			&& ((TileEntityRuleDrawerElement) c).DATA.equals(data));
		this.DRAWER.update();
	}

	public static class TileEntityRuleDrawerElement extends FlowItemStack {

		public final TileEntityRuleDrawerButton PARENT;
		public final FlowTileEntityRuleData DATA;

		public TileEntityRuleDrawerElement(
			TileEntityRuleDrawerButton PARENT,
			FlowTileEntityRuleData data
		) {
			super(data.getIcon(), new Position());
			this.PARENT = PARENT;
			this.DATA = data;
		}

		@Override
		public void setSelected(boolean value, boolean notify) {
			if (notify) {
				// only allow one item to be "selected" (open) at a time
				PARENT.DRAWER.getChildren().forEach(item -> {
					if (item instanceof TileEntityRuleDrawerElement) {
						((TileEntityRuleDrawerElement) item).setSelected(false, false);
					}
				});
				if (!Config.allowMultipleRuleWindows) {
					PARENT.CONTROLLER.getChildren().stream()
						.filter(c -> c instanceof FlowTileEntityRule)
						.map(c -> ((FlowTileEntityRule) c))
						.forEach(c -> {
							c.setVisible(false);
							c.setEnabled(false);
						});
				}
				PARENT.CONTROLLER.findFirstChild(DATA.getId()).ifPresent(v -> v.setVisible(value));
			}
			super.setSelected(value, notify);
		}
	}

	private class AddTileEntityRuleButton extends FlowPlusButton implements
		IFlowController, IFlowTangible {

		private final ExistingRulesDrawer DRAWER;
		private boolean open = false;

		public AddTileEntityRuleButton(ManagerFlowController controller, Position pos, Size size) {
			super(pos, size, CONST.SELECTED);
			this.DRAWER = new ExistingRulesDrawer(
				controller, TileEntityRuleDrawerButton.this.getPosition().withConstantOffset(
				() -> TileEntityRuleDrawerButton.this.DRAWER.getSize().getWidth() + 5,
				() -> 0
			));
			DRAWER.setEnabled(false);
			DRAWER.setVisible(false);
			addChild(DRAWER);
		}

		@Override
		public void onClicked(int mx, int my, int button) {
			open = !open;
			DRAWER.setEnabled(open);
			DRAWER.setVisible(open);
//			createNewRule();
		}
	}
}
