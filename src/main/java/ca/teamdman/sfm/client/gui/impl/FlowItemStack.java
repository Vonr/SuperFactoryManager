package ca.teamdman.sfm.client.gui.impl;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.Colour3f;
import ca.teamdman.sfm.client.gui.core.FlowPanel;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowTangible;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import ca.teamdman.sfm.client.gui.core.Size;
import ca.teamdman.sfm.common.flowdata.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.item.ItemStack;

public class FlowItemStack implements IFlowView, IFlowController, IFlowTangible {

	public static final int ITEM_PADDING_X = 4;
	public static final int ITEM_PADDING_Y = 4;
	public static final int ITEM_WIDTH = 20;
	public static final int ITEM_HEIGHT = 20;
	public static final int ITEM_TOTAL_HEIGHT = ITEM_HEIGHT + ITEM_PADDING_Y;
	public static final int ITEM_TOTAL_WIDTH = ITEM_WIDTH + ITEM_PADDING_X;
	private final ItemStack STACK;
	private final FlowPanel HITBOX;

	public FlowItemStack(ItemStack stack, Position pos) {
		this.STACK = stack;
		this.HITBOX = new FlowPanel(
			pos,
			new Size(ITEM_TOTAL_WIDTH, ITEM_TOTAL_HEIGHT)
		);
	}

	public ItemStack getItemStack() {
		return STACK;
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		screen.drawRect(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			ITEM_TOTAL_WIDTH,
			ITEM_TOTAL_HEIGHT,
			Colour3f.HIGHLIGHT
		);
		screen.drawItemStack(
			matrixStack,
			STACK,
			getPosition().getX() + ITEM_PADDING_X / 2,
			getPosition().getY() + ITEM_PADDING_Y / 2
		);
//		Minecraft.getInstance().getBlockRendererDispatcher().renderBlock();
	}

	@Override
	public Position getPosition() {
		return HITBOX.getPosition();
	}

	@Override
	public Size getSize() {
		return HITBOX.getSize();
	}
}
