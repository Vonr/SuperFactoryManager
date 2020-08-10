package ca.teamdman.sfm.client.gui.core;

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
		this(0,0);
	}

	public Position copy() {
		return new Position(this);
	}

	public void setXY(int x, int y) {
		int oldX = this.x, oldY = this.y;
		posChangeDebounce = true;
		setX(x);
		setY(y);
		posChangeDebounce = false;
		onPositionChanged(oldX, oldY, x, y);
	}

	public void setXY(Position pos){
		setXY(pos.x, pos.y);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		int oldX = this.x;
		this.x = x;
		if (!posChangeDebounce)
			onPositionChanged(oldX, y, x, y);
	}

	public void onPositionChanged(int oldX, int oldY, int newX, int newY) {

	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		int oldY = this.y;
		this.y = y;
		if (!posChangeDebounce)
			onPositionChanged(x, oldY, x, y);
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT tag = new CompoundNBT();
		tag.putInt("x", x);
		tag.putInt("y", y);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		x = nbt.getInt("x");
		y = nbt.getInt("y");
	}
}
