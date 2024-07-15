package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.ClientDiagnosticInfo;
import ca.teamdman.sfm.client.ClientStuff;
import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.item.DiskItem;
import ca.teamdman.sfm.common.net.ServerboundManagerFixPacket;
import ca.teamdman.sfm.common.net.ServerboundManagerProgramPacket;
import ca.teamdman.sfm.common.net.ServerboundManagerRebuildPacket;
import ca.teamdman.sfm.common.net.ServerboundManagerResetPacket;
import ca.teamdman.sfm.common.registry.SFMPackets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.Level;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;

import java.text.DecimalFormat;
import java.util.List;

import static ca.teamdman.sfm.common.Constants.LocalizationKeys.*;

public class ManagerScreen extends AbstractContainerScreen<ManagerContainerMenu> {
    private static final ResourceLocation BACKGROUND_TEXTURE_LOCATION = new ResourceLocation(
            SFM.MOD_ID,
            "textures/gui/container/manager.png"
    );
    private final float STATUS_DURATION = 40;
    private Component status = Component.empty();
    private float statusCountdown = 0;
    @SuppressWarnings("NotNullFieldNotInitialized")
    private ExtendedButton diagButton;
    @SuppressWarnings("NotNullFieldNotInitialized")
    private ExtendedButton clipboardPasteButton;
    @SuppressWarnings("NotNullFieldNotInitialized")
    private ExtendedButton clipboardCopyButton;
    @SuppressWarnings("NotNullFieldNotInitialized")
    private ExtendedButton resetButton;
    @SuppressWarnings("NotNullFieldNotInitialized")
    private ExtendedButton editButton;
    @SuppressWarnings("NotNullFieldNotInitialized")
    private ExtendedButton examplesButton;
    @SuppressWarnings("NotNullFieldNotInitialized")
    private ExtendedButton logsButton;
    @SuppressWarnings("NotNullFieldNotInitialized")
    private ExtendedButton rebuildButton;

    public ManagerScreen(ManagerContainerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    public List<ExtendedButton> getButtonsForJEIExclusionZones() {
        return List.of(
                clipboardPasteButton,
                editButton,
                examplesButton,
                clipboardCopyButton,
                logsButton,
                rebuildButton
        );
    }

    public boolean isReadOnly() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player == null || player.isSpectator();
    }

    public void updateVisibilities() {
        boolean diskPresent = menu.getSlot(0).hasItem();
        diagButton.visible = shouldShowDiagButton();
        clipboardCopyButton.visible = diskPresent;
        logsButton.visible = diskPresent;
        rebuildButton.visible = diskPresent && !isReadOnly();
        clipboardPasteButton.visible = diskPresent && !isReadOnly();
        resetButton.visible = diskPresent && !isReadOnly();
        editButton.visible = diskPresent && !isReadOnly();
    }

    private Tooltip buildTooltip(LocalizationEntry entry) {
        return Tooltip.create(entry.getComponent());
    }

