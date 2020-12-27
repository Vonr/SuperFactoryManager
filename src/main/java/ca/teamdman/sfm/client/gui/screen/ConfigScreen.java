/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.ITextComponent;

public class ConfigScreen extends BaseScreen {

	public ConfigScreen(
		ITextComponent titleIn,
		int scaledWidth,
		int scaledHeight
	) {
		super(titleIn, scaledWidth, scaledHeight);
	}

	@Override
	public void drawScaled(
		MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks
	) {
		drawRect(
			matrixStack,
			0,
			0,
			100,
			100,
			CONST.GREEN
		);
	}
}
