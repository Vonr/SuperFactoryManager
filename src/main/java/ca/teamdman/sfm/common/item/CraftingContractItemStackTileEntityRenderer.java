package ca.teamdman.sfm.common.item;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.math.vector.Vector3f;

public class CraftingContractItemStackTileEntityRenderer extends
	ItemStackTileEntityRenderer {

	private static final ItemStack MISSING_OUTPUT_STACK = new ItemStack(Blocks.BARRIER);

	// prevent recursion
	public static boolean debounce = false;

	@Override
	public void func_239207_a_(
		ItemStack stack,
		TransformType p_239207_2_,
		MatrixStack matrixStack,
		IRenderTypeBuffer buffer,
		int combinedLight,
		int combinedOverlay
	) {
		debounce = true;
		try {
			if (!(stack.getItem() instanceof CraftingContractItem)) return;
			if (Minecraft.getInstance().world == null) return;

			ItemStack result = CraftingContractItem.getRecipe(
				stack,
				Minecraft.getInstance().world
			)
				.map(IRecipe::getRecipeOutput)
				.orElse(MISSING_OUTPUT_STACK);

			ItemStack primary = stack;
			ItemStack secondary = result;
			if (Screen.hasShiftDown()) {
				primary = result;
				secondary = stack;
			}
			matrixStack.push();
			matrixStack.translate(0.5,0.5,0);
			matrixStack.rotate((new Vector3f(0,1,0)).rotationDegrees(180));
			Minecraft.getInstance().getItemRenderer().renderItem(
				primary,
				TransformType.FIXED,
				combinedLight,
				combinedOverlay,
				matrixStack,
				buffer
			);
			matrixStack.translate(-0.3,-0.3,0.1);
			matrixStack.scale(0.7f,0.7f,1);
			Minecraft.getInstance().getItemRenderer().renderItem(
				secondary,
				TransformType.FIXED,
				combinedLight,
				combinedOverlay,
				matrixStack,
				buffer
			);
			matrixStack.pop();
		} finally {
			debounce = false;
		}
	}
}
