package ca.teamdman.sfm.common.container.core.controller;

import ca.teamdman.sfm.client.gui.ManagerScreen;
import ca.teamdman.sfm.common.container.CoreContainer;
import ca.teamdman.sfm.common.container.core.Relationship;
import ca.teamdman.sfm.common.container.core.component.CommandButton;
import ca.teamdman.sfm.common.container.core.component.Component;
import ca.teamdman.sfm.common.container.core.component.Line;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import javafx.util.Pair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ca.teamdman.sfm.client.gui.core.BaseScreen.DEFAULT_LINE_COLOUR;
import static ca.teamdman.sfm.client.gui.core.BaseScreen.HIGHLIGHTED_LINE_COLOUR;
import static net.minecraft.client.gui.screen.Screen.hasAltDown;
import static net.minecraft.client.gui.screen.Screen.hasShiftDown;


public class RelationshipController extends BaseController {
	private final Multimap<Component, Relationship>      RELATIONSHIP_MAP = HashMultimap.create();
	private       Pair<Relationship, Pair<Line, Double>> dragging         = null;
	private       Component                              start            = null;

	public RelationshipController(CoreContainer<?> container) {
		super(container);
	}

	public Optional<Component> getDragStart() {
		return Optional.ofNullable(this.start);
	}

	public void clearDragStart() {
		this.start = null;
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
				return true;
			}
			return false;
		} else if (hasShiftDown()) {
			start = comp;
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
		for (CommandButton c : CONTAINER.COMMAND_CONTROLLER.getCommands()) {
			if (c != start && c.isInBounds(x, y)) {
				addRelationship(new Relationship(start, c));
				start = null;
				return true;
			}
		}
		return false;
	}

	public void addRelationship(Relationship r) {
		if (RELATIONSHIP_MAP.containsValue(r))
			return;
		if (getAncestors(r.TAIL).contains(r.HEAD))
			return;

		RELATIONSHIP_MAP.put(r.HEAD, r);
		RELATIONSHIP_MAP.put(r.TAIL, r);
	}

	public Set<Component> getAncestors(Component c) {
		Set<Component> rtn = new HashSet<>();
		rtn.add(c);
		while (true) {
			Set<Component> ancestors = rtn.stream()
					.flatMap(x -> RELATIONSHIP_MAP.get(x).stream()
							.filter(r -> r.HEAD == x)
							.filter(r -> !rtn.contains(r.TAIL)))
					.map(r -> r.TAIL)
					.collect(Collectors.toSet());
			if (ancestors.isEmpty())
				break;
			rtn.addAll(ancestors);
		}
		rtn.remove(c);
		return rtn;
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

	public Multimap<Component, Relationship> getRelationships() {
		return this.RELATIONSHIP_MAP;
	}

	public Stream<Relationship> getUniqueRelationships() {
		return this.RELATIONSHIP_MAP.entries().stream()
				.filter(e -> e.getKey() == e.getValue().TAIL)
				.map(Map.Entry::getValue);
	}
}
