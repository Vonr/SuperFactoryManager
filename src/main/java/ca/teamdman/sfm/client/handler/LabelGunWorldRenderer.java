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
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collection;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
/**
 * This class uses code from tasgon's "observable" mod, also using MPLv2
 * https://github.com/tasgon/observable/blob/master/common/src/main/kotlin/observable/client/Overlay.kt
 */
public class LabelGunWorldRenderer {
    private static final int        BUFFER_SIZE = 256;
    private static final RenderType RENDER_TYPE = RenderType.create(
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

    @SubscribeEvent
    public static void renderLabelHighlights(RenderLevelLastEvent event) {
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
            for (var entry : labelPositions.asMap().entrySet()) {
                drawLabel(poseStack, camera, entry.getKey(), bufferSource, entry.getValue());
            }
        }
        { // draw highlights
            RENDER_TYPE.setupRenderState();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            RenderSystem.disableTexture();
            var vbo = createVBO(new PoseStack(), camera, labelPositions.keySet());
            vbo.drawWithShader(
                    poseStack.last().pose(),
                    event.getProjectionMatrix(),
                    GameRenderer.getPositionColorShader()
            );
            RENDER_TYPE.clearRenderState();
            RenderSystem.enableTexture();
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
                    true,
                    0,
                    0xF000F0
            );
            poseStack.translate(0, font.lineHeight + 0.1, 0);
        }
        poseStack.popPose();
    }

    private static VertexBuffer createVBO(PoseStack poseStack, Camera camera, Collection<BlockPos> positions) {
        var builder = new BufferBuilder(RENDER_TYPE.bufferSize() * positions.size());
        builder.begin(RENDER_TYPE.mode(), RENDER_TYPE.format());
        for (var pos : positions) {
            drawBlockOutline(pos, poseStack, builder, 100, 0, 255, 100);
        }
        builder.end();
        var vert = new VertexBuffer();
        vert.upload(builder);
        return vert;
    }

    private static void drawBlockOutline(
            BlockPos pos,
            PoseStack poseStack,
            VertexConsumer buf,
            int r,
            int g,
            int b,
            int a
    ) {
        poseStack.pushPose();
        poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
        var mat = poseStack
                .last()
                .pose();

        buf.vertex(mat, 0F, 1F, 0F).color(r, g, b, a).endVertex();
        buf.vertex(mat, 0F, 1F, 1F).color(r, g, b, a).endVertex();
        buf.vertex(mat, 1F, 1F, 1F).color(r, g, b, a).endVertex();
        buf.vertex(mat, 1F, 1F, 0F).color(r, g, b, a).endVertex();

        buf.vertex(mat, 0F, 1F, 0F).color(r, g, b, a).endVertex();
        buf.vertex(mat, 1F, 1F, 0F).color(r, g, b, a).endVertex();
        buf.vertex(mat, 1F, 0F, 0F).color(r, g, b, a).endVertex();
        buf.vertex(mat, 0F, 0F, 0F).color(r, g, b, a).endVertex();

        buf.vertex(mat, 1F, 1F, 1F).color(r, g, b, a).endVertex();
        buf.vertex(mat, 0F, 1F, 1F).color(r, g, b, a).endVertex();
        buf.vertex(mat, 0F, 0F, 1F).color(r, g, b, a).endVertex();
        buf.vertex(mat, 1F, 0F, 1F).color(r, g, b, a).endVertex();

        buf.vertex(mat, 0F, 1F, 1F).color(r, g, b, a).endVertex();
        buf.vertex(mat, 0F, 1F, 0F).color(r, g, b, a).endVertex();
        buf.vertex(mat, 0F, 0F, 0F).color(r, g, b, a).endVertex();
        buf.vertex(mat, 0F, 0F, 1F).color(r, g, b, a).endVertex();

        buf.vertex(mat, 1F, 0F, 1F).color(r, g, b, a).endVertex();
        buf.vertex(mat, 1F, 0F, 0F).color(r, g, b, a).endVertex();
        buf.vertex(mat, 1F, 1F, 0F).color(r, g, b, a).endVertex();
        buf.vertex(mat, 1F, 1F, 1F).color(r, g, b, a).endVertex();

        buf.vertex(mat, 1F, 0F, 0F).color(r, g, b, a).endVertex();
        buf.vertex(mat, 1F, 0F, 1F).color(r, g, b, a).endVertex();
        buf.vertex(mat, 0F, 0F, 1F).color(r, g, b, a).endVertex();
        buf.vertex(mat, 0F, 0F, 0F).color(r, g, b, a).endVertex();

        poseStack.popPose();
    }
}
