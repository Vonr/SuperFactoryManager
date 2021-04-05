/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.core;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.RelationshipFlowData;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;

public class DebugController extends FlowComponent {

	public final ManagerFlowController CONTROLLER;

	public DebugController(ManagerFlowController CONTROLLER) {
		this.CONTROLLER = CONTROLLER;
	}

	@Override
	public void drawTooltip(
		BaseScreen screen,
		MatrixStack matrixStack,
		int mx,
		int my,
		float deltaTime
	) {
		if (Screen.hasControlDown() && Screen.hasAltDown()) {
			Optional<String> elem = CONTROLLER.getElementsUnderMouse(mx, my)
				.filter(FlowDataHolder.class::isInstance)
				.map(FlowDataHolder.class::cast)
				.map(FlowDataHolder::getData)
				.sorted(Comparator.comparingInt(data -> data instanceof RelationshipFlowData ? 1 : 0))
				.map(FlowData::getId)
				.map(UUID::toString)
				.findFirst();
			if (elem.isPresent()) {
				drawDebugInfo(screen, matrixStack, elem.get(), mx, my);
			} else {
				int count = CONTROLLER.SCREEN.getFlowDataContainer().size();
				String info = I18n.format("gui.sfm.flow.tooltip.debug_data_count", count);
				drawDebugInfo(screen, matrixStack, info, mx, my);
			}
		}
	}


	public void drawDebugInfo(BaseScreen screen, MatrixStack matrixStack, String id, int x, int y) {
		String toDraw = id.toString();
		int width = screen.getFontRenderer().getStringWidth(toDraw) + 2;
		int xOffset = 13;
		int yOffset = 0;
		screen.clearRect(matrixStack, x-1 + xOffset, y+yOffset-1, width, 11);
		screen.drawRect(matrixStack, x - 1 + xOffset, y + yOffset - 1, width, 11, CONST.WHITE);
		screen.drawString(matrixStack, toDraw, x + xOffset, y + yOffset, CONST.TEXT_DEBUG);
	}

	@Override
	public int getZIndex() {
		return super.getZIndex() + 600;
	}
}
