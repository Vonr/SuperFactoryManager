package ca.teamdman.sfm.gui;

public class HLine extends Line {
	public HLine(Relationship r, Point head, Point tail) {
		super(r, head, tail);
	}

	@Override
	public void drag(int x, int y) {
		super.drag(x, y);
		HEAD.setY(y);
		TAIL.setY(y);
		reflow();
	}
}
