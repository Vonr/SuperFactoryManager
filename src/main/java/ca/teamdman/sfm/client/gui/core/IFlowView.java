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

	default boolean isInBounds(int mx, int my) {
		return false;
	}
}
