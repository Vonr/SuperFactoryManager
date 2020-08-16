package ca.teamdman.sfm.common.flowdata;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

/**
 * Position class for GUI elements.
 */
public class Position implements INBTSerializable<CompoundNBT> {

	public static final Position ZERO = new Position(0, 0) {
		@Override
		public void setX(int x) {
			throw new RuntimeException("The ZERO position instance can not be modified.");
		}

		@Override
		public void setY(int y) {
			throw new RuntimeException("This ZERO position instance can not be modified.");
		}
	};
	private boolean posChangeDebounce = false; // only fire positionChanged once when using setXY
	private int x, y;

	@SuppressWarnings("CopyConstructorMissesField")
	public Position(Position copy) {
		this(copy.x, copy.y);
	}

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Position() {
		this(0, 0);
	}

	public static Position fromLong(long packed) {
		return new Position((int) (packed >> 32), (int) packed);
	}

	public long toLong() {
		return (((long) getX()) << 32) | (y & 0xffffffffL);
	}

	public Position copy() {
		return new Position(this);
	}

	/**
	 * Returns a copy of this position, offset by the given x,y values
	 *
	 * @param x Offset x
	 * @param y Offset y
	 * @return Offset Position
	 */
	public Position withOffset(int x, int y) {
		return new Position(this.x + x, this.y + y);
	}

	/**
	 * Returns a new position for the result of vector substitution
	 *
	 * @param pos operand
	 * @return new position
	 */
	public Position subtract(Position pos) {
		return new Position(getX() - pos.getX(), getY() - pos.getY());
	}

	/**
	 * Returns a new position for the result of vector addition
	 * @param pos operand
	 * @return new position
	 */
	public Position add(Position pos) {
		return new Position(getX() + pos.getX(), getY() + pos.getY());
	}

	/**
	 * Returns a new position with values multiplied
	 * @param val multiplier
	 * @return multiplied position
	 */
	public Position multiply(int val) {
		return new Position(getX() * val, getY() * val);
	}

	/**
	 * Gets the dot product of this position against another
	 * @param pos operand
	 * @return dot product
	 */
	public int dot(Position pos) {
		return (getX() * pos.getX()) + (getY() * pos.getY());
	}

	/**
	 * Gets the squared magnitude of this position
	 * @return magnitude, squared
	 */
	public int magnitudeSquared() {
		return (getX() * getX()) + (getY() * getY());
	}

	public void setXY(int x, int y) {
		int oldX = this.x, oldY = this.y;
		posChangeDebounce = true;
		setX(x);
		setY(y);
		posChangeDebounce = false;
		onPositionChanged(oldX, oldY, x, y);
	}

	public void setXY(Position pos) {
		setXY(pos.x, pos.y);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		int oldX = this.x;
		this.x = x;
		if (!posChangeDebounce) {
			onPositionChanged(oldX, y, x, y);
		}
	}

	public void onPositionChanged(int oldX, int oldY, int newX, int newY) {

	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		int oldY = this.y;
		this.y = y;
		if (!posChangeDebounce) {
			onPositionChanged(x, oldY, x, y);
		}
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT tag = new CompoundNBT();
		tag.putInt("x", x);
		tag.putInt("y", y);
		return tag;
	}


	@Override
	public String toString() {
		return "Position [x: " + x + ", y: " + y + "]";
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		x = nbt.getInt("x");
		y = nbt.getInt("y");
	}
}
