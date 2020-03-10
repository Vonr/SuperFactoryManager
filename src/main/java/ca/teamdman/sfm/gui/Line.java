package ca.teamdman.sfm.gui;

import javax.vecmath.Color4f;

public class Line extends Component {
	final Point HEAD, TAIL;
	final   Relationship RELATIONSHIP;
	private Component    PREV, NEXT;
	private Color4f color = BaseGui.DEFAULT_LINE_COLOUR;

	public Line(Relationship relationship, Point head, Point tail) {
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

	public void reflow() {
		reflow(Direction.FORWARDS);
		reflow(Direction.BACKWARDS);
	}

	public void checkAndOptimizeContinuity(Direction d) {
		switch (d) {
			case FORWARDS:
				if (NEXT != null) {
					if (NEXT instanceof Line) {
						if (RELATIONSHIP.TAIL.isInBounds(TAIL)) {
							RELATIONSHIP.removeLine((Line) NEXT);
							setNext(RELATIONSHIP.TAIL);
						}
					} else if (!(NEXT.isInBounds(TAIL))) {
						Line line;
						if (this instanceof HLine)
							line = new VLine(RELATIONSHIP, TAIL, new Point(NEXT));
						else if (this instanceof VLine)
							line = new HLine(RELATIONSHIP, TAIL, new Point(NEXT));
						else
							line = new Line(RELATIONSHIP, TAIL, new Point(NEXT));
						line.setPrev(this);
						line.setNext(NEXT);
						setNext(line);
						RELATIONSHIP.addLine(line);
					}
				}
				break;
			case BACKWARDS:
				if (PREV != null) {
					if (PREV instanceof Line) {
						if (RELATIONSHIP.HEAD.isInBounds(HEAD)) {
							RELATIONSHIP.removeLine((Line) PREV);
							setNext(RELATIONSHIP.HEAD);
						}
					} else if (!(PREV.isInBounds(HEAD))) {
						Line line;
						if (this instanceof HLine)
							line = new VLine(RELATIONSHIP, HEAD, new Point(PREV));
						else if (this instanceof VLine)
							line = new HLine(RELATIONSHIP, HEAD, new Point(PREV));
						else
							line = new Line(RELATIONSHIP, HEAD, new Point(PREV));
						line.setPrev(PREV);
						line.setNext(this);
						setPrev(line);
						RELATIONSHIP.addLine(line);
					}
				}
				break;
		}
	}

	public void reflow(Direction d) {
		switch (d) {
			case FORWARDS: // head has changed
				if (this instanceof VLine)
					TAIL.setX(HEAD.getX());
				if (this instanceof HLine)
					TAIL.setY(HEAD.getY());
				if (NEXT != null) {
					if (NEXT instanceof Line) {
						((Line) NEXT).reflow(d);
					}
				}
				break;
			case BACKWARDS: // tail has changed
				if (this instanceof VLine)
					HEAD.setX(TAIL.getX());
				if (this instanceof HLine)
					HEAD.setY(TAIL.getY());
				if (PREV != null) {
					if (PREV instanceof Line) {
						((Line) PREV).reflow(d);
					}
				}
				break;
		}
	}

	//https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line#Line_defined_by_two_points
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
		reflow();
		RELATIONSHIP.LINE_LIST.get(0).checkAndOptimizeContinuity(Direction.FORWARDS);
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

	public enum Direction {
		FORWARDS,
		BACKWARDS
	}
}
