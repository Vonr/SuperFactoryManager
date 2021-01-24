/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.IFlowCloneable;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.util.ruledrawer.ItemStackTileEntityRuleDrawer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton.ButtonLabel;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData;
import ca.teamdman.sfm.common.flow.data.TileInputFlowData;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class FlowInputButton extends FlowContainer implements
	IFlowCloneable, FlowDataHolder<TileInputFlowData> {

	private final ItemStackTileEntityRuleDrawer DRAWER;
	private final ManagerFlowController CONTROLLER;
	private final FlowIconButton BUTTON;
	private TileInputFlowData data;

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
		BUTTON.setDraggable(true);
		addChild(BUTTON);

		this.DRAWER = new MyItemStackTileEntityRuleDrawer(
			controller,
			BUTTON.getPosition().withConstantOffset(25, 0)
		);
		addChild(DRAWER);

		controller.SCREEN.getFlowDataContainer().addObserver(new FlowDataHolderObserver<>(
			this,
			TileInputFlowData.class
		));
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
		DRAWER.rebuildDrawer();
	}

	@Override
	public boolean isDeletable() {
		return true;
	}

	@Override
	public Position getCentroid() {
		return BUTTON.getCentroid();
	}

	@Override
	public Position snapToEdge(Position outside) {
		return BUTTON.snapToEdge(outside);
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
			DRAWER.setVisible(!DRAWER.isVisible());
			DRAWER.setEnabled(DRAWER.isVisible());
		}

		@Override
		public void onDragFinished(int dx, int dy, int mx, int my) {
			data.position = getPosition();
			CONTROLLER.SCREEN.sendFlowDataToServer(data);
		}
	}

	private class MyItemStackTileEntityRuleDrawer extends ItemStackTileEntityRuleDrawer {

		public MyItemStackTileEntityRuleDrawer(ManagerFlowController controller, Position pos) {
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
		public FlowData getDataWithNewChildren(List<UUID> rules) {
			return new TileInputFlowData(
				data.getId(),
				data.getPosition(),
				rules
			);
		}
	}
}
