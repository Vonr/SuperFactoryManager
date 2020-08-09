package ca.teamdman.sfm.client.gui.core;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;

public class FlowRepositionable implements IFlowController, PositionProvider, SizeProvider {
	private boolean isSelected = false;
	private final Position pos;
	private final Size size;
	private int     dx, dy;

	public FlowRepositionable(Position pos, Size size) {
		this.pos = pos;
		this.size = size;
	}

	public boolean isSelected() {
		return this.isSelected;
	}

	public void setSelected(boolean selected) {
		this.isSelected = selected;
	}

	@Override
	public boolean mouseClicked(BaseScreen screen, int mx, int my, int button) {
		if (!Screen.hasAltDown() || !size.contains(pos, mx, my))
			return false;
		isSelected = true;
		dx = mx - pos.getX();
		dy = my - pos.getY();
		return true;
	}

	@Override
	public boolean mouseReleased(BaseScreen screen, int mx, int my, int button) {
		if (!isSelected)
			return false;
		isSelected = false;
		return true;
	}

	@Override
	public boolean mouseDragged(BaseScreen screen, int mx, int my, int button, int dmx, int dmy) {
		if (!isSelected)
			return false;
		int newX = MathHelper.clamp(mx - getDragXOffset(), 0, 512 - size.getWidth());
		int newY = MathHelper.clamp(my - getDragYOffset(), 0, 256 - size.getHeight());
		pos.setX(newX);
		pos.setY(newY);
		return true;
	}

	@Override
	public IFlowView getView() {
		return IFlowView.NO_VIEW;
	}

	public int getDragXOffset() {
		return this.dx;
	}

	public void setDragXOffset(int dx) {
		this.dx = dx;
	}

	public int getDragYOffset() {
		return this.dy;
	}

	public void setDragYOffset(int dy) {
		this.dy = dy;
	}

	@Override
	public Position getPosition() {
		return pos;
	}

	@Override
	public Size getSize() {
		return size;
	}
}
