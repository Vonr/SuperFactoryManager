package ca.teamdman.sfm.gui;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import javafx.util.Pair;

import java.util.Comparator;
import java.util.Optional;

import static ca.teamdman.sfm.SFM.LOGGER;
import static ca.teamdman.sfm.gui.BaseGui.DEFAULT_LINE_COLOUR;
import static ca.teamdman.sfm.gui.BaseGui.HIGHLIGHTED_LINE_COLOUR;
import static ca.teamdman.sfm.gui.ManagerGui.LEFT;
import static net.minecraft.client.gui.screen.Screen.hasAltDown;
import static net.minecraft.client.gui.screen.Screen.hasShiftDown;

public class RelationshipController {
	private final ManagerGui                             GUI;
	private final Multimap<Component, Relationship>      RELATIONSHIP_MAP = HashMultimap.create();
	private       Pair<Relationship, Pair<Line, Double>> dragging         = null;
	private       Component                              start            = null;

	public RelationshipController(ManagerGui GUI) {
		this.GUI = GUI;
	}


	// Return false to pass through
	public boolean onMouseDown(int x, int y, int button, Component comp) {
		if (button != LEFT)
			return false;
		if (comp == null) {
			dragging = null;
			getRelationship(x, y).ifPresent(r -> {
				if (r.getValue().getValue() > 5) // distance too far
					return;
				dragging = r;
				r.getValue().getKey().setColor(HIGHLIGHTED_LINE_COLOUR);
			});
			if (dragging != null) {
				LOGGER.debug("Relationship controller began line dragging. Mouse down terminated.");
				return true;
			}
			return false;
		} else if (hasShiftDown()) {
			start = comp;
			LOGGER.debug("Relationship controller began linking. Mouse down terminated.");
			return true;
		}
		return false;
	}

	public Optional<Pair<Relationship, Pair<Line, Double>>> getRelationship(int x, int y) {
		return RELATIONSHIP_MAP.values().stream()
				.map(r -> new Pair<>(r, r.getNearestLineDistance(x, y)))
				.min(Comparator.comparingDouble(p -> p.getValue().getValue()));
	}

	public boolean onDrag(int x, int y, int button) {
		if (!hasShiftDown() && start != null) {
			start = null;
			return false;
		}

		if (hasAltDown() && dragging != null) {
			dragging.getValue().getKey().drag(x, y);
			LOGGER.debug("Relationship controller dragged component. Mouse drag terminated.");
			return true;
		} else if (!hasAltDown() && dragging != null) {
			dragging = null;
		}
		return false;
	}

	public boolean onMouseUp(int x, int y, int button) {
		if (dragging != null) {
			dragging.getValue().getKey().setColor(DEFAULT_LINE_COLOUR);
			dragging = null;
		}

		if (start == null)
			return false;
		if (!hasShiftDown())
			return false;
		for (Command c : GUI.COMMAND_CONTROLLER.getCommands()) {
			if (c != start && c.isInBounds(x, y)) {
				addRelationship(new Relationship(start, c));
				start = null;
				LOGGER.debug("Relationship controller linked components. Mouse up terminated.");
				return true;
			}
		}
		return false;
	}

	public void addRelationship(Relationship r) {
		if (RELATIONSHIP_MAP.containsValue(r))
			return;
		RELATIONSHIP_MAP.put(r.HEAD, r);
		RELATIONSHIP_MAP.put(r.TAIL, r);
	}

	public void draw(int x, int y) {
		RELATIONSHIP_MAP.values().forEach(this::drawRelationship);
		if (start != null)
			if (hasShiftDown())
				GUI.drawArrow(start.getPosition().getX() + start.width / 2, start.getPosition().getY() + start.height / 2, x, y);
			else
				start = null;
	}

	public void drawRelationship(Relationship r) {
		for (Line line : r.LINE_LIST) {
			if (line.getNext() == r.HEAD) {
				GUI.drawArrow(line);
			} else {
				GUI.drawLine(line);
			}
		}
	}

	public void reflow(Component c) {
		for (Relationship r : RELATIONSHIP_MAP.get(c)) {
			if (r.TAIL == c) {
				r.getFirst().ifPresent(line -> {
					line.TAIL.setXY(c.snapToEdge(line.TAIL));
					if (line instanceof VLine) {
						line.HEAD.setX(line.TAIL.getX());
					} else if (line instanceof HLine) {
						line.HEAD.setY(line.TAIL.getY());
					}
					line.ensureHeadConnection();
					line.pruneIfRedundant();
				});
			} else {
				r.getLast().ifPresent(line -> {
					line.HEAD.setXY(c.snapToEdge(line.HEAD));
					if (line instanceof VLine) {
						line.TAIL.setX(line.HEAD.getX());
					} else if (line instanceof HLine) {
						line.TAIL.setY(line.HEAD.getY());
					}
					line.ensureTailConnection();
					line.pruneIfRedundant();
				});
			}
		}
	}

}
