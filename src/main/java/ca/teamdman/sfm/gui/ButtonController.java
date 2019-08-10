package ca.teamdman.sfm.gui;

import java.util.ArrayList;

import static ca.teamdman.sfm.gui.MouseButton.LEFT;

public class ButtonController {
	private final ManagerGui        gui;
	private       Button            active = null;
	private       ArrayList<Button> buttons = new ArrayList<>();

	public ButtonController(ManagerGui gui) {
		this.gui = gui;
	}

	public Button addButton(Button b) {
		buttons.add(b);
		return b;
	}

	public boolean onMouseDown(int x, int y, int button, Component comp) {
		if (button != LEFT)
			return false;
		if (!(comp instanceof Button))
			return false;
		active = (Button) comp;
		active.setPressed(true);
		return true;
	}

	public boolean onDrag(int x, int y, int button) {
		if (button != LEFT)
			return false;
		if (active == null)
			return false;
		if (active.isInBounds(x, y) == active.isPressed())
			return false;
		active.setPressed(!active.isPressed());
		return true;
	}

	public boolean onMouseUp(int x, int y, int button) {
		if (active == null)
			return false;
		if (!active.isPressed())
			return false;
		if (!active.isInBounds(x, y))
			return false;
		active.click();
		active.setPressed(false);
		active = null;
		return true;
	}

}
