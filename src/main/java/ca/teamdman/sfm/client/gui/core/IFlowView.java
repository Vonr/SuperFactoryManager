package ca.teamdman.sfm.client.gui.core;

public interface IFlowView {
	/**
	 * Fired each render tick
	 * @param mx Scaled mouse x coordinate
	 * @param my Scaled mouse y coordinate
	 * @param deltaTime Time elapsed since last draw
	 */
	void draw(BaseScreen screen, int mx, int my, float deltaTime);
}
