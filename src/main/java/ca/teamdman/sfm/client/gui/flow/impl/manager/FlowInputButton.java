/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager;

import ca.teamdman.sfm.client.gui.flow.core.IFlowCloneable;
import ca.teamdman.sfm.client.gui.flow.core.IFlowDeletable;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.util.AssociatedRulesDrawer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton.ButtonLabel;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.FlowDataContainer.ChangeType;
import ca.teamdman.sfm.common.flow.data.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.core.RuleContainer;
import ca.teamdman.sfm.common.flow.data.impl.FlowTileInputData;
import ca.teamdman.sfm.common.flow.data.impl.RuleFlowData;
import ca.teamdman.sfm.common.flow.data.impl.TileEntityRuleFlowData;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.delete.ManagerDeletePacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.patch.ManagerPositionPacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.put.ManagerFlowInputDataPacketC2S;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class FlowInputButton extends FlowContainer implements IFlowDeletable,
	IFlowCloneable, FlowDataHolder, RuleContainer {

	private final FlowTileInputData DATA;
	private final AssociatedRulesDrawer DRAWER;
	private final ManagerFlowController CONTROLLER;
	private final FlowIconButton BUTTON;
	private boolean open = false;

	public FlowInputButton(
		ManagerFlowController controller,
		FlowTileInputData data
	) {
		this.DATA = data;
		this.CONTROLLER = controller;
		this.BUTTON = new MyFlowIconButton(
			ButtonLabel.INPUT,
			data.getPosition().copy()
		);
		this.DRAWER = new MyAssociatedRulesDrawer(
			controller,
			BUTTON.getPosition().withConstantOffset(25, 0)
		);
		addChild(BUTTON);
		addChild(DRAWER);
		DRAWER.setVisible(false);
		DRAWER.setEnabled(false);
		controller.SCREEN.onChange(null, this::onDataChanged);
	}

	public void onDataChanged(FlowData data, ChangeType changeType) {
		if (data instanceof RuleFlowData) {
			DRAWER.rebuildSelectionDrawer();
		}
	}


	@Override
	public void cloneWithPosition(int x, int y) {
		PacketHandler.INSTANCE.sendToServer(new ManagerFlowInputDataPacketC2S(
			CONTROLLER.SCREEN.CONTAINER.windowId,
			CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
			UUID.randomUUID(),
			new Position(x, y),
			DATA.tileEntityRules
		));
	}

	@Override
	public void delete() {
		PacketHandler.INSTANCE.sendToServer(new ManagerDeletePacketC2S(
			CONTROLLER.SCREEN.CONTAINER.windowId,
			CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
			DATA.getId()
		));
	}

	@Override
	public FlowData getData() {
		return DATA;
	}

	@Override
	public void onDataChanged() {
		BUTTON.getPosition().setXY(DATA.getPosition());
		DRAWER.rebuildChildrenDrawer();
	}

	@Override
	public List<UUID> getRules() {
		return DATA.tileEntityRules;
	}

	@Override
	public void setRules(List<UUID> rules) {
		PacketHandler.INSTANCE.sendToServer(new ManagerFlowInputDataPacketC2S(
			CONTROLLER.SCREEN.CONTAINER.windowId,
			CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
			DATA.getId(),
			DATA.getPosition(),
			rules
		));
	}

	private class MyFlowIconButton extends FlowIconButton {

		public MyFlowIconButton(ButtonLabel type, Position pos) {
			super(type, pos);
		}

		@Override
		public void onClicked(int mx, int my, int button) {
			open = !open;
			DRAWER.setVisible(open);
			DRAWER.setEnabled(open);
		}

		@Override
		public void onDragFinished(int dx, int dy, int mx, int my) {
			PacketHandler.INSTANCE.sendToServer(new ManagerPositionPacketC2S(
				CONTROLLER.SCREEN.CONTAINER.windowId,
				CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
				DATA.getId(),
				getPosition()
			));
		}
	}

	private class MyAssociatedRulesDrawer extends AssociatedRulesDrawer {

		public MyAssociatedRulesDrawer(ManagerFlowController controller, Position pos) {
			super(controller, pos);
		}

		@Override
		public List<RuleFlowData> getChildrenRules() {
			return CONTROLLER.SCREEN.getData(TileEntityRuleFlowData.class)
				.filter(d -> DATA.tileEntityRules.contains(d.getId()))
				.collect(Collectors.toList());
		}

		@Override
		public void setChildrenRules(List<UUID> rules) {
			setRules(rules);
		}

		@Override
		public List<RuleFlowData> getSelectableRules() {
			return CONTROLLER.SCREEN.getData(TileEntityRuleFlowData.class)
				.collect(Collectors.toList());
		}
	}
}
