/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowItemStack;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowItemStackPicker;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowMinusButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowRadioButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowRadioButton.RadioGroup;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData.FilterMode;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.patch.ManagerPositionPacketC2S;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public class ItemStackTileEntityRuleFlowComponent extends FlowContainer implements
	FlowDataHolder<ItemStackTileEntityRuleFlowData> {

	private final FlowItemStackPicker ICON_PICKER;
	private final ManagerFlowController CONTROLLER;
	private final FlowItemStack ICON;
	private final FlowRadioButton WHITELIST_BUTTON;
	private final FlowRadioButton BLACKLIST_BUTTON;
	private final RadioGroup ITEM_SELECTION_MODE_GROUP;
	private ItemStackTileEntityRuleFlowData data;
	private String name = "Tile Entity Rule";

	public ItemStackTileEntityRuleFlowComponent(
		ManagerFlowController controller, ItemStackTileEntityRuleFlowData data
	) {
		super(data.getPosition(), new Size(200, 200));
		this.CONTROLLER = controller;
		this.data = data;

		addChild(new MinimizeButton(
			new Position(180, 5),
			new Size(10, 10)
		));

		// Icon
		addChild(new SectionHeader(
			new Position(5, 25),
			new Size(35, 12),
			I18n.format("gui.sfm.manager.tile_entity_rule.icon.title")
		));

		this.ICON = new FlowIconItemStack(this.data.icon, new Position(5, 40));
		addChild(ICON);

		this.ICON_PICKER = new FlowItemStackPicker(
			CONTROLLER,
			ICON.getPosition().withOffset(ICON.getSize().getWidth() + 5, 0)
		) {
			@Override
			public void onItemStackChanged(ItemStack stack) {
				data.icon = stack;
				CONTROLLER.SCREEN.sendFlowDataToServer(data);
			}
		};
		ICON_PICKER.setVisible(false);
		ICON_PICKER.setEnabled(false);
		addChild(ICON_PICKER);

		// Items
		addChild(new SectionHeader(
			new Position(5, 70),
			new Size(35, 12),
			I18n.format("gui.sfm.manager.tile_entity_rule.items.title")
		));

		this.ITEM_SELECTION_MODE_GROUP = new RadioGroup();
		// White/blacklist
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
		addChild(BLACKLIST_BUTTON);

		// Item drawer

		setVisible(false);
		setEnabled(false);
		CONTROLLER.SCREEN.getFlowDataContainer().addObserver(new FlowDataHolderObserver<>(
			this,
			ItemStackTileEntityRuleFlowData.class
		));
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
		ICON.setItemStack(data.icon);
		ITEM_SELECTION_MODE_GROUP.setSelected(
			data.filterMode == FilterMode.WHITELIST
				? WHITELIST_BUTTON
				: BLACKLIST_BUTTON
		);
	}

	@Override
	public int getZIndex() {
		return super.getZIndex() + 100;
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

	public class MinimizeButton extends FlowMinusButton {

		public MinimizeButton(
			Position pos,
			Size size
		) {
			super(pos, size, CONST.MINIMIZE);
		}

		@Override
		public void onClicked(int mx, int my, int button) {
			ItemStackTileEntityRuleFlowComponent.this.setVisible(false);
			ItemStackTileEntityRuleFlowComponent.this.setEnabled(false);
			CONTROLLER.SCREEN.getFlowDataContainer().notifyChanged(data);
		}

		@Override
		public void draw(
			BaseScreen screen,
			MatrixStack matrixStack,
			int mx,
			int my,
			float deltaTime
		) {
			screen.drawRect(
				matrixStack,
				getPosition().getX(),
				getPosition().getY(),
				getSize().getWidth(),
				getSize().getHeight(),
				CONST.SCREEN_BACKGROUND
			);
			screen.drawBorder(
				matrixStack,
				getPosition().getX(),
				getPosition().getY(),
				getSize().getWidth(),
				getSize().getHeight(),
				1,
				CONST.PANEL_BORDER
			);
			super.draw(screen, matrixStack, mx, my, deltaTime);
		}
	}

	public class FlowIconItemStack extends FlowItemStack {

		public FlowIconItemStack(
			ItemStack stack,
			Position pos
		) {
			super(stack, pos);
		}

		@Override
		public void onClicked(int mx, int my, int button) {
			ICON_PICKER.setVisible(!ICON_PICKER.isVisible());
			ICON_PICKER.setEnabled(ICON_PICKER.isVisible());
//			super.onClicked(mx, my, button);
//			new SearchUtil.Query("stone") {
//				@Override
//				public void onResultsUpdated(Queue<ItemStack> results) {
//					SFM.LOGGER.info(
//						SFMUtil.getMarker(FlowIconItemStack.this.getClass()),
//						"Results updated with {} entries:", results.size()
//					);
//					results.forEach(stack -> SFM.LOGGER.info(
//						SFMUtil.getMarker(FlowIconItemStack.this.getClass()),
//						stack
//					));
//				}
//			}.start();
		}

		@Override
		public void draw(
			BaseScreen screen,
			MatrixStack matrixStack,
			int mx,
			int my,
			float deltaTime
		) {
			drawBackground(screen, matrixStack, CONST.PANEL_BACKGROUND_LIGHT);
			super.draw(screen, matrixStack, mx, my, deltaTime);
		}
	}

	public class SectionHeader extends FlowComponent {

		private final String CONTENT;

		public SectionHeader(Position pos, Size size, String CONTENT) {
			super(pos, size);
			this.CONTENT = CONTENT;
			setEnabled(false);
		}

		@Override
		public void draw(
			BaseScreen screen,
			MatrixStack matrixStack,
			int mx,
			int my,
			float deltaTime
		) {
			screen.drawRect(
				matrixStack,
				getPosition().getX(),
				getPosition().getY(),
				getSize().getWidth(),
				getSize().getHeight(),
				CONST.PANEL_BACKGROUND_DARK
			);
			screen.drawCenteredString(
				matrixStack,
				CONTENT,
				this,
				1,
				CONST.TEXT_LIGHT
			);
		}
	}
}
