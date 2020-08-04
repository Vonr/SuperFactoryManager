package ca.teamdman.sfm.client.gui.core;

import com.mojang.blaze3d.matrix.MatrixStack;

public interface IFlowView {
	IFlowView NO_VIEW = (screen, matrixStack, mx, my, deltaTime) -> {
	};
	/**
	 * Fired each render tick
	 * @param matrixStack
	 * @param mx Scaled mouse x coordinate
	 * @param my Scaled mouse y coordinate
	 * @param deltaTime Time elapsed since last draw
	 */
	void draw(BaseScreen screen, MatrixStack matrixStack, int mx, int my,
		float deltaTime);
}
