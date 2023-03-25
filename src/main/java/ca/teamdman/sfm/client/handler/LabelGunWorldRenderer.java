package ca.teamdman.sfm.client.handler;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.item.LabelGunItem;
import ca.teamdman.sfm.common.util.SFMLabelNBTHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
/**
 * This class uses code from tasgon's "observable" mod, also using MPLv2
 * https://github.com/tasgon/observable/blob/master/common/src/main/kotlin/observable/client/Overlay.kt
 */
public class LabelGunWorldRenderer {
    private static final int          BUFFER_SIZE = 256;
    private static final RenderType   RENDER_TYPE = RenderType.create(
            "sfmlabels",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            BUFFER_SIZE,
            false,
            false,
            RenderType.CompositeState
                    .builder()
                    .setTextureState(new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, false, false))
                    .setDepthTestState(new RenderStateShard.DepthTestStateShard("always", 519))
                    .setTransparencyState(
                            new RenderStateShard.TransparencyStateShard(
                                    "src_to_one",
                                    () -> {
                                        RenderSystem.enableBlend();
                                        RenderSystem.blendFunc(
                                                GlStateManager.SourceFactor.SRC_ALPHA,
                                                GlStateManager.DestFactor.ONE
                                        );
                                    },
                                    () -> {
                                        RenderSystem.disableBlend();
                                        RenderSystem.defaultBlendFunc();
                                    }
                            )
                    )
                    .createCompositeState(true)
    );
    @Nullable
    private static       VertexBuffer vbo;

    @SubscribeEvent
    public static void renderLabelHighlights(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        var labelGun = player.getMainHandItem();
        if (!(labelGun.getItem() instanceof LabelGunItem)) labelGun = player.getOffhandItem();
        if (!(labelGun.getItem() instanceof LabelGunItem)) return;
        var playerPosition = player.position();
        var labelPositions = SFMLabelNBTHelper.getPositionLabels(labelGun);

        var poseStack    = event.getPoseStack();
        var camera       = Minecraft.getInstance().gameRenderer.getMainCamera();
        var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        RenderSystem.disableDepthTest();

        poseStack.pushPose();
        poseStack.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);

        { // draw labels
//            RenderSystem.depthMask(false);
            for (var entry : labelPositions.asMap().entrySet()) {
                drawLabel(poseStack, camera, entry.getKey(), bufferSource, entry.getValue());
            }
        }
        { // draw highlights
            RENDER_TYPE.setupRenderState();

            if (vbo == null) {
                vbo = new VertexBuffer();
                vbo.bind();
                vbo.upload(createShape());
            } else {
                vbo.bind();
            }

            for (var pos : labelPositions.keySet()) {
                poseStack.pushPose();
                poseStack.translate(pos.getX(), pos.getY(), pos.getZ());

                vbo.drawWithShader(
                        poseStack.last().pose(),
                        event.getProjectionMatrix(),
                        GameRenderer.getPositionColorShader()
                );
                poseStack.popPose();
            }

            VertexBuffer.unbind();
            RENDER_TYPE.clearRenderState();
        }
        bufferSource.endBatch();
        poseStack.popPose();
        RenderSystem.enableDepthTest();
    }

    private static void drawLabel(
            PoseStack poseStack,
            Camera camera,
            BlockPos pos,
            MultiBufferSource mbs,
            Collection<String> labels
    ) {
        poseStack.pushPose();
        poseStack.translate(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
//        poseStack.translate(0,1,0);
        poseStack.mulPose(camera.rotation());
        poseStack.scale(-0.025f, -0.025f, 0.025f);
        Font font = Minecraft.getInstance().font;
        poseStack.translate(0, labels.size() * (font.lineHeight + 0.1) / -2f, 0);
        for (var label : labels) {
            font.drawInBatch(
                    label,
                    -font.width(label) / 2f,
                    0,
                    -0x1,
                    false,
                    poseStack.last().pose(),
                    mbs,
                    Font.DisplayMode.SEE_THROUGH,
                    0,
                    0xF000F0
            );
            poseStack.translate(0, font.lineHeight + 0.1, 0);
        }
        poseStack.popPose();
    }

    public static BufferBuilder.RenderedBuffer createShape() {
        var builder = new BufferBuilder(4 * 6 * 8);
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        int r = 100;
        int g = 0;
        int b = 255;
        int a = 100;

        builder.vertex(0F, 1F, 0F).color(r, g, b, a).endVertex();
        builder.vertex(0F, 1F, 1F).color(r, g, b, a).endVertex();
        builder.vertex(1F, 1F, 1F).color(r, g, b, a).endVertex();
        builder.vertex(1F, 1F, 0F).color(r, g, b, a).endVertex();

        builder.vertex(0F, 1F, 0F).color(r, g, b, a).endVertex();
        builder.vertex(1F, 1F, 0F).color(r, g, b, a).endVertex();
        builder.vertex(1F, 0F, 0F).color(r, g, b, a).endVertex();
        builder.vertex(0F, 0F, 0F).color(r, g, b, a).endVertex();

        builder.vertex(1F, 1F, 1F).color(r, g, b, a).endVertex();
        builder.vertex(0F, 1F, 1F).color(r, g, b, a).endVertex();
        builder.vertex(0F, 0F, 1F).color(r, g, b, a).endVertex();
        builder.vertex(1F, 0F, 1F).color(r, g, b, a).endVertex();

        builder.vertex(0F, 1F, 1F).color(r, g, b, a).endVertex();
        builder.vertex(0F, 1F, 0F).color(r, g, b, a).endVertex();
        builder.vertex(0F, 0F, 0F).color(r, g, b, a).endVertex();
        builder.vertex(0F, 0F, 1F).color(r, g, b, a).endVertex();

        builder.vertex(1F, 0F, 1F).color(r, g, b, a).endVertex();
        builder.vertex(1F, 0F, 0F).color(r, g, b, a).endVertex();
        builder.vertex(1F, 1F, 0F).color(r, g, b, a).endVertex();
        builder.vertex(1F, 1F, 1F).color(r, g, b, a).endVertex();

        builder.vertex(1F, 0F, 0F).color(r, g, b, a).endVertex();
        builder.vertex(1F, 0F, 1F).color(r, g, b, a).endVertex();
        builder.vertex(0F, 0F, 1F).color(r, g, b, a).endVertex();
        builder.vertex(0F, 0F, 0F).color(r, g, b, a).endVertex();

        return builder.end();
    }

}
