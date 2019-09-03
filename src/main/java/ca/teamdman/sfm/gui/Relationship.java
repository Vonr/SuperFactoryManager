package ca.teamdman.sfm.gui;

import javafx.util.Pair;

import java.util.*;

public class Relationship {
	public final List<Line> LINE_LIST = new ArrayList<>();
	public final Component  PARENT, CHILD;

	public Relationship(Component parent, Component child) {
		this.PARENT = parent;
		this.CHILD = child;
		Point a = new Point(
				PARENT.getXCentered(),
				CHILD.getYCentered() - (CHILD.getYCentered() - PARENT.getYCentered()) / 2
		);
		Point b = new Point(
				CHILD.getXCentered(),
				CHILD.getYCentered() - (CHILD.getYCentered() - PARENT.getYCentered()) / 2
		);

		Line first = new VLine(this, PARENT.getCenteredPosition(), a),
				second = new HLine(this, a, b),
				third = new VLine(this, b, CHILD.snapToEdge(b));

		first.setPrev(PARENT);
		first.setNext(second);
		second.setPrev(first);
		second.setNext(third);
		third.setPrev(second);
		third.setNext(CHILD);

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
				&& ((Relationship) obj).PARENT == PARENT
				&& ((Relationship) obj).CHILD == CHILD;
	}

	public Optional<Line> getFirst() {
		return LINE_LIST.stream()
				.filter(line -> line.getPrev().equals(PARENT))
				.findFirst();
	}

	public Optional<Line> getLast() {
		return LINE_LIST.stream()
				.filter(line -> line.getNext().equals(CHILD))
				.findFirst();
	}

	public Pair<Line, Double> getNearestLineDistance(int x, int y) {
		return LINE_LIST.stream()
				.map(line -> new Pair<>(line, line.getDistance(x, y)))
				.min(Comparator.comparingDouble(Pair::getValue)).orElse(new Pair<>(null, Double.NaN));
	}

	public Relationship inverse() {
		return new Relationship(CHILD, PARENT);
	}
}
