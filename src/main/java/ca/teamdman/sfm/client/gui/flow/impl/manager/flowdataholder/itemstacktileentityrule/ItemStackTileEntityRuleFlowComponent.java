/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstacktileentityrule;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import com.mojang.blaze3d.matrix.MatrixStack;

public class ItemStackTileEntityRuleFlowComponent extends FlowContainer implements
	FlowDataHolder<ItemStackTileEntityRuleFlowData> {

	public final ManagerFlowController CONTROLLER;
	private final TilesSection TILES_SECTION;
	private final ItemsSection ITEMS_SECTION;
	private final IconSection ICON_SECTION;
	private final FilterSection FILTER_SECTION;
	private final ToolbarSection TOOLBAR_SECTION;
	private final FacesSection FACES_SECTION;
	private final SlotsSection SLOTS_SECTION;
	private ItemStackTileEntityRuleFlowData data;

	public ItemStackTileEntityRuleFlowComponent(
		ManagerFlowController controller, ItemStackTileEntityRuleFlowData data
	) {
		super(data.getPosition(), new Size(215, 170));
		this.CONTROLLER = controller;
		this.data = data;

		TOOLBAR_SECTION = new ToolbarSection(this, new Position(0, 5));
		addChild(TOOLBAR_SECTION);

		ICON_SECTION = new IconSection(this, new Position(5, 25));
		addChild(ICON_SECTION);

		FILTER_SECTION = new FilterSection(this, new Position(45, 25));
		addChild(FILTER_SECTION);

		FACES_SECTION = new FacesSection(this, new Position(85, 25));
		addChild(FACES_SECTION);

		SLOTS_SECTION = new SlotsSection(this, new Position(125, 25));
		addChild(SLOTS_SECTION);

		ITEMS_SECTION = new ItemsSection(this, new Position(5, 73));
		addChild(ITEMS_SECTION);

		TILES_SECTION = new TilesSection(this, new Position(110, 73));
		addChild(TILES_SECTION);

		// Add change listener
		CONTROLLER.SCREEN.getFlowDataContainer().addObserver(new FlowDataHolderObserver<>(
			this,
			ItemStackTileEntityRuleFlowData.class
		));

		setDraggable(true);
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
			getSize().getHeight()
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

		super.draw(screen, matrixStack, mx, my, deltaTime);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers, int mx, int my) {
		// When E is pressed, close window if mouse is hovering
		if (isVisible()
			&& isHovering()
			&& CONTROLLER.SCREEN.getMinecraft().gameSettings.keyBindInventory
			.matchesKey(keyCode, scanCode)
		) {
			setVisible(false);
			setEnabled(false);
			return true;
		}
		return false;
	}

	@Override
	public ItemStackTileEntityRuleFlowData getData() {
		return data;
	}

	@Override
	public void setData(ItemStackTileEntityRuleFlowData data) {
		this.data = data;
		getPosition().setXY(data.getPosition());
		ICON_SECTION.onDataChanged(data);
		FILTER_SECTION.onDataChanged(data);
		ITEMS_SECTION.onDataChanged(data);
		TILES_SECTION.onDataChanged(data);
		SLOTS_SECTION.onDataChanged(data);
	}

	@Override
	public void onDragFinished(int dx, int dy, int mx, int my) {
		data.position = getPosition();
		CONTROLLER.SCREEN.sendFlowDataToServer(data);
	}

	@Override
	public boolean isVisible() {
		return data.open;
	}

	@Override
	public void setVisible(boolean visible) {
		if (data.open != visible) {
			data.open = visible;
			CONTROLLER.SCREEN.sendFlowDataToServer(data);
		}
		if (!visible) {
			// when hiding self, hide all matchers as well
			CONTROLLER.getChildren().stream()
				.filter(FlowDataHolder.class::isInstance)
				.filter(c -> data.matcherIds.contains(((FlowDataHolder<?>) c).getData().getId()))
				.forEach(c -> c.setVisible(false));
		}
	}

	@Override
	public boolean isEnabled() {
		return isVisible();
	}

	@Override
	public void setEnabled(boolean enabled) {
		setVisible(enabled);
	}

	@Override
	public int getZIndex() {
		return super.getZIndex() + 100;
	}

}
