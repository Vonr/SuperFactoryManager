/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.IFlowCloneable;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.util.AssociatedRulesDrawer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton.ButtonLabel;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.core.RuleContainer;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData;
import ca.teamdman.sfm.common.flow.data.TileInputFlowData;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.patch.ManagerPositionPacketC2S;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FlowInputButton extends FlowContainer implements
	IFlowCloneable, FlowDataHolder<TileInputFlowData>, RuleContainer {

	private final AssociatedRulesDrawer DRAWER;
	private final ManagerFlowController CONTROLLER;
	private final FlowIconButton BUTTON;
	private TileInputFlowData data;
	private boolean open = false;

	public FlowInputButton(
		ManagerFlowController controller,
		TileInputFlowData data
	) {
		this.data = data;
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
		BUTTON.setDraggable(true);
		controller.SCREEN.getFlowDataContainer().addObserver(new FlowDataHolderObserver<>(
			this,
			TileInputFlowData.class
		));
	}


	@Override
	public Position snapToEdge(Position outside) {
		return BUTTON.snapToEdge(outside);
	}

	@Override
	public void cloneWithPosition(int x, int y) {
		CONTROLLER.SCREEN.sendFlowDataToServer(
			new TileInputFlowData(
				UUID.randomUUID(),
				new Position(x, y),
				data.tileEntityRules
			)
		);
	}

	@Override
	public TileInputFlowData getData() {
		return data;
	}

	@Override
	public void setData(TileInputFlowData data) {
		this.data = data;
		BUTTON.getPosition().setXY(this.data.getPosition());
		DRAWER.rebuildChildrenDrawer();
	}

	@Override
	public boolean isDeletable() {
		return true;
	}

	@Override
	public List<UUID> getRules() {
		return data.tileEntityRules;
	}

	@Override
	public void setRules(List<UUID> rules) {
		CONTROLLER.SCREEN.sendFlowDataToServer(
			new TileInputFlowData(
				data.getId(),
				data.getPosition(),
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
				data.getId(),
				getPosition()
			));
		}
	}

	private class MyAssociatedRulesDrawer extends AssociatedRulesDrawer {

		public MyAssociatedRulesDrawer(ManagerFlowController controller, Position pos) {
			super(controller, pos);
		}

		@Override
		public List<ItemStackTileEntityRuleFlowData> getChildrenRules() {
			return CONTROLLER.SCREEN.getFlowDataContainer()
				.get(ItemStackTileEntityRuleFlowData.class)
				.filter(d -> data.tileEntityRules.contains(d.getId()))
				.collect(Collectors.toList());
		}

		@Override
		public void setChildrenRules(List<UUID> rules) {
			setRules(rules);
		}

		@Override
		public List<ItemStackTileEntityRuleFlowData> getSelectableRules() {
			return CONTROLLER.SCREEN.getFlowDataContainer()
				.get(ItemStackTileEntityRuleFlowData.class)
				.collect(Collectors.toList());
		}
	}
}
