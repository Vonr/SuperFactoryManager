/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.IFlowCloneable;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton.ButtonLabel;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.BasicTileOutputFlowData;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import java.util.Optional;
import java.util.UUID;

public class BasicTileOutputFlowButton extends FlowContainer implements
	IFlowCloneable, FlowDataHolder<BasicTileOutputFlowData> {

	private final ManagerFlowController CONTROLLER;
	private final FlowIconButton BUTTON;
	private ItemStackTileEntityRuleFlowData ruleData;
	private BasicTileOutputFlowData buttonData;

	public BasicTileOutputFlowButton(
		ManagerFlowController controller,
		BasicTileOutputFlowData buttonData,
		ItemStackTileEntityRuleFlowData ruleData
	) {
		this.buttonData = buttonData;
		this.ruleData = ruleData;
		this.CONTROLLER = controller;

		this.BUTTON = new MyFlowIconButton(
			ButtonLabel.OUTPUT,
			buttonData.getPosition().copy()
		);
		BUTTON.setDraggable(true);
		addChild(BUTTON);

		controller.SCREEN.getFlowDataContainer()
			.addObserver(new FlowDataHolderObserver<>(BasicTileOutputFlowData.class, this));
		controller.SCREEN.getFlowDataContainer().addObserver(new FlowDataHolderObserver<>(
			ItemStackTileEntityRuleFlowData.class,
			data -> data.getId().equals(ruleData.getId()),
			data -> this.ruleData = data
		));
	}

	@Override
	public void cloneWithPosition(int x, int y) {
		FlowData newRule = ruleData.copyWithNewId();
		CONTROLLER.SCREEN.sendFlowDataToServer(
			newRule,
			new BasicTileOutputFlowData(
				UUID.randomUUID(),
				new Position(x, y),
				newRule.getId()
			)
		);
	}

	@Override
	public BasicTileOutputFlowData getData() {
		return buttonData;
	}

	@Override
	public void setData(BasicTileOutputFlowData data) {
		this.buttonData = data;
		BUTTON.getPosition().setXY(this.buttonData.getPosition());
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
			CONTROLLER.findFirstChild(buttonData.tileEntityRule)
				.ifPresent(FlowComponent::toggleVisibilityAndEnabled);
		}

		@Override
		public void onDragFinished(int dx, int dy, int mx, int my) {
			buttonData.position = getPosition();
			CONTROLLER.SCREEN.sendFlowDataToServer(buttonData);
		}

		@Override
		protected boolean isDepressed() {
			return super.isDepressed() || ruleData.open;
		}
	}
}
