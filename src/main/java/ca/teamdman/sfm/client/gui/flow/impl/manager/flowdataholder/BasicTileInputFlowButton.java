/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.IFlowCloneable;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.ButtonLabel;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowSprite;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.BasicTileInputFlowData;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;

public class BasicTileInputFlowButton extends FlowContainer implements
	IFlowCloneable, FlowDataHolder<BasicTileInputFlowData> {

	private final ManagerFlowController CONTROLLER;
	private final MyFlowIconButton BUTTON;
	private ItemStackTileEntityRuleFlowData ruleData;
	private BasicTileInputFlowData buttonData;

	public BasicTileInputFlowButton(
		ManagerFlowController controller,
		BasicTileInputFlowData buttonData,
		ItemStackTileEntityRuleFlowData ruleData
	) {
		this.buttonData = buttonData;
		this.ruleData = ruleData;
		this.CONTROLLER = controller;

		this.BUTTON = new MyFlowIconButton(
			ButtonLabel.INPUT,
			buttonData.getPosition().copy()
		);
		BUTTON.setDraggable(true);
		BUTTON.reloadFromRuleData();
		addChild(BUTTON);

		controller.SCREEN.getFlowDataContainer()
			.addObserver(new FlowDataHolderObserver<>(BasicTileInputFlowData.class, this));
		controller.SCREEN.getFlowDataContainer().addObserver(new FlowDataHolderObserver<>(
			ItemStackTileEntityRuleFlowData.class,
			data -> data.getId().equals(ruleData.getId()),
			this::setRuleData
		));
	}

	public void setRuleData(ItemStackTileEntityRuleFlowData data) {
		this.ruleData = data;
		this.BUTTON.reloadFromRuleData();
	}

	@Override
	public void cloneWithPosition(int x, int y) {
		FlowData newRule = ruleData.copyWithNewId();
		CONTROLLER.SCREEN.sendFlowDataToServer(
			newRule,
			new BasicTileInputFlowData(
				UUID.randomUUID(),
				new Position(x, y),
				newRule.getId()
			)
		);
	}

	@Override
	public BasicTileInputFlowData getData() {
		return buttonData;
	}

	@Override
	public void setData(BasicTileInputFlowData data) {
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
		public List<? extends ITextProperties> getTooltip() {
			List<ITextProperties> rtn = new ArrayList<>();
			rtn.add(new StringTextComponent(ruleData.name));
			return rtn;
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

		public void reloadFromRuleData() {
			if (ruleData.getIcon().isEmpty()) {
				// no custom icon, use default label
				LABEL = ButtonLabel.INPUT.SPRITE;
			} else {
				// custom icon, hide the default label
				LABEL = FlowSprite.EMPTY;
			}
		}

		@Override
		public void drawGhost(
			BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
		) {
			super.drawGhost(screen, matrixStack, mx, my, deltaTime);
			if (LABEL == FlowSprite.EMPTY) {
				// custom icon is set, draw custom stack
				screen.drawItemStack(
					matrixStack,
					ruleData.getIcon(),
					getPosition().getX() + 3,
					getPosition().getY() + 3
				);
			}
		}

		@Override
		protected boolean isDepressed() {
			return super.isDepressed() || ruleData.open;
		}

		@Override
		public void draw(
			BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
		) {
			super.draw(screen, matrixStack, mx, my, deltaTime);
			if (LABEL == FlowSprite.EMPTY) {
				// custom icon is set, draw custom stack
				screen.drawItemStack(
					matrixStack,
					ruleData.getIcon(),
					getPosition().getX() + 3,
					getPosition().getY() + 3
				);
			}
		}
	}
}
