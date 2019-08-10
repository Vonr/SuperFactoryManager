package ca.teamdman.sfm.gui;

import javax.vecmath.Color4f;

public class Line extends Component {
	final Point HEAD, TAIL;
	public Color4f color = new Color4f(0.4f, 0.4f, 0.4f, 1);

	public Line(Point head, Point tail) {
		super(head.getX(), head.getY(), 0, 0);
		this.HEAD = head;
		this.TAIL = tail;
		HEAD.NEXT = this;
		TAIL.PREV = this;
	}

	public void reflow(Direction d) {
		switch (d) {
			case FORWARDS: // head has changed
				if (this instanceof VLine)
					TAIL.setX(HEAD.getX());
				if (this instanceof HLine)
					TAIL.setY(HEAD.getY());
				if (TAIL.NEXT != null && TAIL.NEXT instanceof Line)
					((Line) TAIL.NEXT).reflow(d);
				break;
			case BACKWARDS: // tail has changed
				if (this instanceof VLine)
					HEAD.setX(TAIL.getX());
				if (this instanceof HLine)
					HEAD.setY(TAIL.getY());
				if (HEAD.PREV != null && HEAD.PREV instanceof Line)
					((Line) HEAD.PREV).reflow(d);
				break;
		}
	}

	public double getDistance(int x, int y) {
		return Math.abs(
				(TAIL.getY() - HEAD.getY()) * x
						- (TAIL.getX() - HEAD.getX()) * y
						+ TAIL.getX() * HEAD.getY()
						- TAIL.getY() * HEAD.getX()
		) / Math.sqrt(Math.pow(TAIL.getY() - HEAD.getY(), 2) + Math.pow(TAIL.getX() - HEAD.getX(), 2));
	}

	public void drag(int x, int y) {

	}

	public enum Direction {
		FORWARDS,
		BACKWARDS
	}
}
