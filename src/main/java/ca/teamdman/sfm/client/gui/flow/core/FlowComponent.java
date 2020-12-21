/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.core;

import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.common.flow.data.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Optional;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;

public abstract class FlowComponent implements IFlowController, IFlowTangible, IFlowView, FlowViewHolder {

	private final Position dragStart = new Position();
	private final Position dragOffset = new Position();
	private boolean visible = true;
	private boolean enabled = true;
	private boolean draggable = true;
	private boolean isDragging = false;
	private Position position;
	private Size size;

	public FlowComponent() {
		this(0, 0, 0, 0);
	}

	public FlowComponent(int x, int y, int width, int height) {
		this(new Position(x, y), new Size(width, height));
	}

	public FlowComponent(Position pos, Size size) {
		this.position = pos;
		this.size = size;
	}

	@Override
	public boolean mousePressed(int mx, int my, int button) {
		if (canStartDrag() && isInBounds(mx, my)) {
			isDragging = true;
			dragStart.setXY(mx, my);
			dragOffset.setXY(mx - getPosition().getX(), my - getPosition().getY());
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseDragged(int mx, int my, int button, int dmx, int dmy) {
		if (isDragging) {
			int newX = MathHelper.clamp(mx - dragOffset.getX(), 0, 512 - size.getWidth());
			int newY = MathHelper.clamp(my - dragOffset.getY(), 0, 256 - size.getHeight());

			if (Screen.hasShiftDown()) {
				newX = newX - newX % 5;
				newY = newY - newY % 5;
			}
			int dx = getPosition().getX() - newX;
			int dy = getPosition().getY() - newY;
			getPosition().setXY(newX, newY);
			onDrag(dx, dy, mx, my);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean mouseReleased(int mx, int my, int button) {
		if (isDragging) {
			isDragging = false;
			if (!dragStart.equals(getPosition())) {
				onDragFinished(
					getPosition().getX() - dragStart.getX(),
					getPosition().getY() - dragStart.getY(),
					mx,
					my
				);
			}
			return true;
		} else {
			return false;
		}
	}

	public void onDrag(int dx, int dy, int mx, int my) {

	}

	public void onDragFinished(int dx, int dy, int mx, int my) {

	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean value) {
		this.visible = false;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public IFlowView getView() {
		return this;
	}

	@Override
	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	@Override
	public Size getSize() {
		return size;
	}

	public void setSize(Size size) {
		this.size = size;
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		screen.drawRect(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getHeight(),
			CONST.PANEL_BACKGROUND
		);
	}

	public Optional<FlowComponent> getElementUnderMouse(int mx, int my) {
		return isInBounds(mx, my) ? Optional.of(this) : Optional.empty();
	}

	public boolean isDraggable() {
		return draggable;
	}

	public void setDraggable(boolean draggable) {
		this.draggable = draggable;
	}

	public boolean canStartDrag() {
		return isDraggable() && Screen.hasAltDown();
	}
}
