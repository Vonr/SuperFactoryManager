package ca.teamdman.sfm.gui;

import javax.vecmath.Vector2d;

public abstract class Component {
	protected int x, y, width, height;

	public Component(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public int getXCentered() {
		return x + width / 2;
	}

	public int getYCentered() {
		return y + height / 2;
	}

	public Point snapToEdge(Point p) {
		return snapToEdge(p.getX(), p.getY());
	}

	public Point snapToEdge(int x, int y) {
		return new Point(
				x < getX() ? getX() : Math.min(x, getX() + width),
				y < getY() ? getY() : Math.min(y, getY() + height)
		);
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

	public void setXY(Component c) {
		setXY(c.getXCentered(), c.getYCentered());
	}

	public void setXY(int x, int y) {
		setX(x);
		setY(y);
	}

	public boolean isInBounds(int mx, int my) {
		return mx >= x && mx <= x + width && my >= y && my <= y + height;
	}

	protected <T extends Component> T copy(ManagerGui gui) { return null; };
}
