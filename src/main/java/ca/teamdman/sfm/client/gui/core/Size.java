package ca.teamdman.sfm.client.gui.core;

public class Size {
	private int width, height;
	public Size(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public Size(Size copy) {
		this(copy.width, copy.height);
	}

	/**
	 * Checks if a coordinate is contained in this element
	 *
	 * @param x Scaled x coordinate
	 * @param y Scaled y coordinate
	 * @return true if coordinate is contained in this element's area, false otherwise
	 */
	boolean contains(Position myPosition, int x, int y) {
		return x >= myPosition.getX() && x <= myPosition.getX() + width && y >= myPosition.getY() && y <= myPosition.getY() + height;
	}

	int getWidth() {
		return this.width;
	}

	void setWidth(int width) {
		this.width = width;
	}

	int getHeight() {
		return this.height;
	}

	void setHeight(int height) {
		this.height = height;
	}
}
