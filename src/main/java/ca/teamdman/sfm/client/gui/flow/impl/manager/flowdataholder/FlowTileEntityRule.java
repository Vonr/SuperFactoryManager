/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.SearchUtil;
import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowItemStackPicker;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowMinusButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowRadioButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowRadioButton.RadioGroup;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.TileEntityItemStackRuleFlowData;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange.ChangeType;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.patch.ManagerPositionPacketC2S;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Queue;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public class FlowTileEntityRule extends FlowContainer implements FlowDataHolder<TileEntityItemStackRuleFlowData> {

	private final ManagerFlowController CONTROLLER;
	private TileEntityItemStackRuleFlowData data;
	private String name = "Tile Entity Rule";

	public FlowTileEntityRule(
		ManagerFlowController controller, TileEntityItemStackRuleFlowData data
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

		addChild(new FlowIconItemStack(
			this.data.icon,
			new Position(5, 40)
		));

		// Items
		addChild(new SectionHeader(
			new Position(5, 70),
			new Size(35, 12),
			I18n.format("gui.sfm.manager.tile_entity_rule.items.title")
		));

		// White/blacklist
		RadioGroup itemSelectionModeGroup = new RadioGroup();
		addChild(new FlowRadioButton(
			new Position(5,85),
			new Size(35,12),
			I18n.format("gui.sfm.flow.tileentityrule.button.whitelist"),
			itemSelectionModeGroup
		));

		addChild(new FlowRadioButton(
			new Position(45,85),
			new Size(35,12),
			I18n.format("gui.sfm.flow.tileentityrule.button.blacklist"),
			itemSelectionModeGroup
		));

		// Item drawer

		setVisible(false);
		setEnabled(false);
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
			"Tile Entity Rule",
			getPosition().getX() + 5,
			getPosition().getY() + 5,
			CONST.TEXT_LIGHT
		);

		super.draw(screen, matrixStack, mx, my, deltaTime);
	}

	@Override
	public TileEntityItemStackRuleFlowData getData() {
		return data;
	}

	@Override
	public void setData(TileEntityItemStackRuleFlowData data) {
		this.data = data;
		getPosition().setXY(data.getPosition());
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
			FlowTileEntityRule.this.setVisible(false);
			FlowTileEntityRule.this.setEnabled(false);
			CONTROLLER.SCREEN.getFlowDataContainer().notifyObservers(new FlowDataContainerChange(
				data,
				ChangeType.UPDATED
			));
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

	public class FlowIconItemStack extends FlowItemStackPicker {

		public FlowIconItemStack(
			ItemStack stack,
			Position pos
		) {
			super(stack, pos);
		}

		@Override
		public void onClicked(int mx, int my, int button) {
			super.onClicked(mx, my, button);
			new SearchUtil.Query("stone") {
				@Override
				public void onResultsUpdated(Queue<ItemStack> results) {
					SFM.LOGGER.info(
						SFMUtil.getMarker(FlowIconItemStack.this.getClass()),
						"Results updated with {} entries:", results.size()
					);
					results.forEach(stack -> SFM.LOGGER.info(
						SFMUtil.getMarker(FlowIconItemStack.this.getClass()),
						stack
					));
				}
			}.start();
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
