/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.core;

import java.util.Objects;
import java.util.function.Supplier;
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
	private int x, y;

	public Position(Position copy) {
		this(copy.getX(), copy.getY());
	}

	public Position(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Position() {
		this(0, 0);
	}

	public Position(CompoundNBT tag) {
		this(0,0);
		deserializeNBT(tag);
	}

	public static Position fromLong(long packed) {
		return new Position((int) (packed >> 32), (int) packed);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Position position = (Position) o;
		return getX() == position.getX() &&
			getY() == position.getY();
	}

	public boolean equals(int x, int y) {
		return x == this.x && y == this.y;
	}

	public Position withConstantOffset(int x, int y) {
		return new Position() {
			@Override
			public int getX() {
				return Position.this.getX() + x;
			}

			@Override
			public int getY() {
				return Position.this.getY() + y;
			}
		};
	}

	public Position withConstantOffset(Position other) {
		return new Position() {
			@Override
			public int getX() {
				return Position.this.getX() + other.getX();
			}

			@Override
			public int getY() {
				return Position.this.getY() + other.getY();
			}
		};
	}

	public Position withConstantOffset(Supplier<Integer> x, Supplier<Integer> y) {
		return new Position() {
			@Override
			public int getY() {
				return Position.this.getY() + y.get();
			}

			@Override
			public int getX() {
				return Position.this.getX() + x.get();
			}
		};
	}

	@Override
	public int hashCode() {
		return Objects.hash(getX(), getY());
	}

	public long toLong() {
		return (((long) getX()) << 32) | (getY() & 0xffffffffL);
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
		return new Position(getX() + x, getY() + y);
	}

	/**
	 * Returns a new position offset from this position by {@code other} amount
	 * @param other Offset amount
	 * @return Offset position
	 */
	public Position withOffset(Position other) {
		return new Position(getX() + other.getX(), getY() + other.getY());
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
	 *
	 * @param pos operand
	 * @return new position
	 */
	public Position add(Position pos) {
		return new Position(getX() + pos.getX(), getY() + pos.getY());
	}

	/**
	 * Returns a new position with values multiplied
	 *
	 * @param val multiplier
	 * @return multiplied position
	 */
	public Position multiply(int val) {
		return new Position(getX() * val, getY() * val);
	}

	/**
	 * Gets the dot product of this position against another
	 *
	 * @param pos operand
	 * @return dot product
	 */
	public int dot(Position pos) {
		return (getX() * pos.getX()) + (getY() * pos.getY());
	}

	/**
	 * Gets the squared magnitude of this position
	 *
	 * @return magnitude, squared
	 */
	public int magnitudeSquared() {
		return (getX() * getX()) + (getY() * getY());
	}

	public void setXY(int x, int y) {
		setX(x);
		setY(y);
	}

	public void setXY(Position pos) {
		setXY(pos.x, pos.y);
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

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT tag = new CompoundNBT();
		tag.putInt("x", getX());
		tag.putInt("y", getY());
		return tag;
	}


	@Override
	public String toString() {
		return "Position[" + getX() + ", " + getY() + "]";
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		setXY(
			nbt.getInt("x"),
			nbt.getInt("y")
		);
	}
}
