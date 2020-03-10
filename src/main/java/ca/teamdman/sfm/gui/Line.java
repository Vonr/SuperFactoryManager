package ca.teamdman.sfm.gui;

import javax.vecmath.Color4f;

public class Line extends Component {
	final Point HEAD, TAIL;
	final   Relationship RELATIONSHIP;
	private Component    PREV, NEXT;
	private Color4f color = BaseGui.DEFAULT_LINE_COLOUR;

	public Line(Relationship relationship, Point tail, Point head) {
		super(head, 0, 0);
		this.RELATIONSHIP = relationship;
		this.HEAD = head;
		this.TAIL = tail;
	}

	public Color4f getColor() {
		return color;
	}

	public void setColor(Color4f color) {
		this.color = color;
	}

	public void ensureHeadConnection() {
		if (NEXT == null || NEXT instanceof Line || NEXT != RELATIONSHIP.HEAD || NEXT.isInBounds(TAIL))
			return;
		Line line;
		if (this instanceof HLine)
			line = new VLine(RELATIONSHIP, HEAD, NEXT.snapToEdge(HEAD));
		else if (this instanceof VLine)
			line = new HLine(RELATIONSHIP, HEAD, NEXT.snapToEdge(HEAD));
		else
			line = new Line(RELATIONSHIP, HEAD, NEXT.snapToEdge(HEAD));
		line.setNext(NEXT);
		line.setPrev(this);
		setNext(line);
		RELATIONSHIP.addLine(line);
	}

	public void pruneIfRedundant() {
		if (!(PREV instanceof Line)) {
			if (NEXT instanceof Line) {
				if (RELATIONSHIP.TAIL.isInBounds(HEAD)) {
					RELATIONSHIP.removeLine(this);
					((Line) NEXT).setPrev(PREV);
				}
			}
		}
		if (!(NEXT instanceof Line)) {
			if (PREV instanceof Line) {
				if (RELATIONSHIP.HEAD.isInBounds(TAIL)) {
					RELATIONSHIP.removeLine(this);
					((Line) PREV).setNext(NEXT);
				}
			}
		}
	}

	public void ensureTailConnection() {
		if (PREV == null || PREV instanceof Line || PREV != RELATIONSHIP.TAIL || PREV.isInBounds(TAIL))
			return;
		Line line;
		if (this instanceof HLine)
			line = new VLine(RELATIONSHIP, PREV.snapToEdge(TAIL), TAIL);
		else if (this instanceof VLine)
			line = new HLine(RELATIONSHIP, PREV.snapToEdge(TAIL), TAIL);
		else
			line = new Line(RELATIONSHIP, PREV.snapToEdge(TAIL), TAIL);
		line.setPrev(PREV);
		line.setNext(this);
		setPrev(line);
		RELATIONSHIP.addLine(line);
	}


	/**
	 * Gets the smallest distance from a point to this line.
	 * https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line#Line_defined_by_two_points
	 */
	public double getDistance(int x, int y) {
		return Math.abs(
				(TAIL.getY() - HEAD.getY()) * x
						- (TAIL.getX() - HEAD.getX()) * y
						+ TAIL.getX() * HEAD.getY()
						- TAIL.getY() * HEAD.getX()
		) / Math.sqrt(
				Math.pow(TAIL.getY() - HEAD.getY(), 2)
						+ Math.pow(TAIL.getX() - HEAD.getX(), 2)
		);
	}

	public void drag(int x, int y) {
		if (PREV instanceof Line)
			((Line) PREV).HEAD.setXY(TAIL);
		if (NEXT instanceof Line)
			((Line) NEXT).TAIL.setXY(HEAD);
		pruneIfRedundant();
		ensureHeadConnection();
		ensureTailConnection();
	}


	public Component getPrev() {
		return PREV;
	}

	public void setPrev(Component PREV) {
		this.PREV = PREV;
	}

	public Component getNext() {
		return NEXT;
	}

	public void setNext(Component NEXT) {
		this.NEXT = NEXT;
	}

	@Override
	public String toString() {
		return String.format("Line (%d, %d) => (%d, %d)", TAIL.getX(), TAIL.getY(), HEAD.getX(), HEAD.getY());
	}

	public enum Direction {
		FORWARDS,
		BACKWARDS
	}
}
