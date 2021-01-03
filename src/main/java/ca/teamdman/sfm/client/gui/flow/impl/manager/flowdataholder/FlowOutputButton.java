/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
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
import ca.teamdman.sfm.common.flow.data.impl.TileEntityItemStackRuleFlowData;
import ca.teamdman.sfm.common.flow.data.impl.TileOutputFlowData;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.delete.ManagerDeletePacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.patch.ManagerPositionPacketC2S;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FlowOutputButton extends FlowContainer implements IFlowDeletable,
	IFlowCloneable, FlowDataHolder, RuleContainer {

	private final TileOutputFlowData DATA;
	private final AssociatedRulesDrawer DRAWER;
	private final ManagerFlowController CONTROLLER;
	private final FlowIconButton BUTTON;
	private boolean open = false;

	public FlowOutputButton(
		ManagerFlowController controller,
		TileOutputFlowData data
	) {
		this.DATA = data;
		this.CONTROLLER = controller;
		this.BUTTON = new MyFlowIconButton(
			ButtonLabel.OUTPUT,
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
		DRAWER.setDraggable(false);
		controller.SCREEN.onChange(null, this::onDataChanged);
	}

	@Override
	public Position snapToEdge(Position outside) {
		return BUTTON.snapToEdge(outside);
	}

	public void onDataChanged(FlowData data, ChangeType changeType) {
		if (data instanceof TileEntityItemStackRuleFlowData) {
			DRAWER.rebuildSelectionDrawer();
		}
	}


	@Override
	public void cloneWithPosition(int x, int y) {
		CONTROLLER.SCREEN.sendFlowDataToServer(
			new TileOutputFlowData(
				UUID.randomUUID(),
				new Position(x, y),
				DATA.tileEntityRules
			)
		);
	}

	@Override
	public void delete() {
		PacketHandler.INSTANCE.sendToServer(new ManagerDeletePacketC2S(
			CONTROLLER.SCREEN.getContainer().windowId,
			CONTROLLER.SCREEN.getContainer().getSource().getPos(),
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
		CONTROLLER.SCREEN.sendFlowDataToServer(
			new TileOutputFlowData(
				DATA.getId(),
				DATA.getPosition(),
				rules
			)
		);
	}

	@Override
	public Position getCentroid() {
		return BUTTON.getCentroid();
	}

	@Override
	public Optional<FlowComponent> getElementUnderMouse(int mx, int my) {
		return super.getElementUnderMouse(mx, my).map(__ -> this);
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
				CONTROLLER.SCREEN.getContainer().windowId,
				CONTROLLER.SCREEN.getContainer().getSource().getPos(),
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
		public List<TileEntityItemStackRuleFlowData> getChildrenRules() {
			return CONTROLLER.SCREEN.getData(TileEntityItemStackRuleFlowData.class)
				.filter(d -> DATA.tileEntityRules.contains(d.getId()))
				.collect(Collectors.toList());
		}

		@Override
		public void setChildrenRules(List<UUID> rules) {
			setRules(rules);
		}

		@Override
		public List<TileEntityItemStackRuleFlowData> getSelectableRules() {
			return CONTROLLER.SCREEN.getData(TileEntityItemStackRuleFlowData.class)
				.collect(Collectors.toList());
		}
	}
}
