package ca.teamdman.sfm.gui;

import javafx.util.Pair;

import javax.vecmath.Vector2d;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Relationship {
	public final List<Line> LINE_LIST = new ArrayList<>();
	public final Component  PARENT, CHILD;

	public Relationship(Component parent, Component child) {
		this.PARENT = parent;
		this.CHILD = child;
		Point snap = CHILD.snapToEdge(
				CHILD.getXCentered(),
				CHILD.getYCentered() - (CHILD.getYCentered() - PARENT.getYCentered()) / 2
		);
		Point second = new Point(
				PARENT.getXCentered(),
				CHILD.getYCentered() - (CHILD.getYCentered() - PARENT.getYCentered()) / 2
		);
		Point third = new Point(
				CHILD.getXCentered(),
				CHILD.getYCentered() - (CHILD.getYCentered() - PARENT.getYCentered()) / 2
		);

		LINE_LIST.addAll(Arrays.asList(
				new VLine(new Point(
						PARENT.getXCentered(),
						PARENT.getYCentered()
				), second),
				new HLine(second, third),
				new VLine(third, snap)
		));
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Relationship
				&& ((Relationship) obj).PARENT == PARENT
				&& ((Relationship) obj).CHILD == CHILD;
	}

	public Pair<Line, Double> getNearestLineDistance(int x, int y) {
		//https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line#Line_defined_by_two_points
		return LINE_LIST.stream()
				.map(line -> new Pair<>(line, line.getDistance(x,y)))
				.min(Comparator.comparingDouble(Pair::getValue)).orElse(new Pair<>(null, Double.NaN));
	}
}
