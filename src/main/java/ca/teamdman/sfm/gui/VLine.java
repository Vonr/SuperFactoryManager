package ca.teamdman.sfm.gui;

public class VLine extends Line {
	public VLine(Relationship r, Point head, Point tail) {
		super(r, head, tail);
	}

	@Override
	public void drag(int x, int y) {
		HEAD.setX(x);
		TAIL.setX(x);
		super.drag(x, y);
	}
}