    @Override
    protected void init() {
        super.init();
        int buttonWidth = 120;
        clipboardPasteButton = this.addRenderableWidget(new ExtendedButtonWithTooltip(
                (this.width - this.imageWidth) / 2 - buttonWidth,
                (this.height - this.imageHeight) / 2 + 16,
                buttonWidth,
                16,
                MANAGER_GUI_PASTE_FROM_CLIPBOARD_BUTTON.getComponent(),
                button -> this.onClipboardPasteButtonClicked(),
                buildTooltip(MANAGER_GUI_PASTE_FROM_CLIPBOARD_BUTTON_TOOLTIP)
        ));
        editButton = this.addRenderableWidget(new ExtendedButtonWithTooltip(
                (this.width - this.imageWidth) / 2 - buttonWidth,
                (this.height - this.imageHeight) / 2 + 16 + 50,
                buttonWidth,
                16,
                MANAGER_GUI_EDIT_BUTTON.getComponent(),
                button -> onEditButtonClicked(),
                buildTooltip(MANAGER_GUI_EDIT_BUTTON_TOOLTIP)
        ));
        examplesButton = this.addRenderableWidget(new ExtendedButtonWithTooltip(
                (this.width - this.imageWidth) / 2 - buttonWidth,
                (this.height - this.imageHeight) / 2 + 16 * 2 + 50,
                buttonWidth,
                16,
                MANAGER_GUI_VIEW_EXAMPLES_BUTTON.getComponent(),
                button -> onExamplesButtonClicked(),
                buildTooltip(MANAGER_GUI_VIEW_EXAMPLES_BUTTON_TOOLTIP)
        ));
        clipboardCopyButton = this.addRenderableWidget(new ExtendedButton(
                (this.width - this.imageWidth) / 2 - buttonWidth,
                (this.height - this.imageHeight) / 2 + 128,
                buttonWidth,
                16,
                MANAGER_GUI_COPY_TO_CLIPBOARD_BUTTON.getComponent(),
                button -> this.onClipboardCopyButtonClicked()
        ));
        logsButton = this.addRenderableWidget(new ExtendedButton(
                (this.width - this.imageWidth) / 2 - buttonWidth,
                (this.height - this.imageHeight) / 2 + 16 * 9,
                buttonWidth,
                16,
                MANAGER_GUI_VIEW_LOGS_BUTTON.getComponent(),
                button -> onLogsButtonClicked()
        ));
        rebuildButton = this.addRenderableWidget(new ExtendedButton(
                (this.width - this.imageWidth) / 2 - buttonWidth,
                (this.height - this.imageHeight) / 2 + 16 * 10,
                buttonWidth,
                16,
                MANAGER_GUI_REBUILD_BUTTON.getComponent(),
                button -> this.onRebuildButtonClicked()
        ));
        resetButton = this.addRenderableWidget(new ExtendedButtonWithTooltip(
                (this.width - this.imageWidth) / 2 + 120,
                (this.height - this.imageHeight) / 2 + 10,
                50,
                12,
                MANAGER_GUI_RESET_BUTTON.getComponent(),
                button -> onResetButtonClicked(),
                buildTooltip(MANAGER_GUI_RESET_BUTTON_TOOLTIP)
        ));
        diagButton = this.addRenderableWidget(new ExtendedButtonWithTooltip(
                (this.width - this.imageWidth) / 2 + 35,
                (this.height - this.imageHeight) / 2 + 48,
                12,
                14,
                Component.literal("!"),
                button -> onDiagButtonClicked(),
                buildTooltip(isReadOnly()
                             ? MANAGER_GUI_WARNING_BUTTON_TOOLTIP_READ_ONLY
                             : MANAGER_GUI_WARNING_BUTTON_TOOLTIP)
        ));
        updateVisibilities();
    }

    private void onDiagButtonClicked() {
        if (Screen.hasShiftDown() && !isReadOnly()) {
            sendAttemptFix();
        } else {
            this.onSaveDiagClipboard();
        }
    }

    private void onEditButtonClicked() {
        ClientStuff.showProgramEditScreen(DiskItem.getProgram(menu.getDisk()), this::sendProgram);
    }

    private void onExamplesButtonClicked() {
        ClientStuff.showExampleListScreen(DiskItem.getProgram(menu.getDisk()), this::sendProgram);
    }

    private void onLogsButtonClicked() {
        ClientStuff.showLogsScreen(menu);
    }

    private void onResetButtonClicked() {
        ConfirmScreen confirmScreen = new ConfirmScreen(
                proceed -> {
                    assert this.minecraft != null;
                    this.minecraft.popGuiLayer(); // Close confirm screen

                    if (proceed) {
                        PacketDistributor.SERVER.noArg().send(new ServerboundManagerResetPacket(
                                menu.containerId,
                                menu.MANAGER_POSITION
                        ));
                        status = MANAGER_GUI_STATUS_RESET.getComponent();
                        statusCountdown = STATUS_DURATION;
                    }
                },
                Constants.LocalizationKeys.MANAGER_RESET_CONFIRM_SCREEN_TITLE.getComponent(),
                Constants.LocalizationKeys.MANAGER_RESET_CONFIRM_SCREEN_MESSAGE.getComponent(),
                Constants.LocalizationKeys.MANAGER_RESET_CONFIRM_SCREEN_YES_BUTTON.getComponent(),
                Constants.LocalizationKeys.MANAGER_RESET_CONFIRM_SCREEN_NO_BUTTON.getComponent()
        );
        assert this.minecraft != null;
        this.minecraft.pushGuiLayer(confirmScreen);
        confirmScreen.setDelay(20);
    }

    private void onRebuildButtonClicked() {
        PacketDistributor.SERVER.noArg().send(new ServerboundManagerRebuildPacket(
                menu.containerId,
                menu.MANAGER_POSITION
        ));
        status = MANAGER_GUI_STATUS_REBUILD.getComponent();
        statusCountdown = STATUS_DURATION;
    }

    private void sendAttemptFix() {
        PacketDistributor.SERVER.noArg().send(new ServerboundManagerFixPacket(
                menu.containerId,
                menu.MANAGER_POSITION
        ));
        status = MANAGER_GUI_STATUS_FIX.getComponent();
        statusCountdown = STATUS_DURATION;
    }

    private void sendProgram(String program) {
        PacketDistributor.SERVER.noArg().send(new ServerboundManagerProgramPacket(
                menu.containerId,
                menu.MANAGER_POSITION,
                program
        ));
        menu.program = program;
        status = MANAGER_GUI_STATUS_LOADED_CLIPBOARD.getComponent();
        statusCountdown = STATUS_DURATION;
    }

