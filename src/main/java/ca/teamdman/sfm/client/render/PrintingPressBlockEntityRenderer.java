package ca.teamdman.sfm.client.render;

import ca.teamdman.sfm.common.blockentity.PrintingPressBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class PrintingPressBlockEntityRenderer implements BlockEntityRenderer<PrintingPressBlockEntity> {
    public PrintingPressBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

    }

    @Override
    public void render(
            PrintingPressBlockEntity blockEntity,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buf,
            int packedLight,
            int packedOverlay
    ) {
        var paper = blockEntity.getPaper();
        var dye = blockEntity.getInk();
        var form = blockEntity.getForm();
        var depthAxis = new Vector3f(1, 0, 0);
        if (!dye.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1.055, 0.6);
            poseStack.mulPose(depthAxis.rotationDegrees(-90));
            Minecraft
                    .getInstance()
                    .getItemRenderer()
                    .renderStatic(
                            dye,
                            ItemTransforms.TransformType.GROUND,
                            packedLight,
                            packedOverlay,
                            poseStack,
                            buf,
                            (int) blockEntity.getBlockPos().asLong()
                    );
            poseStack.popPose();
        }
        if (!paper.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1.025, 0.6);
            poseStack.mulPose(depthAxis.rotationDegrees(-90));
            Minecraft
                    .getInstance()
                    .getItemRenderer()
                    .renderStatic(
                            paper,
                            ItemTransforms.TransformType.GROUND,
                            packedLight,
                            packedOverlay,
                            poseStack,
                            buf,
                            (int) blockEntity.getBlockPos().asLong()
                    );
            poseStack.popPose();
        }
        if (!form.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5, 1.083, 0.6);
            poseStack.mulPose(depthAxis.rotationDegrees(-90));
            Minecraft
                    .getInstance()
                    .getItemRenderer()
                    .renderStatic(
                            form,
                            ItemTransforms.TransformType.GROUND,
                            packedLight,
                            packedOverlay,
                            poseStack,
                            buf,
                            (int) blockEntity.getBlockPos().asLong()
                    );
            poseStack.popPose();
        }
    }
}
