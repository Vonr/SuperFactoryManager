package ca.teamdman.sfm.client.gui.manager;

public class VLine extends Line {
	public VLine(Relationship r, Point tail, Point head) {
		super(r, tail, head);
	}

	@Override
	public void drag(int x, int y) {
		HEAD.setX(x);
		TAIL.setX(x);
		super.drag(x, y);
	}
}