    private void onClipboardCopyButtonClicked() {
        try {
            Minecraft.getInstance().keyboardHandler.setClipboard(menu.program);
            status = MANAGER_GUI_STATUS_SAVED_CLIPBOARD.getComponent();
            statusCountdown = STATUS_DURATION;
        } catch (Throwable t) {
            SFM.LOGGER.error("failed to save clipboard", t);
        }
    }

    private boolean shouldShowDiagButton() {
        var disk = menu.getDisk();
        if (!(disk.getItem() instanceof DiskItem)) return false;
        var errors = DiskItem.getErrors(disk);
        var warnings = DiskItem.getWarnings(disk);
        return !errors.isEmpty() || !warnings.isEmpty();
    }

    private void onSaveDiagClipboard() {
        try {
            var disk = menu.CONTAINER.getItem(0);
            if (!(disk.getItem() instanceof DiskItem)) return;
            Minecraft.getInstance().keyboardHandler.setClipboard(ClientDiagnosticInfo.getDiagnosticInfo(
                    menu.program,
                    disk
            ));
            status = MANAGER_GUI_STATUS_SAVED_CLIPBOARD.getComponent();
            statusCountdown = STATUS_DURATION;
        } catch (Throwable t) {
            SFM.LOGGER.error("failed saving clipboard", t);
        }
    }

