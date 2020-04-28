package ca.teamdman.sfm.client.gui.impl;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.util.math.MathHelper;

public class FlowPositionable implements IFlowController {
	private Rectangle2d area;
	private int         dx, dy;
	private boolean isSelected = false;

	public FlowPositionable(Rectangle2d area) {
		this.area = area;
	}

	public Rectangle2d getArea() {
		return area;
	}

	public void setArea(Rectangle2d area) {
		this.area = area;
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean selected) {
		this.isSelected = selected;
	}

	@Override
	public boolean mouseClicked(BaseScreen screen, int mx, int my, int button) {
		if (!Screen.hasAltDown() || !area.contains(mx, my))
			return false;
		this.isSelected = true;
		this.dx = mx - area.getX();
		this.dy = my - area.getY();
		return true;
	}

	@Override
	public boolean mouseReleased(BaseScreen screen, int mx, int my, int button) {
		if (!isSelected)
			return false;
		this.isSelected = false;
		return true;
	}

	@Override
	public boolean mouseDragged(BaseScreen screen, int mx, int my, int button, int dmx, int dmy) {
		if (!Screen.hasAltDown())
			this.isSelected = false;
		if (!isSelected)
			return false;
		int newX = MathHelper.clamp(mx - dx, 0, 512 - area.getWidth());
		int newY = MathHelper.clamp(my - dy, 0, 256 - area.getHeight());
		setArea(new Rectangle2d(newX, newY, area.getWidth(), area.getHeight()));
		return true;
	}
}
