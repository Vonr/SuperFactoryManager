package ca.teamdman.sfm.gui;

import javafx.util.Pair;

import java.util.*;

public class Relationship {
	public final List<Line> LINE_LIST = new ArrayList<>();
	public final Component  HEAD, TAIL;

	public Relationship(Component parent, Component child) {
		this.HEAD = parent;
		this.TAIL = child;
		Point a = new Point(
				HEAD.getXCentered(),
				TAIL.getYCentered() - (TAIL.getYCentered() - HEAD.getYCentered()) / 2
		);
		Point b = new Point(
				TAIL.getXCentered(),
				TAIL.getYCentered() - (TAIL.getYCentered() - HEAD.getYCentered()) / 2
		);

		Line first = new VLine(this, HEAD.getCenteredPosition(), a),
				second = new HLine(this, a, b),
				third = new VLine(this, b, TAIL.snapToEdge(b));

		first.setPrev(HEAD);
		first.setNext(second);
		second.setPrev(first);
		second.setNext(third);
		third.setPrev(second);
		third.setNext(TAIL);

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
				&& ((Relationship) obj).HEAD == HEAD
				&& ((Relationship) obj).TAIL == TAIL;
	}

	public Optional<Line> getFirst() {
		return LINE_LIST.stream()
				.filter(line -> line.getPrev().equals(HEAD))
				.findFirst();
	}

	public Optional<Line> getLast() {
		return LINE_LIST.stream()
				.filter(line -> line.getNext().equals(TAIL))
				.findFirst();
	}

	public Pair<Line, Double> getNearestLineDistance(int x, int y) {
		return LINE_LIST.stream()
				.map(line -> new Pair<>(line, line.getDistance(x, y)))
				.min(Comparator.comparingDouble(Pair::getValue)).orElse(new Pair<>(null, Double.NaN));
	}

	public Relationship inverse() {
		return new Relationship(TAIL, HEAD);
	}
}
