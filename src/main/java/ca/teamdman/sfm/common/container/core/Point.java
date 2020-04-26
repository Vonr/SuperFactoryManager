package ca.teamdman.sfm.common.container.core;

import ca.teamdman.sfm.common.container.core.component.Component;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class Point implements INBTSerializable<CompoundNBT> {
	private int x, y;

	public Point() {}

	public Point(Component c) {
		this(c.getPosition());
	}

	public Point(Point p) {
		this(p.getX(), p.getY());
	}

	public Point(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Point && ((Point) obj).x == x && ((Point) obj).y == y;
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
		if (c == null)
			return;
		setXY(c.getCenteredPosition());
	}

	public void setXY(Point p) {
		if (p == null)
			return;
		setXY(p.getX(), p.getY());
	}

	public void setXY(int x, int y) {
		setX(x);
		setY(y);
	}

	@Override
	public String toString() {
		return "Point (" + x + "," + y + ")";
	}

	public Point copy() {
		return new Point(this);
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("x", x);
		nbt.putInt("y", y);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.x = nbt.getInt("x");
		this.y = nbt.getInt("y");
	}

}
