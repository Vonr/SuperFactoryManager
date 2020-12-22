/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.util;

import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowDrawer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowItemStack;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowPlusButton;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.impl.FlowRuleData;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateTileEntityRulePacketC2S;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;

public class ExistingRulesDrawer extends FlowDrawer {

	final ManagerFlowController CONTROLLER;

	public ExistingRulesDrawer(
		ManagerFlowController controller, Position pos
	) {
		super(pos, FlowItemStack.ITEM_TOTAL_WIDTH, FlowItemStack.ITEM_TOTAL_HEIGHT);
		this.CONTROLLER = controller;
		rebuildChildren();
		CONTROLLER.SCREEN.onChange(null, (data, type) -> {
			if (data instanceof FlowRuleData) {
				this.rebuildChildren();
			}
		});
	}

	public void rebuildChildren() {
		getChildren().clear();
		addChild(new AddRuleButton());
		CONTROLLER.SCREEN.getData(FlowRuleData.class)
			.map(RuleDrawerItem::new)
			.forEach(this::addChild);
		update();
	}

	public static class RuleDrawerItem extends FlowItemStack {

		private final FlowRuleData DATA;

		public RuleDrawerItem(FlowRuleData rule) {
			super(rule.icon, new Position());
			this.DATA = rule;
		}
	}

	public class AddRuleButton extends FlowPlusButton {

		public AddRuleButton(
		) {
			super(new Position(), new Size(24, 24), CONST.SELECTED);
		}

		@Override
		public void onClicked(int mx, int my, int button) {
			PacketHandler.INSTANCE.sendToServer(new ManagerCreateTileEntityRulePacketC2S(
				CONTROLLER.SCREEN.CONTAINER.windowId,
				CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
				"New tile entity rule",
				new ItemStack(Blocks.STONE),
				new Position(0, 0)
			));
		}
	}
}
