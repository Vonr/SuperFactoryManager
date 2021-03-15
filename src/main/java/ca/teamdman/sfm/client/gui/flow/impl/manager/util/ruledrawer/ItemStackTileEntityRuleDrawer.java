/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.util.ruledrawer;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowDrawer;
import ca.teamdman.sfm.common.config.Config.Client;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.ItemRuleFlowData;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.client.resources.I18n;

public abstract class ItemStackTileEntityRuleDrawer extends FlowContainer implements Observer {

	protected final PlusButton PLUS_BUTTON;
	protected final ManagerFlowController CONTROLLER;
	protected final FlowDrawer DRAWER;
	protected boolean isGlobalOpen = false;
	protected String drawerLabel = I18n.format("gui.sfm.associatedrulesdrawer.children.label");

	public ItemStackTileEntityRuleDrawer(ManagerFlowController controller, Position pos) {
		super(pos);

		this.CONTROLLER = controller;
		this.PLUS_BUTTON = new PlusButton(this);
		this.DRAWER = new FlowDrawer(
			new Position(),
			5,
			7
		);
		addChild(DRAWER);

		setVisibleAndEnabled(false);
		rebuildDrawer();
		controller.SCREEN.getFlowDataContainer().addObserver(this);
	}

	public void rebuildDrawer() {
		if (isGlobalOpen) {
			rebuildGlobalDrawer();
			drawerLabel = I18n.format("gui.sfm.associatedrulesdrawer.selection.label");
		} else {
			rebuildChildrenDrawer();
			drawerLabel = I18n.format("gui.sfm.associatedrulesdrawer.children.label");
		}
	}

	private void rebuildGlobalDrawer() {
		DRAWER.getChildren().clear();
		DRAWER.addChild(PLUS_BUTTON);

		CONTROLLER.SCREEN.getFlowDataContainer()
			.get(ItemRuleFlowData.class)
			.collect(Collectors.toList()).stream()
			.map(rule -> new GlobalRulesDrawerItem(rule, this))
			.forEach(DRAWER::addChild);

		// Ensure children rules are selected in the global rule drawer
		List<UUID> selected = getChildrenRuleIds();
		DRAWER.getChildren().stream()
			.filter(c -> c instanceof GlobalRulesDrawerItem)
			.map(c -> ((GlobalRulesDrawerItem) c))
			.filter(c -> selected.contains(c.DATA.getId()))
			.forEach(c -> c.setSelected(true));

		DRAWER.update();
	}

	private void rebuildChildrenDrawer() {
		DRAWER.getChildren().clear();
		DRAWER.addChild(PLUS_BUTTON);
		getChildrenRules().stream()
			.map(rule -> new ChildRulesDrawerItem(this, rule))
			.forEach(DRAWER::addChild);
		DRAWER.update();
	}

	public abstract List<ItemRuleFlowData> getChildrenRules();

	protected List<UUID> getChildrenRuleIds() {
		return getChildrenRules().stream()
			.map(FlowData::getId)
			.collect(Collectors.toList());
	}

	public abstract FlowData getDataWithNewChildren(List<UUID> rules);

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		if (Client.showRuleDrawerLabels) {
			int labelHeight = 15;
			screen.drawRect(
				matrixStack,
				getPosition().getX(),
				getPosition().getY() - labelHeight,
				DRAWER.getMaxWidth(),
				labelHeight,
				CONST.PANEL_BORDER
			);
			screen.drawString(
				matrixStack,
				drawerLabel,
				getPosition().getX() + 5,
				getPosition().getY() - labelHeight + 4,
				CONST.TEXT_LIGHT
			);
		}
		super.draw(screen, matrixStack, mx, my, deltaTime);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof FlowDataContainerChange) {
			FlowDataContainerChange change = ((FlowDataContainerChange) arg);
			if (change.DATA instanceof ItemRuleFlowData) {
				rebuildDrawer();
			}
		}
	}

}
