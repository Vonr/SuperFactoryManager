package ca.teamdman.sfm.gui;

public class VLine extends Line {
	public VLine(Relationship r, Point head, Point tail) {
		super(r, head, tail);
	}

	@Override
	public void drag(int x, int y) {
		super.drag(x, y);
		HEAD.setX(x);
		TAIL.setX(x);
		reflow();
	}
}
