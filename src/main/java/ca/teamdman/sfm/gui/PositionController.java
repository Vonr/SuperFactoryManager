package ca.teamdman.sfm.gui;


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
		} else if (hasControlDown()) {
			mode = COPY;
			dragging = comp.copy(GUI);
		} else {
			mode = NONE;
		}

		if (mode != NONE) {
			dragOffsetX = comp.getX() - x;
			dragOffsetY = comp.getY() - y;
		}

		return mode != NONE;
	}

	public boolean onDrag(int x, int y, int button) {
		if (dragging == null)
			return false;

		if (hasShiftDown()) {
			x -= (x + dragOffsetX) % 10;
			y -= (y + dragOffsetY) % 10;
		}

		dragging.setXY(x + dragOffsetX, y+dragOffsetY);
		GUI.FLOW_CONTROLLER.reflow(dragging);

		return true;
	}

	public boolean onMouseUp(int x, int y, int button) {
		if (dragging != null) {
			dragging = null;
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
