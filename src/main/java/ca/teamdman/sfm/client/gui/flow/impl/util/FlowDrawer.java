/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.IFlowView;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.common.flow.data.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class FlowDrawer extends FlowContainer {

	private static final int PADDING_X = 4;
	private static final int PADDING_Y = 4;
	private static final int ITEM_MARGIN_X = 4;
	private static final int ITEM_MARGIN_Y = 4;

	private final int ITEM_WIDTH;
	private final int ITEM_HEIGHT;
	private int scroll = 0;

	public FlowDrawer(Position pos, int itemWidth, int itemHeight) {
		super(pos, new Size(0, 0));
		this.ITEM_WIDTH = itemWidth;
		this.ITEM_HEIGHT = itemHeight;
	}


	public void update() {
		getSize().setSize(
			(ITEM_WIDTH + ITEM_MARGIN_X) * getItemsPerRow() + PADDING_X,
			(ITEM_HEIGHT + ITEM_MARGIN_Y) * getItemsPerColumn() + PADDING_Y
		);
		AtomicInteger i = new AtomicInteger();
		getChildren().stream().forEachOrdered(c -> c.getPosition().setXY(
			getWrappedX(i.get()),
			getWrappedY(i.getAndIncrement()) - scroll
		));
	}

	public void fixScroll() {
		this.scroll = MathHelper.clamp(
			this.scroll,
			0,
			Math.max(
				0,
				(
					(int) Math.ceil(getChildren().size() / (float) getItemsPerRow())
						- getItemsPerColumn()
				) * (ITEM_HEIGHT + ITEM_MARGIN_Y)
			)
		);
	}

	@Override
	public boolean mouseScrolled(int mx, int my, double scroll) {
		if (isInBounds(mx, my)) {
			if (scroll > 0) {
				scrollUp();
			} else {
				scrollDown();
			}
			return true;
		} else {
			return false;
		}
	}

	public void scrollDown() {
		scroll += 7;
		fixScroll();
		update();
	}

	public void scrollUp() {
		scroll -= 7;
		fixScroll();
		update();
	}


	public int getItemsPerColumn() {
		return MathHelper.clamp(
			getItemRow(getChildren().size() - 1) + 1,
			1,
			7
		);
	}

	public int getItemsPerRow() {
		return MathHelper.clamp(getChildren().size(), 1, 5);
	}

	public int getWrappedX(int index) {
		return getItemColumn(index)
			* (FlowItemStack.ITEM_TOTAL_WIDTH + ITEM_MARGIN_X)
			+ ITEM_MARGIN_X / 2
			+ PADDING_X / 2;
	}

	public int getItemColumn(int index) {
		return index % getItemsPerRow();
	}

	public int getItemRow(int index) {
		return (int) Math.floor(index / (float) getItemsPerRow());
	}

	public int getWrappedY(int index) {
		return getItemRow(index)
			* (FlowItemStack.ITEM_TOTAL_HEIGHT + ITEM_MARGIN_Y)
			+ ITEM_MARGIN_Y / 2
			+ PADDING_Y / 2;
	}

	@Override
	public IFlowView getView() {
		return this;
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		screen.clearRect(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getHeight()
		);
		drawBackground(screen, matrixStack);

		// Enable clipping for drawer contents
		// +2 -4 for border padding
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		screen.scissorRect(
			matrixStack,
			getPosition().getX() + 2,
			getPosition().getY() + 2,
			getSize().getWidth() - 4,
			getSize().getHeight() - 4
		);
		super.draw(screen, matrixStack, mx, my, deltaTime);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);

		screen.drawBorder(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getHeight(),
			2,
			CONST.PANEL_BORDER
		);
	}
}
