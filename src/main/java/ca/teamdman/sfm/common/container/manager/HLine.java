package ca.teamdman.sfm.common.container.manager;

import ca.teamdman.sfm.common.container.ManagerContainer;

public class HLine extends Line {
	public HLine(ManagerContainer container, Relationship r, Point head, Point tail) {
		super(container, r, head, tail);
	}

	@Override
	public void drag(int x, int y) {
		HEAD.setY(y);
		TAIL.setY(y);
		super.drag(x, y);
	}
}
