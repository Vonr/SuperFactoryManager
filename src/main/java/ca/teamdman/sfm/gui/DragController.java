package ca.teamdman.sfm.gui;


import static ca.teamdman.sfm.gui.DragController.DragMode.*;
import static ca.teamdman.sfm.gui.MouseButton.LEFT;
import static ca.teamdman.sfm.gui.ManagerGui.*;

public class DragController {
	protected enum DragMode {
		NONE,
		MOVE,
		COPY;
	}

	private final ManagerGui gui;
	private       DragMode   mode        = NONE;
	private       int        dragOffsetX = 0;
	private       int        dragOffsetY = 0;
	private       Component  dragging    = null;

	public DragController(ManagerGui gui) {
		this.gui = gui;
	}

	// Return false to pass through
	public boolean onMouseDown(int x, int y, int button, Component comp) {
		if (button != LEFT)
			return false;

		if (hasAltDown()) {
			mode = MOVE;
			dragging = comp;
		}
		else if (hasControlDown()) {
			mode = COPY;
			dragging = comp.copy(gui);
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
			x -= (x + dragOffsetX)%10;
			y -= (y + dragOffsetY)%10;
		}

		dragging.setX(x + dragOffsetX);
		dragging.setY(y + dragOffsetY);

		return true;
	}

	public boolean onMouseUp(int x, int y, int button) {
		dragging = null;
		return false;
	}

}
