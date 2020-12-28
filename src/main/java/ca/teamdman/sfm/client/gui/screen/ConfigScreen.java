/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.impl.config.ConfigComponent;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.ITextComponent;

public class ConfigScreen extends BaseScreen {

	public ConfigComponent CONTROLLER;

	public ConfigScreen(
		ITextComponent titleIn,
		int scaledWidth,
		int scaledHeight
	) {
		super(titleIn, scaledWidth, scaledHeight);
		CONTROLLER = new ConfigComponent(scaledWidth, scaledHeight);
	}

	@Override
	public boolean mouseClickedScaled(int mx, int my, int button) {
		return CONTROLLER.mousePressed(mx, my, button);
	}

	@Override
	public boolean keyPressedScaled(int keyCode, int scanCode, int modifiers, int mx, int my) {
		return CONTROLLER.keyPressed(keyCode, scanCode, modifiers, mx, my);
	}

	@Override
	public boolean keyReleasedScaled(int keyCode, int scanCode, int modifiers, int mx, int my) {
		return CONTROLLER.keyReleased(keyCode, scanCode, modifiers, mx, my);
	}

	@Override
	public boolean mouseReleasedScaled(int mx, int my, int button) {
		return CONTROLLER.mouseReleased(mx, my, button);
	}

	@Override
	public boolean onMouseDraggedScaled(int mx, int my, int button, int dmx, int dmy) {
		return CONTROLLER.mouseDragged(mx, my, button, dmx, dmy);
	}

	@Override
	public boolean mouseScrolledScaled(int mx, int my, double scroll) {
		return CONTROLLER.mouseScrolled(mx, my, scroll);
	}
	@Override
	public void drawScaled(
		MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks
	) {
		CONTROLLER.draw(this, matrixStack, mouseX, mouseY, partialTicks);
	}
}
