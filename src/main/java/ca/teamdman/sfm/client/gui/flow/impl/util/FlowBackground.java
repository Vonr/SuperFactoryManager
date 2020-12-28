/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.common.flow.data.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.ResourceLocation;

public class FlowBackground extends FlowComponent {

	private static final ResourceLocation BACKGROUND_LEFT = new ResourceLocation(
		SFM.MOD_ID,
		"textures/gui/background_1.png"
	);
	private static final ResourceLocation BACKGROUND_RIGHT = new ResourceLocation(
		SFM.MOD_ID,
		"textures/gui/background_2.png"
	);

	public FlowBackground() {
		super(new Position(0,0), new Size(512, 256));
		setEnabled(false); // disable mouse events, no dragging
	}

	@Override
	public void drawBackground(BaseScreen screen, MatrixStack matrixStack) {
		screen.clearRect(matrixStack, 5,5,512-10,256-10);
		BaseScreen.bindTexture(BACKGROUND_LEFT);
		screen.drawTexture(matrixStack, 0, 0, 0, 0, 256, 256);
		BaseScreen.bindTexture(BACKGROUND_RIGHT);
		screen.drawTexture(matrixStack, 256, 0, 0, 0, 256, 256);
	}

	@Override
	public int getZIndex() {
		return super.getZIndex() - 2500;
	}
}
