/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.core;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f;
import ca.teamdman.sfm.client.gui.flow.core.IFlowController;
import ca.teamdman.sfm.client.gui.flow.core.IFlowView;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.gui.screen.Screen;

public class DebugController implements IFlowController, IFlowView {

	public final ManagerFlowController CONTROLLER;

	public DebugController(ManagerFlowController CONTROLLER) {
		this.CONTROLLER = CONTROLLER;
	}

	@Override
	public IFlowView getView() {
		return this;
	}

	public void drawId(BaseScreen screen, MatrixStack matrixStack, UUID id, int x, int y) {
		String toDraw = id.toString();
		int width = screen.getFontRenderer().getStringWidth(toDraw) + 2;
		int yOffset = -25;
		screen.drawRect(matrixStack, x - 1, y + yOffset - 1, width, 11, Colour3f.WHITE);
		screen.drawString(matrixStack, toDraw, x, y + yOffset, 0x2222BB);
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		if (Screen.hasControlDown() && Screen.hasAltDown()) {
			Optional<FlowData> check =
				CONTROLLER.RELATIONSHIP_CONTROLLER.CONTROLLER.getElementUnderMouse(mx, my)
					.flatMap(IFlowController::getData);
			check.ifPresent(data -> drawId(screen, matrixStack, data.getId(), mx, my));
			if (!check.isPresent()) {
				CONTROLLER.RELATIONSHIP_CONTROLLER.getFlowRelationships()
					.filter(r -> r.isCloseTo(mx, my))
					.findFirst()
					.ifPresent(rel -> {
						drawId(screen, matrixStack, rel.data.getId(), mx, my);
						rel.draw(screen, matrixStack, Colour3f.HIGHLIGHT);
					});
			}
		}
	}
}
