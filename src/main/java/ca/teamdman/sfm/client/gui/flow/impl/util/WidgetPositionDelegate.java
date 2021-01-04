package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.common.flow.core.Position;
import net.minecraft.client.gui.widget.Widget;

class WidgetPositionDelegate extends Position {

	private final Position ORIGINAL;
	private Widget widget;

	public WidgetPositionDelegate(Position copy) {
		this.ORIGINAL = copy;
	}

	public void setWidget(Widget widget) {
		this.widget = widget;
	}

	@Override
	public int getX() {
		return ORIGINAL.getX();
	}

	@Override
	public void setX(int x) {
		ORIGINAL.setX(x);
		if (widget != null) {
			widget.x = x;
		}
	}

	@Override
	public int getY() {
		return ORIGINAL.getY();
	}

	@Override
	public void setY(int y) {
		ORIGINAL.setY(y);
		if (widget != null) {
			widget.y = y;
		}
	}
}
