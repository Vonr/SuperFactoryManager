/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.util;

import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.FlowTileEntityRule;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowDrawer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowItemStack;
import ca.teamdman.sfm.common.config.Config.Client;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.TileEntityItemStackRuleFlowData;

public class RuleDrawerItem extends FlowContainer implements
	FlowDataHolder<TileEntityItemStackRuleFlowData> {

	private final FlowItemStack ICON;
	private final ManagerFlowController CONTROLLER;
	private final FlowDrawer DRAWER;
	private TileEntityItemStackRuleFlowData data;

	public RuleDrawerItem(
		FlowDrawer drawer,
		ManagerFlowController controller,
		TileEntityItemStackRuleFlowData rule
	) {
		super(rule.position, new Size(FlowItemStack.ITEM_WIDTH + 4, FlowItemStack.ITEM_HEIGHT + 4));
		this.DRAWER = drawer;
		this.CONTROLLER = controller;
		this.data = rule;
		this.ICON = new FlowItemStack(rule.icon, new Position(2, 2)) {
			@Override
			public void onSelectionChanged() {
				RuleDrawerItem.this.onClicked(isSelected());
			}
		};
		addChild(ICON);
		DRAWER.update();
	}

	public void onClicked(boolean activate) {
	}

	public FlowItemStack getIcon() {
		return ICON;
	}

	@Override
	public TileEntityItemStackRuleFlowData getData() {
		return data;
	}

	@Override
	public void setData(TileEntityItemStackRuleFlowData data) {
		this.data = data;
	}

	public void openRule() {
		if (!Client.allowMultipleRuleWindows) {
			CONTROLLER.getChildren().stream()
				.filter(c -> c instanceof FlowTileEntityRule)
				.map(c -> ((FlowTileEntityRule) c))
				.forEach(c -> {
					c.setVisible(false);
					c.setEnabled(false);
				});

			DRAWER.getChildren().stream()
				.filter(c -> c instanceof RuleDrawerItem)
				.forEach(c -> ((RuleDrawerItem) c).ICON.setSelected(false));
		}
		ICON.setSelected(true);
		CONTROLLER.findFirstChild(data.getId()).ifPresent(v -> v.setVisible(true));
	}
}
