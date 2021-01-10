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
import java.util.List;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextProperties;

public class ItemStackFlowComponent extends FlowButton implements ISelectable {
	public static final Size DEFAULT_SIZE = new Size(20, 20);
	private ItemStack STACK;
	private boolean selected;
	private boolean selectable = true;
	private boolean depressed = false;

	public ItemStackFlowComponent(ItemStack stack, Position pos) {
		super(pos, DEFAULT_SIZE.copy());
		this.STACK = stack;
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		toggleSelected();
		onSelectionChanged();
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		if (selected) {
			drawBackground(screen, matrixStack, CONST.SELECTED);
		} else if (isHovering()) {
			drawBackground(screen, matrixStack, CONST.HIGHLIGHT);
		}
		screen.drawItemStack(
			matrixStack,
			STACK,
			getPosition().getX() + 2,
			getPosition().getY() + 2
		);
		super.draw(screen, matrixStack, mx, my, deltaTime);
	}

	@Override
	public List<? extends ITextProperties> getTooltip() {
		return getItemStack().getTooltip(null, TooltipFlags.ADVANCED);
	}

	public ItemStack getItemStack() {
		return STACK;
	}

	public void setItemStack(ItemStack stack) {
		this.STACK = stack;
	}

	protected void drawBackground(BaseScreen screen, MatrixStack matrixStack, Colour3f colour) {
		screen.drawRect(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getHeight(),
			colour
		);
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

	public boolean isSelectable() {
		return selectable;
	}

	public void setSelectable(boolean selectable) {
		this.selectable = selectable;
	}

	@Override
	public void onSelectionChanged() {

	}
}
