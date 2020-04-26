package ca.teamdman.sfm.common.container.core.component;

import ca.teamdman.sfm.common.container.core.Point;
import ca.teamdman.sfm.common.container.core.Relationship;

public class HLine extends Line {
	public HLine(Relationship r, Point head, Point tail) {
		super(r, head, tail);
	}

	@Override
	public void drag(int x, int y) {
		HEAD.setY(y);
		TAIL.setY(y);
		super.drag(x, y);
	}
}
