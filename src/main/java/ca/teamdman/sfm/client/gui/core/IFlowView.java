package ca.teamdman.sfm.client.gui.core;

import com.mojang.blaze3d.matrix.MatrixStack;

public interface IFlowView {

	IFlowView NO_VIEW = new IFlowView() {};

	/**
	 * Fired each render tick
	 *
	 * @param matrixStack
	 * @param mx          Scaled mouse x coordinate
	 * @param my          Scaled mouse y coordinate
	 * @param deltaTime   Time elapsed since last draw
	 */
	default void draw(BaseScreen screen, MatrixStack matrixStack, int mx, int my,
		float deltaTime) {
	}

	/**
	 * Used to determine order of rendering.
	 * Lower number means rendered earlier, i.e., on bottom
	 * Default layer is 0
	 * @return Render layer
	 */
	default int getZIndex() {
		return 0;
	}
}
