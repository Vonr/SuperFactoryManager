/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.config;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowToggleBox;
import ca.teamdman.sfm.common.config.Config.Client;
import ca.teamdman.sfm.common.config.ConfigHelper;
import ca.teamdman.sfm.common.config.ConfigHolder;
import ca.teamdman.sfm.common.flow.data.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.resources.I18n;

public class ConfigComponent extends FlowContainer {

	public ConfigComponent(int width, int height) {
		super(0, 0, width, height);
		setDraggable(false);
		setBackgroundColour(CONST.SCREEN_BACKGROUND);
		Position pos = new Position(10, 10);
		Size size = new Size(0, 0);
		String content1 = I18n.format("gui.sfm.config.title");
		Colour3f colour = new Colour3f(0.2f, 0.2f, 0.2f);
		addChild(new FlowComponent() {
			private String content;
			private Colour3f textColour = CONST.TEXT_PRIMARY;
			private int scale = 2;

			@Override
			public void draw(
				BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
			) {
				matrixStack.push();
				matrixStack.scale(scale, scale, 1);
				screen.drawString(
					matrixStack,
					content,
					getPosition().getX(),
					getPosition().getY(),
					textColour.toInt()
				);
				matrixStack.pop();
			}
		});

		int row = 20;
		int rowHeight = 25;
		row += rowHeight;
		addChild(new FlowComponent(10, row, 300, 14));
		Position pos1 = new Position(30, row + 4);
		Size size1 = new Size(0, 0);
		String content11 = I18n.format("gui.sfm.config.allowMultipleRuleWindows");
		addChild(new FlowComponent(pos1, size1) {
			private String content = content11;
			private Colour3f textColour = CONST.TEXT_PRIMARY;
			private int scale = 1;

			{
				setDraggable(false);
			}

			@Override
			public void draw(
				BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
			) {
				matrixStack.push();
				matrixStack.scale(scale, scale, 1);
				screen.drawString(
					matrixStack,
					content,
					getPosition().getX(),
					getPosition().getY(),
					textColour.toInt()
				);
				matrixStack.pop();
			}
		});
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
		addChild(new FlowComponent(10, row, 300, 14));
		Position pos2 = new Position(30, row + 4);
		Size size2 = new Size(0, 0);
		String content12 = I18n.format("gui.sfm.config.showRuleDrawerLabels");
		addChild(new FlowComponent(pos2, size2) {
			private String content = content12;
			private Colour3f textColour = CONST.TEXT_PRIMARY;
			private int scale = 1;

			{
				setDraggable(false);
			}

			@Override
			public void draw(
				BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
			) {
				matrixStack.push();
				matrixStack.scale(scale, scale, 1);
				screen.drawString(
					matrixStack,
					content,
					getPosition().getX(),
					getPosition().getY(),
					textColour.toInt()
				);
				matrixStack.pop();
			}
		});
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
	}

	@Override
	public void draw(BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime) {
		super.drawBackground(screen, matrixStack);
		super.draw(screen, matrixStack, mx, my, deltaTime);
	}
}
