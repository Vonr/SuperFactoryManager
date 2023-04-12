package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.item.DiskItem;
import ca.teamdman.sfm.common.menu.ManagerMenu;
import ca.teamdman.sfm.common.net.ServerboundManagerFixPacket;
import ca.teamdman.sfm.common.net.ServerboundManagerProgramPacket;
import ca.teamdman.sfm.common.net.ServerboundManagerResetPacket;
import ca.teamdman.sfm.common.registry.SFMPackets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.gui.widget.ExtendedButton;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static ca.teamdman.sfm.common.Constants.LocalizationKeys.*;

public class ManagerScreen extends AbstractContainerScreen<ManagerMenu> {
    private static final ResourceLocation BACKGROUND_TEXTURE_LOCATION = new ResourceLocation(
            SFM.MOD_ID,
            "textures/gui/container/manager.png"
    );
    private final        float            STATUS_DURATION             = 40;
    private              Component        status                      = Component.empty();
    private              float            statusCountdown             = 0;
    private              ExtendedButton   diagButton;


    public ManagerScreen(ManagerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new ExtendedButtonWithTooltip(
                (this.width - this.imageWidth) / 2
                - 24
                - font.width(MANAGER_GUI_BUTTON_IMPORT_CLIPBOARD.getComponent()),
                (this.height - this.imageHeight) / 2 + 16,
                100,
                16,
                MANAGER_GUI_BUTTON_IMPORT_CLIPBOARD.getComponent(),
                button -> this.onLoadClipboard(),
                (btn, pose, mx, my) -> renderTooltip(
                        pose,
                        font.split(
                                MANAGER_GUI_PASTE_BUTTON_TOOLTIP.getComponent(),
                                Math.max(width / 2 - 43, 170)
                        ),
                        mx,
                        my
                )
        ));
        this.addRenderableWidget(new ExtendedButton(
                (this.width - this.imageWidth) / 2
                - 22
                - font.width(MANAGER_GUI_BUTTON_EXPORT_CLIPBOARD.getComponent()),
                (this.height - this.imageHeight) / 2 + 128,
                100,
                16,
                MANAGER_GUI_BUTTON_EXPORT_CLIPBOARD.getComponent(),
                button -> this.onSaveClipboard()
        ));
        this.addRenderableWidget(new ExtendedButtonWithTooltip(
                (this.width - this.imageWidth) / 2 + 120,
                (this.height - this.imageHeight) / 2 + 10,
                50,
                12,
                MANAGER_GUI_BUTTON_RESET.getComponent(),
                button -> sendReset(),
                (btn, pose, mx, my) -> renderTooltip(
                        pose,
                        font.split(
                                MANAGER_RESET_BUTTON_TOOLTIP.getComponent(),
                                Math.max(width / 2 - 43, 170)
                        ),
                        mx,
                        my
                )
        ));
        this.addRenderableWidget(diagButton = new ExtendedButtonWithTooltip(
                (this.width - this.imageWidth) / 2 + 35,
                (this.height - this.imageHeight) / 2 + 48,
                12,
                14,
                Component.translatable("!"),
                button -> {
                    if (Screen.hasShiftDown()) {
                        sendAttemptFix();
                    } else {
                        this.onSaveDiagClipboard();
                    }
                },
                (btn, pose, mx, my) -> renderTooltip(
                        pose,
                        font.split(
                                MANAGER_GUI_WARNING_BUTTON_TOOLTIP.getComponent(),
                                Math.max(width / 2 - 43, 170)
                        ),
                        mx,
                        my
                )
        ));
    }

    private void sendReset() {
        SFMPackets.MANAGER_CHANNEL.sendToServer(new ServerboundManagerResetPacket(
                menu.containerId,
                menu.BLOCK_ENTITY_POSITION
        ));
        status          = MANAGER_GUI_STATUS_RESET.getComponent();
        statusCountdown = STATUS_DURATION;
    }

    private void sendAttemptFix() {
        SFMPackets.MANAGER_CHANNEL.sendToServer(new ServerboundManagerFixPacket(
                menu.containerId,
                menu.BLOCK_ENTITY_POSITION
        ));
        status          = MANAGER_GUI_STATUS_FIX.getComponent();
        statusCountdown = STATUS_DURATION;
    }

    private void sendProgram(String program) {
        SFMPackets.MANAGER_CHANNEL.sendToServer(new ServerboundManagerProgramPacket(
                menu.containerId,
                menu.BLOCK_ENTITY_POSITION,
                program
        ));
        menu.program    = program;
        status          = MANAGER_GUI_STATUS_LOADED_CLIPBOARD.getComponent();
        statusCountdown = STATUS_DURATION;
    }

    private void onSaveClipboard() {
        try {
            Minecraft.getInstance().keyboardHandler.setClipboard(menu.program);
            status          = MANAGER_GUI_STATUS_SAVED_CLIPBOARD.getComponent();
            statusCountdown = STATUS_DURATION;
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private boolean shouldShowDiagButton() {
        var disk = menu.CONTAINER.getItem(0);
        if (!(disk.getItem() instanceof DiskItem)) return false;
        var errors   = DiskItem.getErrors(disk);
        var warnings = DiskItem.getWarnings(disk);
        return !errors.isEmpty() || !warnings.isEmpty();
    }

    private void onSaveDiagClipboard() {
        try {
            var disk = menu.CONTAINER.getItem(0);
            if (!(disk.getItem() instanceof DiskItem)) return;
            StringBuilder content = new StringBuilder(menu.program);

            content
                    .append("\n\n-- Diagnostic info ")
                    .append(new SimpleDateFormat("yyyy-MM-dd HH:mm.ss").format(new java.util.Date()))
                    .append(" --");

            var errors = DiskItem.getErrors(disk);
            if (!errors.isEmpty()) {
                content.append("\n\n-- Errors\n");
                for (var error : errors) {
                    content.append("-- * ").append(I18n.get(error.getKey(), error.getArgs())).append("\n");
                }
            }

            var warnings = DiskItem.getWarnings(disk);
            if (!warnings.isEmpty()) {
                content.append("\n\n-- Warnings\n");
                for (var warning : warnings) {
                    content.append("-- * ").append(I18n.get(warning.getKey(), warning.getArgs())).append("\n");
                }
            }

            Minecraft.getInstance().keyboardHandler.setClipboard(content.toString());
            status          = MANAGER_GUI_STATUS_SAVED_CLIPBOARD.getComponent();
            statusCountdown = STATUS_DURATION;
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void onLoadClipboard() {
        try {
            String contents = Minecraft.getInstance().keyboardHandler.getClipboard();
            sendProgram(contents);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public boolean keyPressed(int p_97765_, int p_97766_, int p_97767_) {
        if (super.keyPressed(p_97765_, p_97766_, p_97767_)) return true;
        if (Screen.isPaste(p_97765_)) {
            onLoadClipboard();
            return true;
        } else if (Screen.isCopy(p_97765_)) {
            onSaveClipboard();
            return true;
        }
        return false;
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mx, int my) {
        // draw title
        super.renderLabels(poseStack, mx, my);

        // draw state string
        var states = ManagerBlockEntity.State.values();
        var key    = menu.CONTAINER_DATA.get(ManagerBlockEntity.STATE_DATA_ACCESS_KEY);
        var state  = states[key];
        this.font.draw(
                poseStack,
                MANAGER_GUI_STATE.getComponent(state.LOC.getComponent().withStyle(state.COLOR)),
                titleLabelX,
                20,
                0
        );

        // draw status string
        if (statusCountdown > 0) {
            this.font.draw(
                    poseStack,
                    status,
                    titleLabelX,
                    20f + font.lineHeight + 0.1f,
                    0
            );
        }

        // Find the maximum tick time for normalization
        long peakTickTime = 0;
        for (int i = 0; i < 20; i++) {
            int tickTimeNanos = menu.CONTAINER_DATA.get(ManagerBlockEntity.TICK_TIME_DATA_ACCESS_KEY + i);
            peakTickTime = Math.max(peakTickTime, tickTimeNanos);
        }

        // Draw the label for the largest value
        var format = NumberFormat.getInstance(Locale.getDefault());
        this.font.draw(
                poseStack,
                MANAGER_GUI_PEAK_TICK_TIME.getComponent((peakTickTime > 25_000_000 ? "§c" : "§a") + format.format(
                        peakTickTime)),
                titleLabelX,
                20f + font.lineHeight + 0.1f,
                0
        );


        // Constants for the plot size and position
        final int plotX      = titleLabelX + 50;
        final int plotY      = 50;
        final int plotWidth  = 100;
        final int plotHeight = 50;


        // Set up rendering
        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator    tesselator    = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        Matrix4f pose = poseStack.last().pose();

        // Draw lines for each data point
        int latestTickIndex = menu.CONTAINER_DATA.get(ManagerBlockEntity.LATEST_TICK_INDEX);
        for (int i = 0; i < 19; i++) {
            for (int j = 0; j < 2; j++) {
                int dataIndex = (latestTickIndex + i + j + 1)
                                % 20; // Calculate the data index based on the latest tick index
                int tickTimeNanos = menu.CONTAINER_DATA.get(ManagerBlockEntity.TICK_TIME_DATA_ACCESS_KEY + dataIndex);
                if (tickTimeNanos == 0) {
                    tickTimeNanos = 10000; // force min value
                }
                float normalizedTickTime = (float) (Math.log10(tickTimeNanos) / Math.log10(peakTickTime));
                int   plotPosY           = plotY + plotHeight - (int) (normalizedTickTime * plotHeight);

                int plotPosX = plotX + (18 - i - j) * plotWidth / 18;

                // Color the lines based on their tick times (green to red)
                float red   = Math.min((float) tickTimeNanos / (50_000_000), 1.0f); // 50ms in nanoseconds
                float green = 1.0f - red;

                bufferbuilder
                        .vertex(pose, (float) plotPosX, (float) plotPosY, (float) getBlitOffset())
                        .color(red, green, 0, 1f)
                        .endVertex();
            }
        }

// Finish rendering
        tesselator.end();
        RenderSystem.disableBlend();
        RenderSystem.enableTexture();


    }

    @Override
    public void render(PoseStack poseStack, int mx, int my, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mx, my, partialTicks);
        this.renderTooltip(poseStack, mx, my);

        // update diag button visibility
        diagButton.visible = shouldShowDiagButton();

        // update status countdown
        statusCountdown -= partialTicks;
    }

    @Override
    protected void renderTooltip(PoseStack pose, int mx, int my) {
        super.renderTooltip(pose, mx, my);
        this.renderables
                .stream()
                .filter(ExtendedButtonWithTooltip.class::isInstance)
                .map(ExtendedButtonWithTooltip.class::cast)
                .forEach(x -> x.renderToolTip(pose, mx, my));

    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mx, int my) {
        //        Lighting.setupForFlatItems();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE_LOCATION);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }
}
