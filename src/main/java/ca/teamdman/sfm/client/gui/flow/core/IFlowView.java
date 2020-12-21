/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.core;

import com.mojang.blaze3d.matrix.MatrixStack;

public interface IFlowView {

	IFlowView NO_VIEW = new IFlowView() {
	};

	/**
	 * Fired each render tick
	 *
	 * @param mx        Scaled mouse x coordinate
	 * @param my        Scaled mouse y coordinate
	 * @param deltaTime Time elapsed since last draw
	 */
	default void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my,
		float deltaTime
	) {
	}

	/**
	 * Used to determine order of rendering. Lower number means rendered earlier, i.e., on bottom
	 * Default layer is 0
	 *
	 * @return Render layer
	 */
	default int getZIndex() {
		return 0;
	}

	/**
	 * Fired each render tick
	 *
	 * @param mx        Scaled mouse x coordinate
	 * @param my        Scaled mouse y coordinate
	 * @param deltaTime Time elapsed since last draw
	 */
	default void drawGhost(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {

	}
}
