package ca.teamdman.sfm.client.gui.manager;

import ca.teamdman.sfm.client.gui.ManagerScreen;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import javafx.util.Pair;

import java.util.Comparator;
import java.util.Optional;

import static ca.teamdman.sfm.SFM.LOGGER;
import static ca.teamdman.sfm.client.gui.manager.BaseScreen.DEFAULT_LINE_COLOUR;
import static ca.teamdman.sfm.client.gui.manager.BaseScreen.HIGHLIGHTED_LINE_COLOUR;
import static net.minecraft.client.gui.screen.Screen.hasAltDown;
import static net.minecraft.client.gui.screen.Screen.hasShiftDown;

public class RelationshipController {
	private final ManagerScreen                          GUI;
	private final Multimap<Component, Relationship>      RELATIONSHIP_MAP = HashMultimap.create();
	private       Pair<Relationship, Pair<Line, Double>> dragging         = null;
	private       Component                              start            = null;

	public RelationshipController(ManagerScreen GUI) {
		this.GUI = GUI;
	}


	// Return false to pass through
	public boolean onMouseDown(int x, int y, int button, Component comp) {
		if (button != ManagerScreen.LEFT)
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

	/**
	 * Ensures that lines are visibly touching the component in all of its relationships.
	 */
	public void postComponentReposition(Component c) {
		for (Relationship r : RELATIONSHIP_MAP.get(c)) {
			if (r.TAIL == c) {
				r.postTailReposition();
			} else { // component is head
				r.postHeadReposition();
			}
			r.cleanupLines();
		}
	}
}
