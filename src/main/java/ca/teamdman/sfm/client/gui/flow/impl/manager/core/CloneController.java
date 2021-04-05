/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.core;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.util.ButtonBackground;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.PositionHolder;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screen.Screen;

public class CloneController extends FlowComponent {

	public final ManagerFlowController CONTROLLER;
	private FlowComponent cloning = null;
	private BasicFlowDataContainer container = null;

	public CloneController(ManagerFlowController CONTROLLER) {
		this.CONTROLLER = CONTROLLER;
	}

	/**
	 * Start cloning a component
	 * @param comp
	 * @param container container to lookup component dependencies
	 * @return true if successfully started cloning
	 */
	public boolean startCloning(
		FlowComponent comp,
		BasicFlowDataContainer container
	) {
		if (comp instanceof FlowDataHolder && ((FlowDataHolder<?>) comp).isCloneable()) {
			cloning = comp;
			this.container = container;
			return true;
		}
		return false;
	}

	@Override
	public boolean mousePressed(int mx, int my, int button) {
		if (!Screen.hasControlDown()) {
			return false;
		}
		return CONTROLLER.getElementsUnderMouse(mx, my)
			.map(hit -> startCloning(hit, CONTROLLER.SCREEN.getFlowDataContainer()))
			.findFirst()
			.orElse(false);
	}

	@Override
	public boolean mouseReleased(int mx, int my, int button) {
		if (cloning != null) {
			List<FlowData> newData = new ArrayList<>();
			FlowData data = ((FlowDataHolder<?>) cloning).getData().duplicate(
				container,
				newData::add
			);
			newData.add(data);
			if (data instanceof PositionHolder) {
				((PositionHolder) data).getPosition().setXY(mx, my);
			}
			CONTROLLER.SCREEN.sendFlowDataToServer(newData);
			cloning = null;
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseDragged(int mx, int my, int button, int dmx, int dmy) {
		return cloning != null;
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		if (cloning == null) {
			return;
		}
		cloning.drawGhost(screen, matrixStack, mx, my, deltaTime);
	}

	/**
	 * Draws a blank background button at the position. Unused because looked tacky.
	 */
	private void drawGhostIndicator(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		ButtonBackground background = ButtonBackground.NORMAL;
		BaseScreen.bindTexture(background.SPRITE_SHEET);
		screen.drawTextureWithRGBA(
			matrixStack,
			mx,
			my,
			background.LEFT,
			background.TOP,
			background.WIDTH,
			background.HEIGHT,
			1f, 1f, 1f, 0.5f
		);
	}

	@Override
	public int getZIndex() {
		return super.getZIndex() + 300;
	}
}
