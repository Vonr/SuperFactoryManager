package ca.teamdman.sfm.common.container.manager;

import ca.teamdman.sfm.client.gui.ManagerScreen;
import ca.teamdman.sfm.common.container.ManagerContainer;

import java.util.Optional;

public abstract class Component {
	protected       Point            position;
	protected final ManagerContainer CONTAINER;

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	protected int   width, height;

	public Component(ManagerContainer container, Point position, int width, int height) {
		this.CONTAINER = container;
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

	protected Optional<Component> copy() {
		return Optional.empty();
	}

	@Override
	public String toString() {
		return String.format("Component (%d,%d) ", position.getX(), position.getY());
	}

}
