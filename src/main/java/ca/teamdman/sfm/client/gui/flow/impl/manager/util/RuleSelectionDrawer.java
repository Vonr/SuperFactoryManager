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
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;

public abstract class RuleSelectionDrawer extends FlowDrawer {

	final ManagerFlowController CONTROLLER;

	public RuleSelectionDrawer(
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
			.map(data -> new DrawerItem(CONTROLLER, data))
			.forEach(this::addChild);
		update();
	}

	public abstract void onSelectionChanged(List<FlowRuleData> data);

	private class DrawerItem extends RuleDrawerItem {

		public DrawerItem(
			ManagerFlowController controller,
			FlowRuleData rule
		) {
			super(RuleSelectionDrawer.this, controller, rule);
		}

		@Override
		public void onClicked(boolean activate) {
			onSelectionChanged(RuleSelectionDrawer.this.getChildren().stream()
				.filter(c -> c instanceof DrawerItem)
				.map(c -> ((DrawerItem) c))
				.filter(c -> c.getIcon().isSelected())
				.map(c -> ((FlowRuleData) c.getData()))
				.collect(Collectors.toList()));
		}
	}

	private class AddRuleButton extends FlowPlusButton {
		private ItemStack[] items = {
			new ItemStack(Blocks.BEACON),
			new ItemStack(Blocks.STONE),
			new ItemStack(Blocks.SAND),
			new ItemStack(Blocks.SANDSTONE),
			new ItemStack(Blocks.TURTLE_EGG),
			new ItemStack(Blocks.DRAGON_EGG),
			new ItemStack(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE),
			new ItemStack(Blocks.CREEPER_HEAD),
		};

		public AddRuleButton(
		) {
			super(
				new Position(),
				new Size(FlowItemStack.ITEM_TOTAL_WIDTH, FlowItemStack.ITEM_TOTAL_HEIGHT),
				CONST.SELECTED
			);
		}

		@Override
		public void onClicked(int mx, int my, int button) {
			PacketHandler.INSTANCE.sendToServer(new ManagerCreateTileEntityRulePacketC2S(
				CONTROLLER.SCREEN.CONTAINER.windowId,
				CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
				"New tile entity rule",
				items[(int) (Math.random()* items.length)],
				new Position(0, 0)
			));
		}
	}
}
