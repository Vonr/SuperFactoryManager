package ca.teamdman.sfm.gui;

import javafx.util.Pair;

import javax.vecmath.Color4f;
import java.util.*;

import static ca.teamdman.sfm.gui.ManagerGui.LEFT;
import static net.minecraft.client.gui.screen.Screen.hasAltDown;
import static net.minecraft.client.gui.screen.Screen.hasShiftDown;

public class FlowController {
	private final ManagerGui                                           GUI;
	private final HashMap<Component, HashMap<Component, Relationship>> HIERARCHY         = new HashMap<>();
	private final ArrayList<Relationship>                              RELATIONSHIP_LIST = new ArrayList<>();
	private       Pair<Relationship, Pair<Line, Double>>               dragging          = null;
	private       Component                                            start             = null;

	public FlowController(ManagerGui GUI) {
		this.GUI = GUI;
	}


	// Return false to pass through
	public boolean onMouseDown(int x, int y, int button, Component comp) {
		if (button != LEFT)
			return false;
		if (comp == null) {
			dragging = null;
			getRelationship(x, y).ifPresent(r -> {
				if (r.getValue().getValue() > 5)
					return;
				dragging = r;
				r.getValue().getKey().color = new Color4f((float) Math.random(), (float) Math.random(), (float) Math.random(), 1);
			});
		} else if (hasShiftDown()) {
			start = comp;
		}
		return true;
	}

	public Optional<Pair<Relationship, Pair<Line, Double>>> getRelationship(int x, int y) {
		return RELATIONSHIP_LIST.stream()
				.map(r -> new Pair<>(r, r.getNearestLineDistance(x, y)))
				//				.filter(p -> p.getValue().getValue() < 5)
				.min(Comparator.comparingDouble(p -> p.getValue().getValue()));
	}

	public boolean onDrag(int x, int y, int button) {
		if (!hasShiftDown() && start != null) {
			start = null;
			return false;
		}

		if (hasAltDown() && dragging != null) {
			dragging.getValue().getKey().drag(x, y);
		} else if (!hasAltDown() && dragging != null) {
			dragging = null;
			return false;
		}
		return true;
	}

	public boolean onMouseUp(int x, int y, int button) {
		dragging = null;
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
		if (RELATIONSHIP_LIST.contains(r))
			return;
		if (RELATIONSHIP_LIST.contains(r.inverse()))
			return;
		HIERARCHY.computeIfAbsent(r.PARENT, (__) -> new HashMap<>()).put(r.CHILD, r);
		HIERARCHY.computeIfAbsent(r.CHILD, (__) -> new HashMap<>()).put(r.PARENT, r);
		RELATIONSHIP_LIST.add(r);
	}

	public Optional<Relationship> getRelationship(Component a, Component b) {
		if (HIERARCHY.containsKey(a))
			if (HIERARCHY.get(a).containsKey(b))
				return Optional.of(HIERARCHY.get(a).get(b));
		if (HIERARCHY.containsKey(b))
			if (HIERARCHY.get(b).containsKey(a))
				return Optional.of(HIERARCHY.get(b).get(a));
		return Optional.empty();
	}

	public void draw(int x, int y) {
		RELATIONSHIP_LIST.forEach(this::drawRelationship);
		if (start != null)
			if (hasShiftDown())
				GUI.drawArrow(start.getX() + start.width / 2, start.getY() + start.height / 2, x, y);
			else
				start = null;
	}

	public void drawRelationship(Relationship r) {
		Iterator<Line> iter = r.LINE_LIST.iterator();
		while (iter.hasNext()) {
			Line line = iter.next();
			if (iter.hasNext())
				GUI.drawLine(line);
			else
				GUI.drawArrow(line);
		}
	}

	public void reflow(Component c) {
		RELATIONSHIP_LIST.stream()
				.filter(r -> r.PARENT == c || r.CHILD == c)
				.forEach(r -> {
					if (r.PARENT == c) {
						Line line = r.LINE_LIST.get(0);
						line.HEAD.setXY(c.snapToEdge(line.TAIL));
						line.reflow(Line.Direction.FORWARDS);
					} else {
						Line line = r.LINE_LIST.get(r.LINE_LIST.size()-1);
						line.TAIL.setXY(c.snapToEdge(line.HEAD));
						line.reflow(Line.Direction.BACKWARDS);
					}
				});
	}

}
