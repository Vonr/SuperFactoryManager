/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.ISelectable;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.common.flow.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;

public class FlowItemStack extends FlowButton implements ISelectable {
	public static final int ITEM_PADDING_X = 4;
	public static final int ITEM_PADDING_Y = 4;
	public static final int ITEM_WIDTH = 16;
	public static final int ITEM_HEIGHT = 16;
	public static final int ITEM_TOTAL_HEIGHT = ITEM_HEIGHT + ITEM_PADDING_Y;
	public static final int ITEM_TOTAL_WIDTH = ITEM_WIDTH + ITEM_PADDING_X;
	public static final Size ITEM_TOTAL_SIZE = new Size(ITEM_WIDTH, ITEM_HEIGHT);
	private ItemStack STACK;
	private boolean selected;
	private boolean selectable = true;
	private boolean depressed = false;

	public FlowItemStack(ItemStack stack, Position pos) {
		super(pos, ITEM_TOTAL_SIZE.copy());
		setDraggable(false);
		this.STACK = stack;
	}

	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	public ItemStack getItemStack() {
		return STACK;
	}

	@Override
	public void onSelectionChanged() {

	}

	@Override
	public void onClicked(int mx, int my, int button) {
		toggleSelected();
		onSelectionChanged();
	}

	protected void drawBackground(BaseScreen screen, MatrixStack matrixStack, Colour3f colour) {
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
			drawBackground(screen, matrixStack, CONST.SELECTED);
		} else if (isInBounds(mx, my)) {
			drawBackground(screen, matrixStack, CONST.HIGHLIGHT);
		}
		screen.drawItemStack(
			matrixStack,
			STACK,
			getPosition().getX() + ITEM_PADDING_X / 2,
			getPosition().getY() + ITEM_PADDING_Y / 2
		);
		drawTooltip(screen, matrixStack, mx, my, deltaTime);
	}

	@Override
	public boolean isSelected() {
		return this.selected;
	}

	@Override
	public void setSelected(boolean value) {
		if (isSelectable()) {
			this.selected = value;
		}
	}
}
