package ca.teamdman.sfm.client.gui.manager;

import ca.teamdman.sfm.client.gui.ManagerScreen;

import java.util.Optional;

public abstract class Component {
	@SuppressWarnings("CanBeFinal")
	protected Point position;
	@SuppressWarnings("CanBeFinal")
	protected int   width, height;

	public Component(Point position, int width, int height) {
		this.position = position;
		this.width = width;
		this.height = height;
	}

	public Point getCenteredPosition() {
		return new Point(getXCentered(), getYCentered());
	}

	public int getXCentered() {
		return position.getX() + width / 2;
	}

	public int getYCentered() {
		return position.getY() + height / 2;
	}

	public Point snapToEdge(Point p) {
		return snapToEdge(p.getX(), p.getY());
	}

	public Point snapToEdge(int x, int y) {
		return new Point(
				x < position.getX() ? position.getX() : Math.min(x, position.getX() + width),
				y < position.getY() ? position.getY() : Math.min(y, position.getY() + height)
		);
	}

	public Point getPosition() {
		return position;
	}

	public boolean isInBounds(Point p) {
		return isInBounds(p.getX(), p.getY());
	}

	public boolean isInBounds(int mx, int my) {
		return mx >= position.getX() && mx <= position.getX() + width && my >= position.getY() && my <= position.getY() + height;
	}

	protected <T extends Component> Optional<T> copy(ManagerScreen gui) {
		return Optional.empty();
	}

	@Override
	public String toString() {
		return String.format("Component (%d,%d) ", position.getX(), position.getY());
	}

}
