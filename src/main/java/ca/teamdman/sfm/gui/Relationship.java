package ca.teamdman.sfm.gui;

import javafx.util.Pair;

import java.util.*;

public class Relationship {
	public final List<Line> LINE_LIST = new ArrayList<>();
	public final Component  TAIL, HEAD;

	public Relationship(Component child, Component parent) {
		this.TAIL = child;
		this.HEAD = parent;
		Point a = new Point(
				TAIL.getXCentered(),
				HEAD.getYCentered() - (HEAD.getYCentered() - TAIL.getYCentered()) / 2
		);
		Point b = new Point(
				HEAD.getXCentered(),
				HEAD.getYCentered() - (HEAD.getYCentered() - TAIL.getYCentered()) / 2
		);

		Line first = new VLine(this, TAIL.getCenteredPosition(), a),
				second = new HLine(this, a, b),
				third = new VLine(this, b, HEAD.snapToEdge(b));

		first.setPrev(TAIL);
		first.setNext(second);
		second.setPrev(first);
		second.setNext(third);
		third.setPrev(second);
		third.setNext(HEAD);

		LINE_LIST.addAll(Arrays.asList(first, second, third));
	}

	public void addLine(Line l) {
		LINE_LIST.add(l);
	}

	public void removeLine(Line l) {
		LINE_LIST.remove(l);
	}

	public void addLines(Collection<Line> l) {
		LINE_LIST.addAll(l);
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Relationship
				&& ((Relationship) obj).TAIL == TAIL
				&& ((Relationship) obj).HEAD == HEAD;
	}

	public Optional<Line> getFirst() {
		return LINE_LIST.stream()
				.filter(line -> line.getPrev().equals(TAIL))
				.findFirst();
	}

	public Optional<Line> getLast() {
		return LINE_LIST.stream()
				.filter(line -> line.getNext().equals(HEAD))
				.findFirst();
	}

	public Pair<Line, Double> getNearestLineDistance(int x, int y) {
		return LINE_LIST.stream()
				.map(line -> new Pair<>(line, line.getDistance(x, y)))
				.min(Comparator.comparingDouble(Pair::getValue)).orElse(new Pair<>(null, Double.NaN));
	}

	/**
	 * Ensure that the lines are properly positioned after repositioning the TAIL of the relationship.
	 */
	public void postTailReposition() {
		getFirst().ifPresent(line -> {
			line.TAIL.setXY(TAIL.snapToEdge(line.TAIL));
			if (line instanceof VLine) {
				line.HEAD.setX(line.TAIL.getX());
			} else if (line instanceof HLine) {
				line.HEAD.setY(line.TAIL.getY());
			}
			line.ensureHeadConnection();
			line.pruneIfRedundant();
		});
		getLast().ifPresent(Line::pruneIfRedundant);
	}

	/**
	 * Ensure that the lines are properly positioned after repositioning the HEAD of the relationship.
	 */
	public void postHeadReposition() {
		getLast().ifPresent(line -> {
			line.HEAD.setXY(HEAD.snapToEdge(line.HEAD));
			if (line instanceof VLine) {
				line.TAIL.setX(line.HEAD.getX());
			} else if (line instanceof HLine) {
				line.TAIL.setY(line.HEAD.getY());
			}
			line.ensureTailConnection();
			line.pruneIfRedundant();
		});
		getFirst().ifPresent(Line::pruneIfRedundant);
	}

	/**
	 * Ensure that connections to the HEAD and TAIL are using the closest available edge.
	 */
	public void cleanupLines() {
		getFirst().ifPresent(line -> {
			line.TAIL.setXY(line.getPrev().snapToEdge(line.HEAD));
		});
		getLast().ifPresent(line -> {
			line.HEAD.setXY(line.getNext().snapToEdge(line.TAIL));
		});
	}

	/**
	 * @return New relationship with the direction reversed
	 */
	public Relationship inverse() {
		return new Relationship(HEAD, TAIL);
	}
}
