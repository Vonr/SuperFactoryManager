/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.util.ruledrawer;

import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowDrawer;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class ItemStackTileEntityRuleDrawer extends FlowContainer implements Observer {

	private final ManagerFlowController CONTROLLER;
	private final FlowDrawer CHILDREN_RULES_DRAWER;
	private final FlowDrawer SELECTION_RULES_DRAWER;

	public ItemStackTileEntityRuleDrawer(ManagerFlowController controller, Position pos) {
		super(pos);

		this.CONTROLLER = controller;

//			I18n.format("gui.sfm.associatedrulesdrawer.children.label")
		this.CHILDREN_RULES_DRAWER = new FlowDrawer(
			new Position(),
			5,
			7
		);
		addChild(CHILDREN_RULES_DRAWER);

//			I18n.format("gui.sfm.associatedrulesdrawer.selection.label")
		this.SELECTION_RULES_DRAWER = new FlowDrawer(
			CHILDREN_RULES_DRAWER.getPosition().withConstantOffset(
				() -> CHILDREN_RULES_DRAWER.getMaxWidth() + 10,
				() -> 0
			),
			5,
			7
		);
		SELECTION_RULES_DRAWER.setVisible(false);
		SELECTION_RULES_DRAWER.setEnabled(false);

		setVisible(false);
		setEnabled(false);
		addChild(SELECTION_RULES_DRAWER);

		controller.SCREEN.getFlowDataContainer().addObserver(this);
		rebuildChildrenDrawer();
		rebuildSelectionDrawer();
	}

	/*
	TODO: label drawing

		if (Client.showRuleDrawerLabels) {
			int labelHeight = 15;
			screen.drawRect(
				matrixStack,
				getPosition().getX(),
				getPosition().getY()-labelHeight,
				getMaxWidth(),
				labelHeight,
				CONST.PANEL_BORDER
			);
			screen.drawString(
				matrixStack,
				LABEL_TEXT,
				getPosition().getX() + 5,
				getPosition().getY() - labelHeight + 4,
				CONST.TEXT_LIGHT
			);
		}
	 */

	public void rebuildChildrenDrawer() {
		CHILDREN_RULES_DRAWER.getChildren().clear();
		CHILDREN_RULES_DRAWER.addChild(new EditChildrenButton(SELECTION_RULES_DRAWER));
		getChildrenRules().stream()
			.map(rule -> new ChildRulesDrawerItem(rule, CONTROLLER, CHILDREN_RULES_DRAWER))
			.forEach(CHILDREN_RULES_DRAWER::addChild);
		CHILDREN_RULES_DRAWER.update();
	}

	public void rebuildSelectionDrawer() {
		SELECTION_RULES_DRAWER.getChildren().clear();
		SELECTION_RULES_DRAWER.addChild(new AddRuleButton(CONTROLLER));

		getSelectableRules().stream()
			.map(rule -> new SelectionRulesDrawerItem(rule, this))
			.forEach(SELECTION_RULES_DRAWER::addChild);

		// Ensure children rules are selected in the global rule drawer
		Set<UUID> selected = getChildrenRules().stream()
			.map(FlowData::getId)
			.collect(Collectors.toSet());
		SELECTION_RULES_DRAWER.getChildren().stream()
			.filter(c -> c instanceof SelectionRulesDrawerItem)
			.map(c -> ((SelectionRulesDrawerItem) c))
			.filter(c -> selected.contains(c.DATA.getId()))
			.forEach(c -> c.setSelected(true));

		SELECTION_RULES_DRAWER.update();
	}

	public abstract List<ItemStackTileEntityRuleFlowData> getChildrenRules();

	public abstract List<ItemStackTileEntityRuleFlowData> getSelectableRules();

	public abstract void setChildrenRules(List<UUID> rules);

	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof FlowDataContainerChange) {
			FlowDataContainerChange change = ((FlowDataContainerChange) arg);
			if (change.DATA instanceof ItemStackTileEntityRuleFlowData) {
				rebuildChildrenDrawer();
				rebuildSelectionDrawer();
			}
		}
	}

}
