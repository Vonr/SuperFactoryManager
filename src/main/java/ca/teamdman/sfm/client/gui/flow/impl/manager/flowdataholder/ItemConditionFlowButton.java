/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.ButtonLabel;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowSprite;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.ItemConditionFlowData;
import ca.teamdman.sfm.common.flow.data.ItemConditionRuleFlowData;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;

public class ItemConditionFlowButton extends FlowContainer implements
	FlowDataHolder<ItemConditionFlowData> {

	private final ManagerFlowController CONTROLLER;
	private final MyFlowIconButton BUTTON;
	private ItemConditionRuleFlowData ruleData;
	private ItemConditionFlowData buttonData;

	public ItemConditionFlowButton(
		ManagerFlowController controller,
		ItemConditionFlowData buttonData,
		ItemConditionRuleFlowData ruleData
	) {
		this.buttonData = buttonData;
		this.ruleData = ruleData;
		this.CONTROLLER = controller;

		this.BUTTON = new MyFlowIconButton(
			ButtonLabel.CONDITIONAL,
			buttonData.getPosition().copy()
		);
		BUTTON.setDraggable(true);
		BUTTON.reloadFromRuleData();
		addChild(BUTTON);

		controller.SCREEN.getFlowDataContainer()
			.addObserver(new FlowDataHolderObserver<>(ItemConditionFlowData.class, this));
		controller.SCREEN.getFlowDataContainer().addObserver(new FlowDataHolderObserver<>(
			ItemConditionRuleFlowData.class,
			data -> data.getId().equals(ruleData.getId()),
			this::setRuleData
		));
	}

	public void setRuleData(ItemConditionRuleFlowData data) {
		this.ruleData = data;
		this.BUTTON.reloadFromRuleData();
	}

	@Override
	public ItemConditionFlowData getData() {
		return buttonData;
	}

	@Override
	public void setData(ItemConditionFlowData data) {
		this.buttonData = data;
		BUTTON.getPosition().setXY(this.buttonData.getPosition());
	}

	@Override
	public boolean isDeletable() {
		return true;
	}

	@Override
	public boolean isCloneable() {
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
	public Stream<? extends FlowComponent> getElementsUnderMouse(int mx, int my) {
		return BUTTON.isElementUnderMouse(mx, my) ? Stream.of(this) : Stream.empty();
	}

	private class MyFlowIconButton extends FlowIconButton {

		public MyFlowIconButton(ButtonLabel type, Position pos) {
			super(type, pos);
		}

		@Override
		public void onClicked(int mx, int my, int button) {
			CONTROLLER.findFirstChild(buttonData.rule)
				.ifPresent(FlowComponent::toggleVisibilityAndEnabled);
		}

		@Override
		public void onDragFinished(int dx, int dy, int mx, int my) {
			buttonData.position = getPosition();
			CONTROLLER.SCREEN.sendFlowDataToServer(buttonData);
		}

		@Override
		public List<? extends ITextProperties> getTooltip() {
			return Collections.singletonList(new StringTextComponent(ruleData.name));
		}

		public void reloadFromRuleData() {
			if (ruleData.getIcon().isEmpty()) {
				// no custom icon, use default label
				LABEL = ButtonLabel.CONDITIONAL.SPRITE;
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
			if (LABEL == FlowSprite.EMPTY) {
				GlStateManager.color4f(0.9f, 0.5f, 0.5f, 1f);
			}
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
