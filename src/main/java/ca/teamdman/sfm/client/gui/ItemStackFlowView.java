package ca.teamdman.sfm.client.gui;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import ca.teamdman.sfm.client.gui.core.FlowPositionBox;
import ca.teamdman.sfm.client.gui.core.Position;
import ca.teamdman.sfm.client.gui.core.Size;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class ItemStackFlowView extends FlowPositionBox implements IFlowView {
	private ItemStack stack = new ItemStack(Blocks.PUMPKIN);

	public ItemStackFlowView(ItemStack stack, Position pos, Size size) {
		super(pos, size);
	}

	@Override
	public void draw(BaseScreen screen, MatrixStack matrixStack, int mx,
		int my, float deltaTime) {
		RenderHelper.disableStandardItemLighting();
		RenderHelper.enableStandardItemLighting();
		screen.getItemRenderer().renderItemAndEffectIntoGUI(stack, 25, 25);
		RenderHelper.enableStandardItemLighting();

	}

}
