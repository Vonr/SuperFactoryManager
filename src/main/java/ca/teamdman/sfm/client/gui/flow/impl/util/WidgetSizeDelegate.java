package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.client.gui.flow.core.Size;
import net.minecraft.client.gui.widget.Widget;

class WidgetSizeDelegate extends Size {

	private final Size ORIGINAL;
	private Widget widget;

	public WidgetSizeDelegate(Size copy) {
		super(0, 0);
		this.ORIGINAL = copy;
	}

	public void setWidget(Widget widget) {
		this.widget = widget;
	}

	@Override
	public int getWidth() {
		return ORIGINAL.getWidth();
	}

	@Override
	public void setWidth(int width) {
		ORIGINAL.setWidth(width);
		if (widget != null) {
			widget.setWidth(width);
		}
	}

	@Override
	public int getHeight() {
		return ORIGINAL.getHeight();
	}

	@Override
	public void setHeight(int height) {
		ORIGINAL.setHeight(height);
		if (widget != null) {
			widget.setHeight(height);
		}
	}
}
