package ca.teamdman.sfm.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import static ca.teamdman.sfm.gui.ManagerGui.LEFT;
import static net.minecraft.client.gui.screen.Screen.hasShiftDown;

public class HierarchyController {
	private final ManagerGui                                           GUI;
	private final HashMap<Component, HashMap<Component, Relationship>> HIERARCHY         = new HashMap<>();
	private final ArrayList<Relationship>                              RELATIONSHIP_LIST = new ArrayList<>();
	private       Component                                            start             = null;

	public HierarchyController(ManagerGui GUI) {
		this.GUI = GUI;
	}


	// Return false to pass through
	public boolean onMouseDown(int x, int y, int button, Component comp) {
		if (button != LEFT)
			return false;
		if (comp == null)
			return false;
		if (!hasShiftDown())
			return false;
		start = comp;
		return true;
	}

	public boolean onDrag(int x, int y, int button) {
		if (start == null)
			return false;
		if (!hasShiftDown()) {
			start = null;
			return false;
		}
		return true;
	}

	public boolean onMouseUp(int x, int y, int button) {
		if (start == null)
			return false;
		if (!hasShiftDown())
			return false;
		for (Command c : GUI.COMMAND_CONTROLLER.getCommands()) {
			if (c != start && c.isInBounds(x, y)) {
				addRelationship(new Relationship(start, c));
				start = null;
				return true;
			}
		}
		return false;
	}


	public void addRelationship(Relationship r) {
		HIERARCHY.computeIfAbsent(r.PARENT, (__) -> new HashMap<>()).put(r.CHILD, r);
		HIERARCHY.computeIfAbsent(r.CHILD, (__) -> new HashMap<>()).put(r.PARENT, r);
		RELATIONSHIP_LIST.add(r);
	}

	public Optional<Relationship> getRelationship(Component a, Component b) {
		return Optional.ofNullable(HIERARCHY.getOrDefault(a, new HashMap<>()).get(b));
	}

	public void draw(int x, int y) {
		RELATIONSHIP_LIST.forEach(r -> {
			GUI.drawArrow(r.PARENT.getX() + r.PARENT.width/2,
					r.PARENT.getY() + r.PARENT.height/2,
					r.CHILD.getX() + r.CHILD.width/2,
					r.CHILD.getY() + r.CHILD.height/2);
		});
		if (start != null)
			if (hasShiftDown())
				GUI.drawArrow(start.getX() + start.width/2, start.getY() + start.height/2, x, y);
			else
				start = null;
	}

	public static class Relationship {
		final Component PARENT, CHILD;

		public Relationship(Component parent, Component child) {
			this.PARENT = parent;
			this.CHILD = child;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof Relationship
					&& ((Relationship) obj).PARENT == PARENT
					&& ((Relationship) obj).CHILD == CHILD;
		}
	}
}
