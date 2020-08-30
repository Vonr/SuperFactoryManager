package ca.teamdman.sfm.client.gui.flow.core;

import com.mojang.blaze3d.matrix.MatrixStack;

public interface IFlowCloneable {

	/**
	 * Fired each render tick by the clone manager when creating a ghost
	 */
	void drawGhostAtPosition(
		BaseScreen screen, MatrixStack matrixStack, int x, int y,
		float deltaTime
	);

	/**
	 * Fired when the clone should be committed, i.e., send packet to create new flow data here.
	 * !! It is expected that the clone will have a new UUID for the data. !!
	 *
	 * @param x X location for cloned element
	 * @param y Y location for cloned element
	 */
	void cloneWithPosition(int x, int y);

}
