/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.config;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowBackground;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowToggleBox;
import ca.teamdman.sfm.common.config.Config.Client;
import ca.teamdman.sfm.common.config.ConfigHelper;
import ca.teamdman.sfm.common.config.ConfigHolder;
import ca.teamdman.sfm.common.flow.data.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.resources.I18n;

public class ConfigComponent extends FlowContainer {

	BaseScreen SCREEN;

	public ConfigComponent(BaseScreen screen) {
		super(new Position(0, 0), new Size(512, 256));
		this.SCREEN = screen;
		addChild(new FlowBackground());
		setDraggable(false);

		addChild(new FlowHeader(
			new Position(10, 10),
			new Size(0, 0),
			I18n.format("gui.sfm.config.title")
		));

		int row = 20;
		int rowHeight = 25;
		row += rowHeight;
		addChild(new FlowPanel(10, row, 300, 14));
		addChild(new FlowLabel(
			new Position(30, row + 4),
			new Size(0, 0),
			I18n.format("gui.sfm.config.allowMultipleRuleWindows")
		));
		addChild(new FlowToggleBox(
			new Position(12, row + 2),
			new Size(10, 10),
			Client.allowMultipleRuleWindows
		) {
			@Override
			public void onChecked(boolean checked) {
				ConfigHelper.setValueAndSave(
					ConfigHelper.clientConfig,
					ConfigHolder.CLIENT.allowMultipleRuleWindows,
					checked
				);
			}
		});

		row += rowHeight;
		addChild(new FlowPanel(10, row, 300, 14));
		addChild(new FlowLabel(
			new Position(30, row + 4),
			new Size(0, 0),
			I18n.format("gui.sfm.config.showRuleDrawerLabels")
		));
		addChild(new FlowToggleBox(
			new Position(12, row + 2),
			new Size(10, 10),
			Client.showRuleDrawerLabels
		) {
			@Override
			public void onChecked(boolean checked) {
				ConfigHelper.setValueAndSave(
					ConfigHelper.clientConfig,
					ConfigHolder.CLIENT.showRuleDrawerLabels,
					checked
				);
			}
		});

		row += rowHeight;
		addChild(new FlowPanel(10, row, 300, 14));
		addChild(new FlowLabel(
			new Position(30, row + 4),
			new Size(0, 0),
			I18n.format("gui.sfm.config.alwaysSnapMovementToGrid")
		));
		addChild(new FlowToggleBox(
			new Position(12, row + 2),
			new Size(10, 10),
			Client.alwaysSnapMovementToGrid
		) {
			@Override
			public void onChecked(boolean checked) {
				ConfigHelper.setValueAndSave(
					ConfigHelper.clientConfig,
					ConfigHolder.CLIENT.alwaysSnapMovementToGrid,
					checked
				);
			}
		});

		row += rowHeight;
		addChild(new FlowPanel(10, row, 300, 14));
		addChild(new FlowLabel(
			new Position(30, row + 4),
			new Size(0, 0),
			I18n.format("gui.sfm.config.allowElementsOutOfBounds")
		));
		addChild(new FlowToggleBox(
			new Position(12, row + 2),
			new Size(10, 10),
			Client.allowElementsOutOfBounds
		) {
			@Override
			public void onChecked(boolean checked) {
				ConfigHelper.setValueAndSave(
					ConfigHelper.clientConfig,
					ConfigHolder.CLIENT.allowElementsOutOfBounds,
					checked
				);
			}
		});

		row += rowHeight;
		addChild(new FlowPanel(10, row, 300, 14));
		addChild(new FlowLabel(
			new Position(30, row + 4),
			new Size(0, 0),
			I18n.format("gui.sfm.config.enableRegexSearch")
		));
		addChild(new FlowToggleBox(
			new Position(12, row + 2),
			new Size(10, 10),
			Client.enableRegexSearch
		) {
			@Override
			public void onChecked(boolean checked) {
				ConfigHelper.setValueAndSave(
					ConfigHelper.clientConfig,
					ConfigHolder.CLIENT.enableRegexSearch,
					checked
				);
			}
		});
	}

	private static class FlowLabel extends FlowComponent {

		protected Colour3f TEXT_COLOUR = CONST.TEXT_LIGHT;
		private String content;

		public FlowLabel(Position pos, Size size, String content) {
			super(pos, size);
			this.content = content;
		}

		@Override
		public void draw(
			BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
		) {
			screen.drawString(
				matrixStack,
				content,
				getPosition().getX(),
				getPosition().getY(),
				TEXT_COLOUR
			);
		}
	}

	private static class FlowHeader extends FlowLabel {

		public FlowHeader(Position pos, Size size, String content) {
			super(pos, size, content);
			TEXT_COLOUR = new Colour3f(0.2f, 0.2f, 0.2f);
		}

		@Override
		public void draw(
			BaseScreen screen,
			MatrixStack matrixStack,
			int mx,
			int my,
			float deltaTime
		) {
			matrixStack.push();
			matrixStack.scale(2, 2, 1);
			super.draw(screen, matrixStack, mx, my, deltaTime);
			matrixStack.pop();
		}
	}

	private static class FlowPanel extends FlowComponent {

		public FlowPanel(int x, int y, int width, int height) {
			super(x, y, width, height);
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
				CONST.PANEL_BACKGROUND_NORMAL
			);
		}
	}
}
