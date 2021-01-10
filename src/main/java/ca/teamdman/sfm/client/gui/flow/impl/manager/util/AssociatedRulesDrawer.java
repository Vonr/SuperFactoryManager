/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.util;

import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstacktileentityrule.ItemStackTileEntityRuleFlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowDrawer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowPlusButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.ItemStackFlowComponent;
import ca.teamdman.sfm.common.config.Config.Client;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData.FilterMode;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;

public abstract class AssociatedRulesDrawer extends FlowContainer implements Observer {

	private final ManagerFlowController CONTROLLER;
	private final FlowDrawer CHILDREN_RULES_DRAWER;
	private final FlowDrawer SELECTION_RULES_DRAWER;

	public AssociatedRulesDrawer(ManagerFlowController controller, Position pos) {
		super(pos);

		this.CONTROLLER = controller;

//			I18n.format("gui.sfm.associatedrulesdrawer.children.label")
		this.CHILDREN_RULES_DRAWER = new FlowDrawer(
			new Position(),
			5,
			7
		);
		CHILDREN_RULES_DRAWER.setDraggable(false);
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
		SELECTION_RULES_DRAWER.setDraggable(false);
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
		CHILDREN_RULES_DRAWER.addChild(new EditChildrenButton());
		getChildrenRules().stream()
			.map(ChildRulesDrawerItem::new)
			.forEach(CHILDREN_RULES_DRAWER::addChild);
		CHILDREN_RULES_DRAWER.update();
	}

	public void rebuildSelectionDrawer() {
		SELECTION_RULES_DRAWER.getChildren().clear();
		SELECTION_RULES_DRAWER.addChild(new AddRuleButton());

		getSelectableRules().stream()
			.map(SelectionRulesDrawerItem::new)
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

	private class ChildRulesDrawerItem extends ItemStackFlowComponent implements
		FlowDataHolder<ItemStackTileEntityRuleFlowData> {

		public ItemStackTileEntityRuleFlowData data;

		public ChildRulesDrawerItem(
			ItemStackTileEntityRuleFlowData data
		) {
			super(data.getIcon(), new Position());
			setDraggable(false);
			setData(data);
			CONTROLLER.SCREEN.getFlowDataContainer().addObserver(new FlowDataHolderObserver<>(
				this,
				ItemStackTileEntityRuleFlowData.class
			));
		}


		private void refreshSelection() {
			setSelected(CONTROLLER.findFirstChild(data.getId())
				.filter(FlowComponent::isVisible)
				.isPresent());
		}

		@Override
		public List<? extends ITextProperties> getTooltip() {
			ArrayList<ITextComponent> list = new ArrayList<>();
			list.add(new StringTextComponent(data.name));
			return list;
		}

		@Override
		public void onSelectionChanged() {
			if (!Client.allowMultipleRuleWindows && isSelected()) {
				CONTROLLER.getChildren().stream()
					.filter(c -> c instanceof ItemStackTileEntityRuleFlowComponent)
					.map(c -> ((ItemStackTileEntityRuleFlowComponent) c))
					.forEach(c -> {
						c.setVisible(false);
						c.setEnabled(false);
					});
				CHILDREN_RULES_DRAWER.getChildren().stream()
					.filter(c -> c instanceof ChildRulesDrawerItem && c != this)
					.forEach(c -> ((ChildRulesDrawerItem) c).setSelected(false));
			}
			CONTROLLER.findFirstChild(data.getId()).ifPresent(comp -> {
				comp.setVisible(isSelected());
				comp.setEnabled(isSelected());
			});
		}

		@Override
		public ItemStackTileEntityRuleFlowData getData() {
			return data;
		}

		@Override
		public void setData(ItemStackTileEntityRuleFlowData data) {
			this.data = data;
			refreshSelection();
		}
	}

	private class SelectionRulesDrawerItem extends ItemStackFlowComponent {

		public ItemStackTileEntityRuleFlowData DATA;

		public SelectionRulesDrawerItem(
			ItemStackTileEntityRuleFlowData data
		) {
			super(data.getIcon(), new Position());
			this.DATA = data;
			setDraggable(false);
		}

		@Override
		public List<? extends ITextProperties> getTooltip() {
			ArrayList<ITextComponent> list = new ArrayList<>();
			list.add(new StringTextComponent(DATA.name));
			return list;
		}

		@Override
		public void onSelectionChanged() {
			List<UUID> next = getChildrenRules().stream()
				.map(FlowData::getId)
				.collect(Collectors.toList());
			if (isSelected()) {
				next.add(DATA.getId());
			} else {
				next.remove(DATA.getId());
			}
			setChildrenRules(next);
		}
	}

	private class EditChildrenButton extends FlowPlusButton {

		private boolean open = false;

		public EditChildrenButton() {
			super(
				new Position(),
				ItemStackFlowComponent.DEFAULT_SIZE.copy(),
				CONST.SELECTED
			);
			setDraggable(false);
		}

		@Override
		public void onClicked(int mx, int my, int button) {
			open = !open;
			SELECTION_RULES_DRAWER.setEnabled(open);
			SELECTION_RULES_DRAWER.setVisible(open);
		}
	}

	private class AddRuleButton extends FlowPlusButton {

		private final ItemStack[] items = {
			new ItemStack(Blocks.BEACON),
			new ItemStack(Blocks.STONE),
			new ItemStack(Blocks.SAND),
			new ItemStack(Blocks.SANDSTONE),
			new ItemStack(Blocks.TURTLE_EGG),
			new ItemStack(Blocks.DRAGON_EGG),
			new ItemStack(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE),
			new ItemStack(Blocks.CREEPER_HEAD),
		};

		public AddRuleButton() {
			super(
				new Position(),
				ItemStackFlowComponent.DEFAULT_SIZE.copy(),
				CONST.SELECTED
			);
			setDraggable(false);
		}

		@Override
		public void onClicked(int mx, int my, int button) {
			//todo: remove debug item icons, or put more effort into random rule icons
			//todo: abstract onclicked???
			CONTROLLER.SCREEN.sendFlowDataToServer(
				new ItemStackTileEntityRuleFlowData(
					UUID.randomUUID(),
					"New tile entity rule",
					items[(int) (Math.random() * items.length)],
					new Position(0, 0),
					FilterMode.WHITELIST,
					Collections.emptyList()
				)
			);
		}
	}
}
