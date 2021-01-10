/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstacktileentityrule;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowDrawer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowRadioButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowRadioButton.RadioGroup;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.ItemStackMatcher;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData.FilterMode;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.patch.ManagerPositionPacketC2S;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Optional;
import net.minecraft.client.resources.I18n;

public class ItemStackTileEntityRuleFlowComponent extends FlowContainer implements
	FlowDataHolder<ItemStackTileEntityRuleFlowData> {

	protected final ManagerFlowController CONTROLLER;
	protected final FlowDrawer MATCHER_DRAWER;
	private final IconComponent ICON;
	private final FlowRadioButton WHITELIST_BUTTON;
	private final FlowRadioButton BLACKLIST_BUTTON;
	private final RadioGroup ITEM_SELECTION_MODE_GROUP;
	private final AddMatcherButton ADD_MATCHER_BUTTON;
	private ItemStackTileEntityRuleFlowData data;
	private String name = "Tile Entity Rule";

	public ItemStackTileEntityRuleFlowComponent(
		ManagerFlowController controller, ItemStackTileEntityRuleFlowData data
	) {
		super(data.getPosition(), new Size(200, 200));
		this.CONTROLLER = controller;
		this.data = data;

		//region toolbar
		addChild(new MinimizeButton(
			this, new Position(180, 5),
			new Size(10, 10)
		));
		//endregion

		//region icon
		addChild(new SectionHeader(
			new Position(5, 25),
			new Size(35, 12),
			I18n.format("gui.sfm.manager.tile_entity_rule.icon.title")
		));

		this.ICON = new IconComponent(this, new Position(5, 40));
		addChild(ICON);

		//endregion

		//region items
		addChild(new SectionHeader(
			new Position(5, 70),
			new Size(35, 12),
			I18n.format("gui.sfm.manager.tile_entity_rule.items.title")
		));

		this.ITEM_SELECTION_MODE_GROUP = new RadioGroup() {
			@Override
			public void onSelectionChanged(FlowRadioButton member) {
				FilterMode next = member == WHITELIST_BUTTON
					? FilterMode.WHITELIST
					: FilterMode.BLACKLIST;
				if (data.filterMode != next) {
					data.filterMode = next;
					CONTROLLER.SCREEN.sendFlowDataToServer(data);
				}
			}
		};
		WHITELIST_BUTTON = new FlowRadioButton(
			new Position(5, 85),
			new Size(35, 12),
			I18n.format("gui.sfm.flow.tileentityrule.button.whitelist"),
			ITEM_SELECTION_MODE_GROUP
		);
		addChild(WHITELIST_BUTTON);

		BLACKLIST_BUTTON = new FlowRadioButton(
			new Position(45, 85),
			new Size(35, 12),
			I18n.format("gui.sfm.flow.tileentityrule.button.blacklist"),
			ITEM_SELECTION_MODE_GROUP
		);
		ITEM_SELECTION_MODE_GROUP.setSelected(
			data.filterMode == FilterMode.WHITELIST
				? WHITELIST_BUTTON
				: BLACKLIST_BUTTON);
		addChild(BLACKLIST_BUTTON);

		MATCHER_DRAWER = new FlowDrawer(new Position(5, 105), 4, 3);
		MATCHER_DRAWER.setShrinkToFit(false);
		ADD_MATCHER_BUTTON = new AddMatcherButton(
			CONTROLLER, data, new Position(5, getSize().getHeight() - 21));
		rebuildMatcherDrawerChildren();
		addChild(MATCHER_DRAWER);

		//endregion

		//region default behaviour
		// Hide by default
		setVisible(false);
		setEnabled(false);

		// Add change listener
		CONTROLLER.SCREEN.getFlowDataContainer().addObserver(new FlowDataHolderObserver<>(
			this,
			ItemStackTileEntityRuleFlowData.class
		));
		//endregion

		setDraggable(true);
	}

	public void rebuildMatcherDrawerChildren() {
		MATCHER_DRAWER.getChildren().clear();
		MATCHER_DRAWER.addChild(ADD_MATCHER_BUTTON);
		data.matcherIds.stream()
			.map(CONTROLLER::findFirstChild)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.filter(FlowDataHolder.class::isInstance)
			.filter(c -> ((FlowDataHolder<?>) c).getData() instanceof ItemStackMatcher)
			.map(c -> new MatcherDrawerItem(this, c))
			.forEach(MATCHER_DRAWER::addChild);
		MATCHER_DRAWER.update();
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		screen.clearRect(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getWidth()
		);

		screen.drawRect(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getHeight(),
			CONST.PANEL_BACKGROUND_NORMAL
		);

		screen.drawBorder(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getHeight(),
			2,
			CONST.PANEL_BORDER
		);

		screen.drawString(
			matrixStack,
			data.name,
			getPosition().getX() + 5,
			getPosition().getY() + 5,
			CONST.TEXT_LIGHT
		);

		super.draw(screen, matrixStack, mx, my, deltaTime);
	}

	@Override
	public ItemStackTileEntityRuleFlowData getData() {
		return data;
	}

	@Override
	public void setData(ItemStackTileEntityRuleFlowData data) {
		this.data = data;
		getPosition().setXY(data.getPosition());
		ICON.BUTTON.setItemStack(data.icon);
		ITEM_SELECTION_MODE_GROUP.setSelected(
			data.filterMode == FilterMode.WHITELIST
				? WHITELIST_BUTTON
				: BLACKLIST_BUTTON
		);
		rebuildMatcherDrawerChildren();
	}

	@Override
	public void onDragFinished(int dx, int dy, int mx, int my) {
		PacketHandler.INSTANCE.sendToServer(new ManagerPositionPacketC2S(
			CONTROLLER.SCREEN.getContainer().windowId,
			CONTROLLER.SCREEN.getContainer().getSource().getPos(),
			data.getId(),
			this.getPosition()
		));
	}

	@Override
	public int getZIndex() {
		return super.getZIndex() + 100;
	}

}
