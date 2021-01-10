/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.core;

import ca.teamdman.sfm.common.config.Config.Client;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.core.PositionHolder;
import ca.teamdman.sfm.common.flow.core.SizeHolder;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;

public class FlowComponent implements PositionHolder, SizeHolder {

	private final Position dragStart = new Position();
	private final Position dragOffset = new Position();
	private boolean visible = true;
	private boolean enabled = true;
	private boolean draggable = true;
	private boolean isDragging = false;
	private boolean isHovering = false;
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

	public Position getCentroid() {
		return getPosition().withOffset(getSize().getWidth() / 2, getSize().getHeight() / 2);
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

	public boolean mousePressed(int mx, int my, int button) {
		if (canStartDrag() && isInBounds(mx, my)) {
			isDragging = true;
			dragStart.setXY(mx, my);
			dragOffset.setXY(mx - getPosition().getX(), my - getPosition().getY());
			return true;
		}
		// Consume click event if mouse is over background
		// we don't want elements occluded by this one to detect events
		return isInBounds(mx, my);
	}

	public boolean canStartDrag() {
		return isDraggable() && Screen.hasAltDown();
	}

	public boolean isInBounds(int mx, int my) {
		return getSize().contains(getPosition(), mx, my);
	}

	public boolean isDraggable() {
		return draggable;
	}

	public void setDraggable(boolean draggable) {
		this.draggable = draggable;
	}

	public boolean mouseDragged(int mx, int my, int button, int dmx, int dmy) {
		if (isDragging) {
			int newX = mx - dragOffset.getX();
			int newY = my - dragOffset.getY();

			if (!Client.allowElementsOutOfBounds) {
				newX = MathHelper.clamp(newX, 0, 512 - size.getWidth());
				newY = MathHelper.clamp(newY, 0, 256 - size.getHeight());
			}

			if (Screen.hasShiftDown() || Client.alwaysSnapMovementToGrid) {
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

	public void onDrag(int dx, int dy, int mx, int my) {

	}

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

	public void onDragFinished(int dx, int dy, int mx, int my) {

	}

	@Override
	public String toString() {
		return "FlowComponent{" +
			"visible=" + visible +
			", enabled=" + enabled +
			", position=" + position +
			", size=" + size +
			'}';
	}

	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		drawTooltip(screen, matrixStack, mx, my, deltaTime);
	}

	public void drawTooltip(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		if (isTooltipEnabled(mx, my)) {
			screen.pauseScissor(); // Disable scissoring when drawing tooltip
			net.minecraftforge.fml.client.gui.GuiUtils.drawHoveringText(
				matrixStack,
				getTooltip(),
				mx,
				my,
				screen.scaledWidth,
				screen.scaledHeight,
				-1,
				screen.getFontRenderer()
			);
			screen.resumeScissor();
		}
	}

	public boolean isTooltipEnabled(int mx, int my) {
		return isHovering(); // && isInBounds(mx, my);
	}

	public List<? extends ITextProperties> getTooltip() {
		return new ArrayList<ITextComponent>();
	}

	/**
	 * @return true if {@code this} is the top-most element with the mouse over top of it
	 */
	public boolean isHovering() {
		return isHovering;
	}

	public Optional<FlowComponent> getElementUnderMouse(int mx, int my) {
		return isVisible()
			&& isEnabled()
			&& isInBounds(mx, my)
			? Optional.of(this)
			: Optional.empty();
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean value) {
		this.visible = value;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	/**
	 * Key press handler
	 *
	 * @return consume event
	 */
	public boolean keyPressed(int keyCode, int scanCode, int modifiers, int mx, int my) {
		return false;
	}

	/**
	 * Key press handler
	 *
	 * @return consume event
	 */
	public boolean keyReleased(int keyCode, int scanCode, int modifiers, int mx, int my) {
		return false;
	}

	/**
	 * Mouse scroll handler
	 *
	 * @param mx     Scaled mouse X coordinate
	 * @param my     Scaled mouse Y coordinate
	 * @param scroll Scroll amount
	 * @return consume event
	 */
	public boolean mouseScrolled(int mx, int my, double scroll) {
		return false;
	}

	/**
	 * Keyboard character typed
	 *
	 * @param codePoint Key typed
	 * @param modifiers modifiers?
	 * @param mx        Scaled mouse X coordinate
	 * @param my        Scaled mouse Y coordinate
	 * @return consume event
	 */
	public boolean charTyped(char codePoint, int modifiers, int mx, int my) {
		return false;
	}

	/**
	 * Fired every screen tick
	 */
	public void tick() {
	}

	public Position snapToEdge(Position outside) {
		return new Position(
			MathHelper.clamp(
				outside.getX(),
				getPosition().getX(),
				getPosition().getX() + getSize().getWidth()
			),
			MathHelper.clamp(
				outside.getY(),
				getPosition().getY(),
				getPosition().getY() + getSize().getHeight()
			)
		);
	}

	/**
	 * Used to determine order of rendering. Lower number means rendered earlier, i.e., on bottom
	 * Default layer is 0
	 *
	 * @return Render layer
	 */
	public int getZIndex() {
		return 0;
	}

	/**
	 * Fired each render tick
	 *
	 * @param mx        Scaled mouse x coordinate
	 * @param my        Scaled mouse y coordinate
	 * @param deltaTime Time elapsed since last draw
	 */
	public void drawGhost(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {

	}

	public boolean mouseMoved(int mx, int my, boolean consumed) {
		isHovering = !consumed && isInBounds(mx, my);
		return isHovering;
	}
}
