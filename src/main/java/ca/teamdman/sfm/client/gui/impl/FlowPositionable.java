package ca.teamdman.sfm.client.gui.impl;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;

public class FlowPositionable implements IFlowController {
	private boolean isSelected = false;
	private int     x, y, width, height, dx, dy;

	public FlowPositionable(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void setXY(int x, int y) {
		this.setX(x);
		this.setY(y);
	}

	public boolean isSelected() {
		return this.isSelected;
	}

	public void setSelected(boolean selected) {
		this.isSelected = selected;
	}

	@Override
	public boolean mouseClicked(BaseScreen screen, int mx, int my, int button) {
		if (!Screen.hasAltDown() || !this.contains(mx, my))
			return false;
		this.isSelected = true;
		this.dx = mx - this.getX();
		this.dy = my - this.getY();
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
		int newX = MathHelper.clamp(mx - this.getDragXOffset(), 0, 512 - this.getWidth());
		int newY = MathHelper.clamp(my - this.getDragYOffset(), 0, 256 - this.getHeight());
		this.setX(newX);
		this.setY(newY);
		return true;
	}

	@Override
	public IFlowView getView() {
		return IFlowView.NO_VIEW;
	}

	public int getDragXOffset() {
		return this.dx;
	}

	public void setDragXOffset(int dx) {
		this.dx = dx;
	}

	public int getDragYOffset() {
		return this.dy;
	}

	public void setDragYOffset(int dy) {
		this.dy = dy;
	}

	/**
	 * Checks if a coordinate is contained in this element
	 *
	 * @param x Scaled x coordinate
	 * @param y Scaled y coordinate
	 * @return true if coordinate is contained in this element's area, false otherwise
	 */
	public boolean contains(int x, int y) {
		return x >= this.getX() && x <= this.getX() + this.getWidth() && y >= this.getY() && y <= this.getY() + this.getHeight();
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
