package ca.teamdman.sfm.gui;


import static ca.teamdman.sfm.SFM.LOGGER;
import static ca.teamdman.sfm.gui.ManagerGui.*;
import static ca.teamdman.sfm.gui.PositionController.DragMode.*;

public class PositionController {
	private final ManagerGui GUI;
	private       int        dragOffsetX = 0;
	private       int        dragOffsetY = 0;
	private       Component  dragging    = null;
	private       DragMode   mode        = NONE;

	public PositionController(ManagerGui gui) {
		this.GUI = gui;
	}

	// Return false to pass through
	public boolean onMouseDown(int x, int y, int button, Component comp) {
		if (button != LEFT)
			return false;
		if (comp == null)
			return false;
		if (hasAltDown()) {
			mode = MOVE;
			dragging = comp;
			dragOffsetX = comp.getPosition().getX() - x;
			dragOffsetY = comp.getPosition().getY() - y;
			LOGGER.debug("Position controller began dragging. Mouse down terminated.");
			return true;
		} else if (hasControlDown()) {
			comp.copy(GUI).ifPresent(c -> {
				dragging = c;
				mode = COPY;
				LOGGER.debug("Position controller cloned component.");
			});
			dragOffsetX = comp.getPosition().getX() - x;
			dragOffsetY = comp.getPosition().getY() - y;
			LOGGER.debug("Position controller began copy. Mouse down terminated.");
			return true;
		} else {
			mode = NONE;
			return false;
		}
	}

	public boolean onDrag(int x, int y, int button) {
		if (dragging == null)
			return false;

		if (hasShiftDown()) {
			x -= (x + dragOffsetX) % 10;
			y -= (y + dragOffsetY) % 10;
		}

		dragging.getPosition().setXY(x + dragOffsetX, y + dragOffsetY);
		GUI.RELATIONSHIP_CONTROLLER.postComponentReposition(dragging);
		LOGGER.debug("Position controller drag terminated.");
		return true;
	}

	public boolean onMouseUp(int x, int y, int button) {
		if (dragging != null) {
			dragging = null;
			LOGGER.debug("Position controller mouse up terminated.");
			return true;
		} else {
			return false;
		}
	}

	protected enum DragMode {
		NONE,
		MOVE,
		COPY
	}

}
