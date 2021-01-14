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
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.patch.ManagerPositionPacketC2S;
import com.mojang.blaze3d.matrix.MatrixStack;

public class ItemStackTileEntityRuleFlowComponent extends FlowContainer implements
	FlowDataHolder<ItemStackTileEntityRuleFlowData> {

	protected final ManagerFlowController CONTROLLER;
	private final TilesSection TILES_SECTION;
	private final ItemsSection ITEMS_SECTION;
	private final IconSection ICON_SECTION;
	private final ToolbarSection TOOLBAR_SECTION;
	private ItemStackTileEntityRuleFlowData data;

	public ItemStackTileEntityRuleFlowComponent(
		ManagerFlowController controller, ItemStackTileEntityRuleFlowData data
	) {
		super(data.getPosition(), new Size(200, 200));
		this.CONTROLLER = controller;
		this.data = data;

		TOOLBAR_SECTION = new ToolbarSection(this, new Position(0, 5));
		addChild(TOOLBAR_SECTION);

		ICON_SECTION = new IconSection(this, new Position(5, 25));
		addChild(ICON_SECTION);

		TILES_SECTION = new TilesSection(this, new Position(50, 25));
		addChild(TILES_SECTION);

		ITEMS_SECTION = new ItemsSection(this, new Position(5, 70));
		addChild(ITEMS_SECTION);

		// Hide by default
		setVisible(false);
		setEnabled(false);

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
		ICON_SECTION.onDataChanged(data);
		ITEMS_SECTION.onDataChanged(data);
		TILES_SECTION.onDataChanged(data);
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
