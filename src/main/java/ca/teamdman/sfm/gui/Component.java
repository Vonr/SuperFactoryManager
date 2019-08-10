package ca.teamdman.sfm.gui;

public abstract class Component {
	protected int x, y, width, height;

	public Component(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
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

	public boolean isInBounds(int mx, int my) {
		return mx >= x && mx <= x + width && my >= y && my <= y + height;
	}

	protected abstract <T extends Component> T copy(ManagerGui gui);
}
