/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager;

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
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.FlowDataContainer.ChangeType;
import ca.teamdman.sfm.common.flow.data.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.impl.TileEntityRuleFlowData;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.patch.ManagerPositionPacketC2S;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Queue;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

public class FlowTileEntityRule extends FlowContainer implements FlowDataHolder {

	private final ManagerFlowController CONTROLLER;
	private final TileEntityRuleFlowData DATA;
	private String name = "Tile Entity Rule";

	public FlowTileEntityRule(
		ManagerFlowController controller, TileEntityRuleFlowData data
	) {
		super(data.getPosition(), new Size(200, 200));
		this.CONTROLLER = controller;
		this.DATA = data;

		addChild(new MinimizeButton(
			new Position(180, 5),
			new Size(10, 10)
		));

		addChild(new SectionHeader(
			new Position(5, 25),
			new Size(35, 10),
			I18n.format("gui.sfm.manager.tile_entity_rule.icon.title")
		));

		addChild(new FlowIconItemStack(
			DATA.icon,
			new Position(5, 40)
		));

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
		drawBackground(screen, matrixStack);
		screen.drawBorder(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getWidth(),
			2,
			CONST.PANEL_BORDER
		);

		screen.drawString(
			matrixStack,
			"Tile Entity Rule",
			getPosition().getX() + 5,
			getPosition().getY() + 5,
			CONST.TEXT_PRIMARY.toInt()
		);
		super.draw(screen, matrixStack, mx, my, deltaTime);
	}

	@Override
	public FlowData getData() {
		return DATA;
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
			DATA.getId(),
			this.getPosition()
		));
	}

	@Override
	public void onDataChanged() {
		getPosition().setXY(DATA.getPosition());
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
			CONTROLLER.SCREEN.notifyChanged(DATA.getId(), ChangeType.UPDATED);
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
				getSize().getWidth(),
				CONST.SCREEN_BACKGROUND
			);
			screen.drawBorder(
				matrixStack,
				getPosition().getX(),
				getPosition().getY(),
				getSize().getWidth(),
				getSize().getWidth(),
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
	}

	public class SectionHeader extends FlowComponent {

		private final String CONTENT;

		public SectionHeader(Position pos, Size size, String CONTENT) {
			super(pos, size);
			this.CONTENT = CONTENT;
			setEnabled(false);
			setBackgroundColour(CONST.PANEL_BACKGROUND_DARK);
		}

		@Override
		public void draw(
			BaseScreen screen,
			MatrixStack matrixStack,
			int mx,
			int my,
			float deltaTime
		) {
			super.draw(screen, matrixStack, mx, my, deltaTime);
			screen.drawString(
				matrixStack,
				CONTENT,
				getPosition().getX() + 2,
				getPosition().getY() + 2,
				CONST.TEXT_PRIMARY.toInt()
			);
		}
	}
}
