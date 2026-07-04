package ca.teamdman.sfm.client.screen;

import ca.teamdman.sfm.client.screen.widget.SFMButtonBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class SFMDrawCanvasScreen extends Screen {
    private static final int BACKGROUND = 0xFF15191E;
    private static final int MINOR_GRID = 0xFF252C34;
    private static final int MAJOR_GRID = 0xFF343D47;
    private static final int AXIS_X = 0xFF9A6B6B;
    private static final int AXIS_Y = 0xFF6D9075;
    private static final int CROSSHAIR = 0xFFE6EDF3;
    private static final int GLYPH = 0xFFE6EDF3;
    private static final int HUD_BACKGROUND = 0xC0181D23;
    private static final int HUD_BORDER = 0xFF4B5563;
    private static final int HUD_TEXT = 0xFFE6EDF3;
    private static final int HUD_MUTED = 0xFF9CA3AF;
    private static final double MIN_ZOOM = 0.05D;
    private static final double MAX_ZOOM = 8.0D;
    private static final double ZOOM_STEP = 1.15D;
    private static final double BASE_GRID_STEP = 32.0D;
    private static final double MAJOR_GRID_INTERVAL = 5.0D;
    private static final double MIN_GRID_PIXEL_STEP = 12.0D;

    private final Screen previousScreen;
    private final List<CanvasGlyph> glyphs = new ArrayList<>();
    private double cameraX;
    private double cameraY;
    private double zoom = 1.0D;
    private double cursorCanvasX;
    private double cursorCanvasY;
    private boolean panning;
    private double panAnchorMouseX;
    private double panAnchorMouseY;
    private double panAnchorCameraX;
    private double panAnchorCameraY;

    public SFMDrawCanvasScreen(Screen previousScreen) {
        super(Component.literal("SFM Draw Canvas"));
        this.previousScreen = previousScreen;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(previousScreen);
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new SFMButtonBuilder()
                .setPosition(this.width - 88, this.height - 24)
                .setSize(80, 20)
                .setText(CommonComponents.GUI_DONE)
                .setOnPress(button -> this.onClose())
                .build());
    }

    @Override
    public void render(
            PoseStack poseStack,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        fill(poseStack, 0, 0, this.width, this.height, BACKGROUND);
        renderGrid(poseStack);
        renderGlyphs(poseStack);
        renderCanvasCursor(poseStack);
        renderHud(poseStack, mouseX, mouseY);
        super.render(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    public void mouseMoved(
            double mouseX,
            double mouseY
    ) {
        cursorCanvasX = screenToCanvasX(mouseX);
        cursorCanvasY = screenToCanvasY(mouseY);
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
            beginPan(mouseX, mouseY);
            return true;
        }
        cursorCanvasX = screenToCanvasX(mouseX);
        cursorCanvasY = screenToCanvasY(mouseY);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            int button,
            double dragX,
            double dragY
    ) {
        if (panning && button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE) {
            cameraX = panAnchorCameraX - (mouseX - panAnchorMouseX) / zoom;
            cameraY = panAnchorCameraY - (mouseY - panAnchorMouseY) / zoom;
            cursorCanvasX = screenToCanvasX(mouseX);
            cursorCanvasY = screenToCanvasY(mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(
            double mouseX,
            double mouseY,
            int button
    ) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE && panning) {
            panning = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double delta
    ) {
        if (delta == 0.0D) {
            return super.mouseScrolled(mouseX, mouseY, delta);
        }
        double focusX = screenToCanvasX(mouseX);
        double focusY = screenToCanvasY(mouseY);
        double scaleFactor = Math.pow(ZOOM_STEP, delta);
        zoom = Mth.clamp(zoom * scaleFactor, MIN_ZOOM, MAX_ZOOM);
        cameraX = focusX - (mouseX - this.width / 2.0D) / zoom;
        cameraY = focusY - (mouseY - this.height / 2.0D) / zoom;
        cursorCanvasX = focusX;
        cursorCanvasY = focusY;
        return true;
    }

    @Override
    public boolean charTyped(
            char codePoint,
            int modifiers
    ) {
        if (Character.isISOControl(codePoint)) {
            return super.charTyped(codePoint, modifiers);
        }
        String text = Character.toString(codePoint);
        glyphs.add(new CanvasGlyph(text, cursorCanvasX, cursorCanvasY));
        cursorCanvasX += this.font.width(text);
        return true;
    }

    @Override
    public boolean keyPressed(
            int keyCode,
            int scanCode,
            int modifiers
    ) {
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !glyphs.isEmpty()) {
            CanvasGlyph removed = glyphs.remove(glyphs.size() - 1);
            cursorCanvasX = removed.x();
            cursorCanvasY = removed.y();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void beginPan(
            double mouseX,
            double mouseY
    ) {
        panning = true;
        panAnchorMouseX = mouseX;
        panAnchorMouseY = mouseY;
        panAnchorCameraX = cameraX;
        panAnchorCameraY = cameraY;
    }

    private void renderGrid(PoseStack poseStack) {
        double step = visibleGridStep();
        double leftCanvas = screenToCanvasX(0);
        double rightCanvas = screenToCanvasX(this.width);
        double topCanvas = screenToCanvasY(0);
        double bottomCanvas = screenToCanvasY(this.height);

        int firstVertical = Mth.floor(leftCanvas / step);
        int lastVertical = Mth.ceil(rightCanvas / step);
        for (int gridX = firstVertical; gridX <= lastVertical; gridX++) {
            double canvasX = gridX * step;
            int screenX = (int) Math.round(canvasToScreenX(canvasX));
            int color = gridLineColor(gridX);
            fill(poseStack, screenX, 0, screenX + 1, this.height, color);
        }

        int firstHorizontal = Mth.floor(topCanvas / step);
        int lastHorizontal = Mth.ceil(bottomCanvas / step);
        for (int gridY = firstHorizontal; gridY <= lastHorizontal; gridY++) {
            double canvasY = gridY * step;
            int screenY = (int) Math.round(canvasToScreenY(canvasY));
            int color = gridLineColor(gridY);
            fill(poseStack, 0, screenY, this.width, screenY + 1, color);
        }

        int axisX = (int) Math.round(canvasToScreenX(0.0D));
        if (axisX >= 0 && axisX < this.width) {
            fill(poseStack, axisX, 0, axisX + 2, this.height, AXIS_Y);
        }
        int axisY = (int) Math.round(canvasToScreenY(0.0D));
        if (axisY >= 0 && axisY < this.height) {
            fill(poseStack, 0, axisY, this.width, axisY + 2, AXIS_X);
        }
    }

    private int gridLineColor(int gridIndex) {
        return Math.floorMod(gridIndex, (int) MAJOR_GRID_INTERVAL) == 0 ? MAJOR_GRID : MINOR_GRID;
    }

    private double visibleGridStep() {
        double step = BASE_GRID_STEP;
        while (step * zoom < MIN_GRID_PIXEL_STEP) {
            step *= 2.0D;
        }
        while (step * zoom >= MIN_GRID_PIXEL_STEP * 4.0D) {
            step /= 2.0D;
        }
        return step;
    }

    private void renderGlyphs(PoseStack poseStack) {
        for (CanvasGlyph glyph : glyphs) {
            poseStack.pushPose();
            poseStack.translate(canvasToScreenX(glyph.x()), canvasToScreenY(glyph.y()), 0.0D);
            poseStack.scale((float) zoom, (float) zoom, 1.0F);
            drawString(poseStack, this.font, glyph.text(), 0, 0, GLYPH);
            poseStack.popPose();
        }
    }

    private void renderCanvasCursor(PoseStack poseStack) {
        int mouseX = (int) Math.round(canvasToScreenX(cursorCanvasX));
        int mouseY = (int) Math.round(canvasToScreenY(cursorCanvasY));
        int size = panning ? 8 : 6;
        fill(poseStack, mouseX - size, mouseY, mouseX - 2, mouseY + 1, CROSSHAIR);
        fill(poseStack, mouseX + 3, mouseY, mouseX + size + 1, mouseY + 1, CROSSHAIR);
        fill(poseStack, mouseX, mouseY - size, mouseX + 1, mouseY - 2, CROSSHAIR);
        fill(poseStack, mouseX, mouseY + 3, mouseX + 1, mouseY + size + 1, CROSSHAIR);
        fill(poseStack, mouseX, mouseY, mouseX + 1, mouseY + 1, CROSSHAIR);
    }

    private void renderHud(
            PoseStack poseStack,
            int mouseX,
            int mouseY
    ) {
        int left = 8;
        int top = 8;
        int right = 226;
        int bottom = 48;
        fill(poseStack, left, top, right, bottom, HUD_BACKGROUND);
        fill(poseStack, left, top, right, top + 1, HUD_BORDER);
        fill(poseStack, left, bottom - 1, right, bottom, HUD_BORDER);
        fill(poseStack, left, top, left + 1, bottom, HUD_BORDER);
        fill(poseStack, right - 1, top, right, bottom, HUD_BORDER);

        drawString(poseStack, this.font, this.title, left + 8, top + 7, HUD_TEXT);
        drawString(
                poseStack,
                this.font,
                String.format(
                        "cursor %.1f, %.1f  zoom %.2fx",
                        cursorCanvasX,
                        cursorCanvasY,
                        zoom
                ),
                left + 8,
                top + 22,
                HUD_MUTED
        );
    }

    private double canvasToScreenX(double canvasX) {
        return (canvasX - cameraX) * zoom + this.width / 2.0D;
    }

    private double canvasToScreenY(double canvasY) {
        return (canvasY - cameraY) * zoom + this.height / 2.0D;
    }

    private double screenToCanvasX(double screenX) {
        return (screenX - this.width / 2.0D) / zoom + cameraX;
    }

    private double screenToCanvasY(double screenY) {
        return (screenY - this.height / 2.0D) / zoom + cameraY;
    }

    private record CanvasGlyph(
            String text,
            double x,
            double y
    ) {
    }
}
