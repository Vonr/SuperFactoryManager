package ca.teamdman.sfm.common.container.core.component;

import ca.teamdman.sfm.common.container.core.Point;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.Optional;

public abstract class Component implements INBTSerializable<CompoundNBT> {

	protected Point position;
	protected int width, height;


	public Component(Point position, int width, int height) {
		this.position = position;
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
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

	public void setPosition(Point position) {
		this.position = position;
	}

	public boolean isInBounds(Point p) {
		return isInBounds(p.getX(), p.getY());
	}

	public boolean isInBounds(int mx, int my) {
		return mx >= position.getX() && mx <= position.getX() + width && my >= position.getY() && my <= position.getY() + height;
	}

	public Optional<Component> copy() {
		return Optional.empty();
	}

	@Override
	public String toString() {
		return String.format("Component (%d,%d) ", position.getX(), position.getY());
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT tag = new CompoundNBT();
		tag.put("position", position.serializeNBT());
		tag.putInt("width", width);
		tag.putInt("height", height);
		return null;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		Point position = new Point();
		position.deserializeNBT(nbt.getCompound("position"));
		this.position = position;
		this.width = nbt.getInt("width");
		this.height = nbt.getInt("height");
	}
}
