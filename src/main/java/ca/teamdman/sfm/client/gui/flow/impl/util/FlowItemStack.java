/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.IFlowView;
import ca.teamdman.sfm.client.gui.flow.core.ISelectable;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.common.flow.data.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;

public class FlowItemStack extends FlowComponent implements ISelectable {

	public static final int ITEM_PADDING_X = 4;
	public static final int ITEM_PADDING_Y = 4;
	public static final int ITEM_WIDTH = 16;
	public static final int ITEM_HEIGHT = 16;
	public static final int ITEM_TOTAL_HEIGHT = ITEM_HEIGHT + ITEM_PADDING_Y;
	public static final int ITEM_TOTAL_WIDTH = ITEM_WIDTH + ITEM_PADDING_X;
	private final ItemStack STACK;
	private boolean selected;
	private boolean depressed = false;


	public FlowItemStack(ItemStack stack, Position pos) {
		super(pos, new Size(ITEM_TOTAL_WIDTH, ITEM_TOTAL_HEIGHT));
		this.STACK = stack;
	}

	public ItemStack getItemStack() {
		return STACK;
	}


	@Override
	public IFlowView getView() {
		return this;
	}


	@Override
	public boolean mousePressed(int mx, int my, int button) {
		if (isInBounds(mx, my)) {
			depressed = true;
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseReleased(int mx, int my, int button) {
		boolean check = depressed;
		depressed = false;
		if (isInBounds(mx, my) && check) {
			toggleSelected(true);
			return true;
		}
		return false;
	}

	private void drawSquare(BaseScreen screen, MatrixStack matrixStack, Colour3f colour) {
		screen.drawRect(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			ITEM_TOTAL_WIDTH,
			ITEM_TOTAL_HEIGHT,
			colour
		);
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		if (selected) {
			drawSquare(screen, matrixStack, CONST.SELECTED);
		} else if (isInBounds(mx, my)) {
			drawSquare(screen, matrixStack, CONST.HIGHLIGHT);
		}
		screen.drawItemStack(
			matrixStack,
			STACK,
			getPosition().getX() + ITEM_PADDING_X / 2,
			getPosition().getY() + ITEM_PADDING_Y / 2
		);
//		Minecraft.getInstance().getBlockRendererDispatcher().renderBlock();
	}

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean value, boolean notify) {
		this.selected = value;
	}
}