    private void onClipboardPasteButtonClicked() {
        try {
            String contents = Minecraft.getInstance().keyboardHandler.getClipboard();
            sendProgram(contents);
        } catch (Throwable t) {
            SFM.LOGGER.error("failed loading clipboard", t);
        }
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if (Screen.isPaste(pKeyCode) && clipboardPasteButton.visible) {
            onClipboardPasteButtonClicked();
            return true;
        } else if (Screen.isCopy(pKeyCode) && clipboardCopyButton.visible) {
            onClipboardCopyButtonClicked();
            return true;
        } else if (pKeyCode == GLFW.GLFW_KEY_E
                   && Screen.hasControlDown()
                   && Screen.hasShiftDown()
                   && examplesButton.visible) {
            onExamplesButtonClicked();
            return true;
        } else if (pKeyCode == GLFW.GLFW_KEY_E && Screen.hasControlDown() && editButton.visible) {
            onEditButtonClicked();
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mx, int my) {
        PoseStack poseStack = graphics.pose();
        // draw title
        super.renderLabels(graphics, mx, my);

        // draw state string
        var state = menu.state;
        graphics.drawString(
                this.font,
                MANAGER_GUI_STATE.getComponent(state.LOC.getComponent().withStyle(state.COLOR)).withStyle(),
                titleLabelX,
                20,
                0,
                false
        );

        // draw log level
        if (!menu.logLevel.equals(Level.OFF.name())) {
            poseStack.pushPose();
            poseStack.translate(
                    titleLabelX,
                    font.lineHeight * 1.5,
                    0f
            );
            poseStack.scale(0.5f, 0.5f, 1f);
            graphics.drawString(
                    this.font,
                    Component
                            .literal(menu.logLevel),
                    0,
                    0,
                    0,
                    false
            );
            poseStack.popPose();
        }

        // draw status string
        if (statusCountdown > 0) {
            graphics.drawString(
                    this.font,
                    status,
                    inventoryLabelX + font.width(playerInventoryTitle.getString()) + 5,
                    inventoryLabelY,
                    0,
                    false
            );
        }

        // Find the maximum tick time for normalization
        long peakTickTimeNanoseconds = 0;
        for (int i = 0; i < menu.tickTimeNanos.length; i++) {
            peakTickTimeNanoseconds = Long.max(peakTickTimeNanoseconds, menu.tickTimeNanos[i]);
        }
        long yMax = Long.max(peakTickTimeNanoseconds, 50000000); // Start with max at 50ms but allow it to grow

        // Constants for the plot size and position
        final int plotX = titleLabelX + 45;
        final int plotY = 40;
        final int spaceBetweenPoints = 6;
        final int plotWidth = spaceBetweenPoints * (menu.tickTimeNanos.length - 1);
        final int plotHeight = 30;


        // Set up rendering
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Tesselator tesselator = Tesselator.getInstance();
        Matrix4f pose = graphics.pose().last().pose();
        BufferBuilder bufferbuilder;

        // Draw the plot background
        bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(pose, plotX, plotY, 0).color(0, 0, 0, 0.5f).endVertex();
        bufferbuilder.vertex(pose, plotX + plotWidth, plotY, 0).color(0, 0, 0, 0.5f).endVertex();
        bufferbuilder.vertex(pose, plotX + plotWidth, plotY + plotHeight, 0).color(0, 0, 0, 0.5f).endVertex();
        bufferbuilder.vertex(pose, plotX, plotY + plotHeight, 0).color(0, 0, 0, 0.5f).endVertex();
        bufferbuilder.vertex(pose, plotX, plotY, 0).color(0, 0, 0, 0.5f).endVertex();
        tesselator.end();

        // Draw lines for each data point
        bufferbuilder = tesselator.getBuilder();
        bufferbuilder.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
        int mouseTickTimeIndex = -1;
        for (int i = 0; i < menu.tickTimeNanos.length; i++) {
            long y = menu.tickTimeNanos[i];
            float normalizedTickTime = y == 0 ? 0 : (float) (Math.log10(y) / Math.log10(yMax));
            int plotPosY = plotY + plotHeight - (int) (normalizedTickTime * plotHeight);

            int plotPosX = plotX + spaceBetweenPoints * i;

            // Color the lines based on their tick times (green to red)
            var c = getMillisecondColour(y / 1_000_000f);
            //noinspection DataFlowIssue
            float red = ((c.getColor() >> 16) & 0xFF) / 255f;
            float green = ((c.getColor() >> 8) & 0xFF) / 255f;
            float blue = (c.getColor() & 0xFF) / 255f;

            bufferbuilder
                    .vertex(pose, (float) plotPosX, (float) plotPosY, 0f)
                    .color(red, green, blue, 1f)
                    .endVertex();

            // Check if the mouse is hovering over this line
            if (mx - leftPos >= plotPosX - spaceBetweenPoints / 2
                && mx - leftPos <= plotPosX + spaceBetweenPoints / 2
                && my - topPos >= plotY - 2
                && my - topPos <= plotY + plotHeight + 2) {
                mouseTickTimeIndex = i;
            }
        }
        tesselator.end();

        // Draw the tick time text
        var format = new DecimalFormat("0.000");
        if (mouseTickTimeIndex != -1) { // We are hovering over the plot
            // Draw the tick time text for the hovered point instead of peak
            long hoveredTickTimeNanoseconds = menu.tickTimeNanos[mouseTickTimeIndex];
            var hoveredTickTimeMilliseconds = hoveredTickTimeNanoseconds / 1_000_000f;

            graphics.drawString(
                    this.font,
                    MANAGER_GUI_HOVERED_TICK_TIME.getComponent(Component
                                                                       .literal(format.format(
                                                                               hoveredTickTimeMilliseconds))
                                                                       .withStyle(getMillisecondColour(
                                                                               hoveredTickTimeMilliseconds))),
                    titleLabelX,
                    20 + font.lineHeight,
                    0,
                    false
            );

            // draw a vertical line
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            tesselator = Tesselator.getInstance();
            bufferbuilder = tesselator.getBuilder();
            bufferbuilder.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);
            pose = graphics.pose().last().pose();

            int x = plotX + spaceBetweenPoints * mouseTickTimeIndex;
            bufferbuilder
                    .vertex(pose, (float) x, (float) plotY, 0f)
                    .color(1f, 1f, 1f, 1f)
                    .endVertex();
            bufferbuilder
                    .vertex(pose, (float) x, (float) plotY + plotHeight, 0f)
                    .color(1f, 1f, 1f, 1f)
                    .endVertex();
            tesselator.end();
        } else {
            // Draw the tick time text for peak value
            var peakTickTimeMilliseconds = peakTickTimeNanoseconds / 1_000_000f;
            graphics.drawString(
                    this.font,
                    MANAGER_GUI_PEAK_TICK_TIME.getComponent(Component
                                                                    .literal(format.format(peakTickTimeMilliseconds))
                                                                    .withStyle(getMillisecondColour(
                                                                            peakTickTimeMilliseconds))),
                    titleLabelX,
                    20 + font.lineHeight,
                    0,
                    false
            );
        }

        // Restore stuff
        RenderSystem.disableBlend();
    }

    public ChatFormatting getMillisecondColour(float ms) {
        if (ms <= 5) {
            return ChatFormatting.GREEN;
        } else if (ms <= 15) {
            return ChatFormatting.YELLOW;
        } else {
            return ChatFormatting.RED;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mx, int my, float partialTicks) {
        this.renderTransparentBackground(graphics);
        super.render(graphics, mx, my, partialTicks);
        this.renderTooltip(graphics, mx, my);

        updateVisibilities();

        // update status countdown
        statusCountdown -= partialTicks;
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        if (Minecraft.getInstance().screen != this) return;
        super.renderTooltip(pGuiGraphics, pX, pY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mx, int my) {
        if (!menu.logLevel.equals(Level.OFF.name())) {
            RenderSystem.setShaderColor(0.2f, 0.8f, 1f, 1f);
        } else {
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        }
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        graphics.blit(BACKGROUND_TEXTURE_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }
}
