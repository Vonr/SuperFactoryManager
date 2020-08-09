package ca.teamdman.sfm.client.gui.core;

public interface IFlowController {
	/**
	 * Fired when a mouse button is pressed
	 *
	 * @param screen
	 * @param mx     Scaled mouse x coordinate
	 * @param my     Scaled mouse y coordinate
	 * @param button Mouse button pressed
	 * @return Consume event
	 */
	boolean mousePressed(BaseScreen screen, int mx, int my, int button);

	/**
	 * Fired when a mouse button is released
	 *
	 * @param screen
	 * @param mx     Scaled mouse x coordinate
	 * @param my     Scaled mouse y coordinate
	 * @param button Mouse button pressed
	 * @return Consume event
	 */
	boolean mouseReleased(BaseScreen screen, int mx, int my, int button);

	/**
	 * Fired when a mouse is dragged from one position to another
	 *
	 * @param screen
	 * @param mx     Drag begin scaled mouse x coordinate
	 * @param my     Drag begin scaled mouse y coordinate
	 * @param button Mouse button held during drag
	 * @param dmx    Drag ended scaled mouse x coordinate
	 * @param dmy    Drag ended scaled mouse y coordiante
	 * @return Consume event
	 */
	boolean mouseDragged(BaseScreen screen, int mx, int my, int button, int dmx, int dmy);

	/**
	 * Gets the view used to draw this controller
	 *
	 * @return view
	 */
	IFlowView getView();

	/**
	 * Called once the container has set all the values needed to set the controller defaults.
	 * Expected behaviour: overwrite values in controller with values from container.
	 */
	default void load(){};
}
