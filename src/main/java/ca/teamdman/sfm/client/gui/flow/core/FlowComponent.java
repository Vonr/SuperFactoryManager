/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.core;

import ca.teamdman.sfm.common.config.Config.Client;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.core.SizeHolder;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;

public class FlowComponent implements SizeHolder {

	private final Position dragStart = new Position();
	private final Position dragOffset = new Position();
	private boolean visible = true;
	private boolean enabled = true;
	private boolean draggable = false;
	private boolean dragging = false;
	private boolean hovering = false;
	private Position position;
	private Size size;

	public FlowComponent() {
		this(new Position(), new Size(0,0));
	}

	public FlowComponent(Position pos, Size size) {
		this.position = pos;
		this.size = size;
	}

	public Position getCentroid() {
		return getPosition().withOffset(getSize().getWidth() / 2, getSize().getHeight() / 2);
	}

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
			dragging = true;
			dragStart.setXY(mx, my);
			dragOffset.setXY(mx - getPosition().getX(), my - getPosition().getY());
			onDragStarted(mx, my);
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
		if (dragging) {
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
			int dx = getPosition().getX() - dragStart.getX() + dragOffset.getX();
			int dy = getPosition().getY() - dragStart.getY() + dragOffset.getY();
			getPosition().setXY(newX, newY);
			onDrag(dx, dy, mx, my);
			return true;
		} else {
			return false;
		}
	}

	public void onDrag(int dx, int dy, int mx, int my) {

	}

	public void onDragStarted(int mx, int my) {

	}

	public boolean mouseReleased(int mx, int my, int button) {
		if (dragging) {
			dragging = false;
			if (!dragStart.equals(getPosition())) {
				onDragFinished(
					getPosition().getX() - dragStart.getX() + dragOffset.getX(),
					getPosition().getY() - dragStart.getY() + dragOffset.getY(),
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

	/**
	 * Draw the content of this component
	 */
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
	}

	/**
	 * Draw the tooltip for this component.
	 * Called after {@code draw} so that tooltip is on top
	 */
	public void drawTooltip(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		if (isTooltipEnabled(mx, my)) {
			// Disable scissoring when drawing tooltip
			screen.pauseScissor();

			// get current transformation matrix
			Matrix4f mat = matrixStack.getLast().getMatrix().copy();

			// get vector representing pos to draw
			Vector4f pos = new Vector4f(mx, my, 0, 1);

			// move pos from matstack-space to screen-space
			// (to account for current transformation)
//			mat.invert();
			pos.transform(mat);

			// draw tooltip
			net.minecraftforge.fml.client.gui.GuiUtils.drawHoveringText(
				new MatrixStack(),
				getTooltip(),
				(int) pos.getX(),
				(int) pos.getY(),
				screen.width,
				screen.height,
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
		return hovering;
	}

	public Stream<? extends FlowComponent> getElementsUnderMouse(int mx, int my) {
		return isElementUnderMouse(mx, my)
			? Stream.of(this)
			: Stream.empty();
	}

	public boolean isElementUnderMouse(int mx, int my) {
		return isVisible()
			&& isEnabled()
			&& isInBounds(mx, my);
	}

	public boolean isVisible() {
		return this.visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setVisibleAndEnabled(boolean visible) {
		setVisible(visible);
		setEnabled(visible);
	}

	public void toggleVisibilityAndEnabled() {
		setVisible(!isVisible());
		setEnabled(isVisible());
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
		hovering = !consumed && isInBounds(mx, my);
		return hovering;
	}
}
