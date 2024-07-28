package ca.teamdman.sfm.client.handler;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.item.LabelGunItem;
import ca.teamdman.sfm.common.item.NetworkToolItem;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import ca.teamdman.sfm.common.registry.SFMDataComponents;
import com.google.common.collect.HashMultimap;
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
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber(modid = SFM.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
/*
 * This class uses code from tasgon's "observable" mod, also using MPLv2
 * https://github.com/tasgon/observable/blob/master/common/src/main/kotlin/observable/client/Overlay.kt
 */
public class ItemWorldRenderer {
    private static final int BUFFER_SIZE = 256;
    @SuppressWarnings("deprecation")
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
    @Nullable
    private static VertexBuffer capabilityProviderVBO;
    @Nullable
    private static VertexBuffer cableVBO;

    @SubscribeEvent
    public static void renderLabelHighlights(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;
        var player = Minecraft.getInstance().player;
        if (player == null) return;

        var labelGun = player.getMainHandItem();
        if (!(labelGun.getItem() instanceof LabelGunItem)) labelGun = player.getOffhandItem();
        if (labelGun.getItem() instanceof LabelGunItem) {
            var labels = LabelPositionHolder.from(labelGun);
            var labelPositions = HashMultimap.<BlockPos, String>create();
            labels.forEach((label, pos1) -> labelPositions.put(pos1, label));

            var poseStack = event.getPoseStack();
            var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

            RenderSystem.disableDepthTest();

            poseStack.pushPose();
            poseStack.mulPose(camera.rotation().invert());
            poseStack.translate(-camera.getPosition().x, -camera.getPosition().y, -camera.getPosition().z);

            { // draw labels
                poseStack.pushPose();
//                poseStack.mulPose(camera.rotation());
//                poseStack.mulPose(camera.rotation().invert());

                for (var entry : labelPositions.asMap().entrySet()) {
                    drawLabel(poseStack, camera, entry.getKey(), bufferSource, entry.getValue());
                }
                poseStack.popPose();
            }
            { // draw highlights
                RENDER_TYPE.setupRenderState();

                if (capabilityProviderVBO == null) {
                    capabilityProviderVBO = new VertexBuffer(VertexBuffer.Usage.STATIC);
                    capabilityProviderVBO.bind();
                    capabilityProviderVBO.upload(createCapabilityProviderVBO());
                } else {
                    capabilityProviderVBO.bind();
                }

                for (var pos : labelPositions.keySet()) {
                    poseStack.pushPose();
                    poseStack.translate(pos.getX(), pos.getY(), pos.getZ());

                    //noinspection DataFlowIssue
                    capabilityProviderVBO.drawWithShader(
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

        var networkTool = player.getMainHandItem();
        if (!(networkTool.getItem() instanceof NetworkToolItem)) networkTool = player.getOffhandItem();
        if (networkTool.getItem() instanceof NetworkToolItem) {
            Set<BlockPos> cablePositions = networkTool.getOrDefault(SFMDataComponents.CABLE_POSITIONS, new HashSet<>());
            Set<BlockPos> capabilityProviderPositions = networkTool.getOrDefault(
                    SFMDataComponents.CAPABILITY_POSITIONS,
                    new HashSet<>()
            );

            var poseStack = event.getPoseStack();
            var camera = Minecraft.getInstance().gameRenderer.getMainCamera();
            var bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

            RenderSystem.disableDepthTest();

            poseStack.pushPose();
            poseStack.translate(
                    -camera.getPosition().x,
                    -camera.getPosition().y,
                    -camera.getPosition().z
            );
            poseStack.mulPose(camera.rotation().invert());

            { // draw highlights
                RENDER_TYPE.setupRenderState();

                if (capabilityProviderVBO == null) {
                    capabilityProviderVBO = new VertexBuffer(VertexBuffer.Usage.STATIC);
                    capabilityProviderVBO.bind();
                    capabilityProviderVBO.upload(createCapabilityProviderVBO());
                } else {
                    capabilityProviderVBO.bind();
                }

                for (var blockPos : capabilityProviderPositions) {
                    poseStack.pushPose();
                    poseStack.translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());

                    //noinspection DataFlowIssue
                    capabilityProviderVBO.drawWithShader(
                            poseStack.last().pose(),
                            event.getProjectionMatrix(),
                            GameRenderer.getPositionColorShader()
                    );
                    poseStack.popPose();
                }


                if (cableVBO == null) {
                    cableVBO = new VertexBuffer(VertexBuffer.Usage.STATIC);
                    cableVBO.bind();
                    cableVBO.upload(createCableVBO());
                } else {
                    cableVBO.bind();
                }

                for (var blockPos : cablePositions) {
                    poseStack.pushPose();
                    poseStack.translate(blockPos.getX(), blockPos.getY(), blockPos.getZ());

                    //noinspection DataFlowIssue
                    cableVBO.drawWithShader(
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
    }

    public static MeshData createCapabilityProviderVBO() {
        return createShape(100, 0, 255, 100);
    }

    public static MeshData createCableVBO() {
        return createShape(100, 255, 0, 100);
    }

    public static MeshData createShape(
            int r,
            int g,
            int b,
            int a
    ) {
        ByteBufferBuilder byteBufferBuilder = new ByteBufferBuilder(4 * 6 * 8);
        BufferBuilder builder = new BufferBuilder(
                byteBufferBuilder,
                VertexFormat.Mode.QUADS,
                DefaultVertexFormat.POSITION_COLOR
        );

        builder.addVertex(0F, 1F, 0F).setColor(r, g, b, a);
        builder.addVertex(0F, 1F, 1F).setColor(r, g, b, a);
        builder.addVertex(1F, 1F, 1F).setColor(r, g, b, a);
        builder.addVertex(1F, 1F, 0F).setColor(r, g, b, a);

        builder.addVertex(0F, 1F, 0F).setColor(r, g, b, a);
        builder.addVertex(1F, 1F, 0F).setColor(r, g, b, a);
        builder.addVertex(1F, 0F, 0F).setColor(r, g, b, a);
        builder.addVertex(0F, 0F, 0F).setColor(r, g, b, a);

        builder.addVertex(1F, 1F, 1F).setColor(r, g, b, a);
        builder.addVertex(0F, 1F, 1F).setColor(r, g, b, a);
        builder.addVertex(0F, 0F, 1F).setColor(r, g, b, a);
        builder.addVertex(1F, 0F, 1F).setColor(r, g, b, a);

        builder.addVertex(0F, 1F, 1F).setColor(r, g, b, a);
        builder.addVertex(0F, 1F, 0F).setColor(r, g, b, a);
        builder.addVertex(0F, 0F, 0F).setColor(r, g, b, a);
        builder.addVertex(0F, 0F, 1F).setColor(r, g, b, a);

        builder.addVertex(1F, 0F, 1F).setColor(r, g, b, a);
        builder.addVertex(1F, 0F, 0F).setColor(r, g, b, a);
        builder.addVertex(1F, 1F, 0F).setColor(r, g, b, a);
        builder.addVertex(1F, 1F, 1F).setColor(r, g, b, a);

        builder.addVertex(1F, 0F, 0F).setColor(r, g, b, a);
        builder.addVertex(1F, 0F, 1F).setColor(r, g, b, a);
        builder.addVertex(0F, 0F, 1F).setColor(r, g, b, a);
        builder.addVertex(0F, 0F, 0F).setColor(r, g, b, a);

        return builder.buildOrThrow();
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
        poseStack.mulPose(camera.rotation().invert());
//        poseStack.mulPose(camera.rotation().);
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

}
