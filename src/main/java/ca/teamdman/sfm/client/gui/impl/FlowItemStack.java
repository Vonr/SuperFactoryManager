package ca.teamdman.sfm.client.gui.impl;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;

public class FlowItemStack implements IFlowView {

	private final ItemStack STACK;

	public FlowItemStack(ItemStack stack) {
		this.STACK = stack;
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		Minecraft.getInstance().getItemRenderer()
			.renderItem(
				this.STACK,
				ItemCameraTransforms.TransformType.FIXED,
				15728880,
				OverlayTexture.NO_OVERLAY,
				matrixStack,
				IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer())
			);
//		Minecraft.getInstance().getBlockRendererDispatcher().renderBlock();
	}
}
