package ca.teamdman.sfm.common.container.manager;

import ca.teamdman.sfm.common.container.ManagerContainer;

public class VLine extends Line {
	public VLine(ManagerContainer container, Relationship r, Point tail, Point head) {
		super(container, r, tail, head);
	}

	@Override
	public void drag(int x, int y) {
		HEAD.setX(x);
		TAIL.setX(x);
		super.drag(x, y);
	}
}
