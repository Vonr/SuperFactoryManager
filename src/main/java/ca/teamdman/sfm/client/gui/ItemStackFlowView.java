package ca.teamdman.sfm.client.gui;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import ca.teamdman.sfm.client.gui.impl.FlowRepositionable;
import ca.teamdman.sfm.client.gui.impl.Position;
import ca.teamdman.sfm.client.gui.impl.Size;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

public class ItemStackFlowView extends FlowRepositionable implements IFlowView {
	private ItemStack stack = new ItemStack(Blocks.PUMPKIN);

	public ItemStackFlowView(ItemStack stack, Position pos, Size size) {
		super(pos, size);
	}

	@Override
	public void draw(BaseScreen screen, int mx, int my, float deltaTime) {
		RenderHelper.disableStandardItemLighting();
		RenderHelper.enableGUIStandardItemLighting();
		screen.getItemRenderer().renderItemAndEffectIntoGUI(stack, 25, 25);
		RenderHelper.enableStandardItemLighting();

	}

}
