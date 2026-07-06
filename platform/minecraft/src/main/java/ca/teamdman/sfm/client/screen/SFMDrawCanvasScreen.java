package ca.teamdman.sfm.client.screen;

import ca.teamdman.sfm.client.screen.widget.SFMButtonBuilder;
import ca.teamdman.sfm.client.screen.text_editor.ISFMTextEditScreen;
import ca.teamdman.sfm.client.text_editor.ISFMTextEditScreenOpenContext;
import ca.teamdman.sfm.common.config.SFMConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SFMDrawCanvasScreen extends Screen implements ISFMTextEditScreen {
    private static final int BACKGROUND = 0xFF15191E;
    private static final int MINOR_GRID = 0xFF252C34;
    private static final int MAJOR_GRID = 0xFF343D47;
    private static final int AXIS_X = 0xFF9A6B6B;
    private static final int AXIS_Y = 0xFF6D9075;
    private static final int CURSOR_TRAIL = 0xFFFF8A8A;
    private static final int GLYPH = 0xFFE6EDF3;
    private static final int GLYPH_BOUNDS = 0xFFFF5CCD;
    private static final int HUD_BACKGROUND = 0xC0181D23;
    private static final int HUD_BORDER = 0xFF4B5563;
    private static final int HUD_TEXT = 0xFFE6EDF3;
    private static final int HUD_MUTED = 0xFF9CA3AF;
    private static final int INPUT_LOG_LIMIT = 8;
    private static final double MIN_ZOOM = 0.05D;
    private static final double MAX_ZOOM = 8.0D;
    private static final double ZOOM_STEP = 1.15D;
    private static final double BASE_GRID_STEP = 32.0D;
    private static final double MAJOR_GRID_INTERVAL = 5.0D;
    private static final double MIN_GRID_PIXEL_STEP = 12.0D;
    private static final int CURSOR_TRAIL_LIMIT = 48;
    private static final double CURSOR_TRAIL_MIN_DISTANCE = 2.0D;
    private static final int DEFAULT_ORIGIN_MARGIN = 32;

    private final Screen previousScreen;
    private final ISFMTextEditScreenOpenContext openContext;
    private SFMDrawCanvasModel model = new SFMDrawCanvasModel();
    private final List<CanvasPoint> cursorTrail = new ArrayList<>();
    private final List<String> inputEvents = new ArrayList<>();
    private final List<Button> diagnosticButtons = new ArrayList<>();
    private Button canvasFocusTarget;
    private double cameraX;
    private double cameraY;
    private double zoom = 1.0D;
    private boolean cameraInitialized;
    private boolean diagnosticControlsVisible = false;
    private boolean showGrid = false;
    private boolean showCrosshairCoordinates = false;
    private boolean showGlyphBoundingBoxes = false;
    private boolean showCursorTrail = false;
    private boolean hideSelection = false;
    private boolean panning;
    private boolean initialContentLoaded;
    private double panAnchorMouseX;
    private double panAnchorMouseY;
    private double panAnchorCameraX;
    private double panAnchorCameraY;

    public SFMDrawCanvasScreen(Screen previousScreen) {
        super(Component.literal("SFM Draw Canvas"));
        this.previousScreen = previousScreen;
        this.openContext = null;
    }

    public SFMDrawCanvasScreen(
            ISFMTextEditScreenOpenContext openContext,
            Screen previousScreen
    ) {
        super(Component.literal("SFM Draw Canvas"));
        this.previousScreen = previousScreen;
        this.openContext = openContext;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        if (model().cursors().size() > 1) {
            model().collapseToFocusedCursor();
            rememberCursorPosition();
            return;
        }
        if (openContext == null) {
            onTryCloseStandalone();
            return;
        }
        openContext.onTryClose(getCurrentText(), () -> Minecraft.getInstance().setScreen(previousScreen));
    }

    @Override
    public ISFMTextEditScreenOpenContext openContext() {
        return openContext;
    }

    @Override
    public OpenBehaviour openBehaviour() {
        return OpenBehaviour.Replace;
    }

    @Override
    protected void init() {
        super.init();
        SFMScreenRenderUtils.enableKeyRepeating();
        loadInitialContent();
        initializeCamera();
        canvasFocusTarget = new CanvasFocusTarget(2, 2, Math.max(1, this.width - 4), Math.max(1, this.height - 4));
        this.addRenderableWidget(canvasFocusTarget);
        this.setInitialFocus(canvasFocusTarget);
        this.setFocused(canvasFocusTarget);
        canvasFocusTarget.setFocused(true);
        diagnosticButtons.clear();
        addDiagnosticButton(8, 8, () -> showCrosshairCoordinates, value -> showCrosshairCoordinates = value, "Coords");
        addDiagnosticButton(8, 32, () -> showGlyphBoundingBoxes, value -> showGlyphBoundingBoxes = value, "Glyph Bounds");
        addDiagnosticButton(8, 56, () -> showCursorTrail, value -> showCursorTrail = value, "Cursor Trail");
        addDiagnosticButton(8, 80, () -> showGrid, value -> showGrid = value, "Grid");
        addDiagnosticButton(8, 104, () -> hideSelection, value -> hideSelection = value, "Hide Selection");
        if (openContext != null) {
            this.addRenderableWidget(new SFMButtonBuilder()
                    .setPosition(4, this.height - 24)
                    .setSize(16, 20)
                    .setText(Component.literal("#"))
                    .setOnPress(button -> SFMScreenChangeHelpers.setOrPushScreen(new SFMTextEditorConfigScreen(
                            this,
                            SFMConfig.CLIENT_TEXT_EDITOR_CONFIG,
                            () -> { }
                    )))
                    .build());
        }
        this.addRenderableWidget(new SFMButtonBuilder()
                .setPosition(this.width - 88, this.height - 24)
                .setSize(80, 20)
                .setText(CommonComponents.GUI_DONE)
                .setOnPress(button -> this.saveAndClose())
                .build());
        refreshDiagnosticControls();
    }

    @Override
    public void render(
            PoseStack poseStack,
            int mouseX,
            int mouseY,
            float partialTick
    ) {
        fill(poseStack, 0, 0, this.width, this.height, BACKGROUND);
        if (showGrid) {
            renderGrid(poseStack);
        }
        if (showCursorTrail) {
            renderCursorTrail(poseStack);
        }
        renderGlyphs(poseStack);
        if (!hideSelection) {
            renderGlyphSelectionHighlights(poseStack);
        }
        if (showGlyphBoundingBoxes) {
            renderGlyphBoundingBoxes(poseStack);
        }
        renderCanvasCursor(poseStack);
        if (showCrosshairCoordinates) {
            renderHud(poseStack);
        }
        if (diagnosticControlsVisible) {
            renderInputDiagnostics(poseStack);
        }
        super.render(poseStack, mouseX, mouseY, partialTick);
    }

    @Override
    public void mouseMoved(
            double mouseX,
            double mouseY
    ) {
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
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (super.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            if (hasAltDown()) {
                model().addCursor(screenToCanvasX(mouseX), screenToCanvasY(mouseY));
            } else {
                model().setActiveCursors(screenToCanvasX(mouseX), screenToCanvasY(mouseY));
            }
            this.setFocused(canvasFocusTarget);
            canvasFocusTarget.setFocused(true);
            rememberCursorPosition();
            return true;
        }
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
            model().setCursor(screenToCanvasX(mouseX), screenToCanvasY(mouseY));
            rememberCursorPosition();
            return true;
        }
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (hasAltDown()) {
                model().addCursorAvoidingCrowding(
                        screenToCanvasX(mouseX),
                        screenToCanvasY(mouseY),
                        this.font.width("W"),
                        this.font.lineHeight
                );
            } else {
                model().setActiveCursors(screenToCanvasX(mouseX), screenToCanvasY(mouseY));
            }
            this.setFocused(canvasFocusTarget);
            canvasFocusTarget.setFocused(true);
            rememberCursorPosition();
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
        model().setCursor(focusX, focusY);
        rememberCursorPosition();
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
        rememberInputEvent(String.format("charTyped '%s' U+%04X modifiers=%s", text, (int) codePoint, modifierText(modifiers)));
        model().typeGlyph(text, this.font.width(text));
        rememberCursorPosition();
        return true;
    }

    @Override
    public boolean keyPressed(
            int keyCode,
            int scanCode,
            int modifiers
    ) {
        rememberInputEvent(String.format("keyPressed key=%d scan=%d modifiers=%s", keyCode, scanCode, modifierText(modifiers)));
        if (keyCode == GLFW.GLFW_KEY_F3) {
            diagnosticControlsVisible = !diagnosticControlsVisible;
            refreshDiagnosticControls();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_F1) {
            model().focusPreviousCursor((modifiers & GLFW.GLFW_MOD_SHIFT) != 0);
            rememberCursorPosition();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_F4) {
            model().focusNextCursor((modifiers & GLFW.GLFW_MOD_SHIFT) != 0);
            rememberCursorPosition();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_A && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            model().ensureCursorClosestToEachGlyph();
            rememberCursorPosition();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_L && (modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            model().ensureCursorClosestToEachGlyphOnActiveCursorLines(this.font.lineHeight);
            rememberCursorPosition();
            return true;
        }
        if (handleNumpadMovement(keyCode)) {
            rememberCursorPosition();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_LEFT) {
            model().moveCursorLeft(this.font.lineHeight);
            rememberCursorPosition();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT) {
            model().moveCursorRight();
            rememberCursorPosition();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_UP) {
            if ((modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
                model().moveCursorUpToGlyph(this.font.lineHeight);
            } else {
                model().moveCursorUp(this.font.lineHeight);
            }
            rememberCursorPosition();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DOWN) {
            if ((modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
                model().moveCursorDownToGlyph(this.font.lineHeight);
            } else {
                model().moveCursorDown(this.font.lineHeight);
            }
            rememberCursorPosition();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_HOME) {
            if ((modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
                model().moveCursorToDocumentStart();
            } else {
                model().moveCursorToLineStart();
            }
            rememberCursorPosition();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_END) {
            if ((modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
                model().moveCursorToDocumentEnd();
            } else {
                model().moveCursorToLineEnd();
            }
            rememberCursorPosition();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            model().deleteLeft();
            rememberCursorPosition();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_DELETE) {
            model().deleteNearestAndMoveRight();
            rememberCursorPosition();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            if ((modifiers & GLFW.GLFW_MOD_SHIFT) != 0) {
                saveAndClose();
            } else {
                insertLineBreak();
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(
            int keyCode,
            int scanCode,
            int modifiers
    ) {
        rememberInputEvent(String.format("keyReleased key=%d scan=%d modifiers=%s", keyCode, scanCode, modifierText(modifiers)));
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    private boolean handleNumpadMovement(int keyCode) {
        double x = 0.0D;
        double y = 0.0D;
        switch (keyCode) {
            case GLFW.GLFW_KEY_KP_7 -> {
                x = -1.0D;
                y = -1.0D;
            }
            case GLFW.GLFW_KEY_KP_8 -> y = -1.0D;
            case GLFW.GLFW_KEY_KP_9 -> {
                x = 1.0D;
                y = -1.0D;
            }
            case GLFW.GLFW_KEY_KP_4 -> x = -1.0D;
            case GLFW.GLFW_KEY_KP_6 -> x = 1.0D;
            case GLFW.GLFW_KEY_KP_1 -> {
                x = -1.0D;
                y = 1.0D;
            }
            case GLFW.GLFW_KEY_KP_2 -> y = 1.0D;
            case GLFW.GLFW_KEY_KP_3 -> {
                x = 1.0D;
                y = 1.0D;
            }
            default -> {
                return false;
            }
        }
        model().moveCursorRaw(x, y);
        return true;
    }

    private void addDiagnosticButton(
            int x,
            int y,
            ToggleReader reader,
            ToggleWriter writer,
            String label
    ) {
        Button button = new SFMButtonBuilder()
                .setPosition(x, y)
                .setSize(104, 20)
                .setText(diagnosticButtonLabel(label, reader.get()))
                .setOnPress(pressed -> {
                    writer.set(!reader.get());
                    refreshDiagnosticControls();
                })
                .build();
        diagnosticButtons.add(button);
        this.addRenderableWidget(button);
    }

    private void refreshDiagnosticControls() {
        for (Button button : diagnosticButtons) {
            button.visible = diagnosticControlsVisible;
            button.active = diagnosticControlsVisible;
        }
        if (diagnosticButtons.size() >= 2) {
            diagnosticButtons.get(0).setMessage(diagnosticButtonLabel("Coords", showCrosshairCoordinates));
            diagnosticButtons.get(1).setMessage(diagnosticButtonLabel("Glyph Bounds", showGlyphBoundingBoxes));
        }
        if (diagnosticButtons.size() >= 3) {
            diagnosticButtons.get(2).setMessage(diagnosticButtonLabel("Cursor Trail", showCursorTrail));
        }
        if (diagnosticButtons.size() >= 4) {
            diagnosticButtons.get(3).setMessage(diagnosticButtonLabel("Grid", showGrid));
        }
        if (showCursorTrail && cursorTrail.isEmpty()) {
            rememberCursorPosition();
        }
        if (diagnosticButtons.size() >= 5) {
            diagnosticButtons.get(4).setMessage(diagnosticButtonLabel("Hide Selection", hideSelection));
        }
    }

    private Component diagnosticButtonLabel(
            String label,
            boolean enabled
    ) {
        return Component.literal((enabled ? "[x] " : "[ ] ") + label);
    }

    private SFMDrawCanvasModel model() {
        if (model == null) {
            model = new SFMDrawCanvasModel();
        }
        return model;
    }

    private void loadInitialContent() {
        if (initialContentLoaded || openContext == null) {
            return;
        }
        initialContentLoaded = true;
        model = new SFMDrawCanvasModel();
        model.typeText(openContext.initialValue(), this.font::width, this.font.lineHeight);
        model.moveCursorToDocumentStart();
    }

    private void saveAndClose() {
        if (openContext == null) {
            Minecraft.getInstance().setScreen(previousScreen);
            return;
        }
        openContext.saveWriter().accept(getCurrentText());
        Minecraft.getInstance().setScreen(previousScreen);
    }

    private void onTryCloseStandalone() {
        if (model().glyphs().isEmpty()) {
            Minecraft.getInstance().setScreen(previousScreen);
            return;
        }
        ConfirmScreen exitWithoutSavingConfirmScreen = new ConfirmScreen(
                doClose -> {
                    SFMScreenChangeHelpers.popScreen();
                    if (doClose) {
                        Minecraft.getInstance().setScreen(previousScreen);
                    }
                },
                ISFMTextEditScreenOpenContext.EXIT_WITHOUT_SAVING_CONFIRM_SCREEN_TITLE.getComponent(),
                ISFMTextEditScreenOpenContext.EXIT_WITHOUT_SAVING_CONFIRM_SCREEN_MESSAGE.getComponent(),
                ISFMTextEditScreenOpenContext.EXIT_WITHOUT_SAVING_CONFIRM_SCREEN_YES_BUTTON.getComponent(),
                ISFMTextEditScreenOpenContext.EXIT_WITHOUT_SAVING_CONFIRM_SCREEN_NO_BUTTON.getComponent()
        );
        SFMScreenChangeHelpers.setOrPushScreen(exitWithoutSavingConfirmScreen);
        exitWithoutSavingConfirmScreen.setDelay(20);
    }

    private String getCurrentText() {
        return SFMDrawCanvasSyntaxHighlightingHelper
                .projectCanvasDocument(model().glyphs(), this.font.width(" "), this.font.lineHeight)
                .text();
    }

    private void initializeCamera() {
        if (cameraInitialized) {
            return;
        }
        cameraX = (this.width / 2.0D - DEFAULT_ORIGIN_MARGIN) / zoom;
        cameraY = (this.height / 2.0D - DEFAULT_ORIGIN_MARGIN) / zoom;
        cameraInitialized = true;
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
        Map<SFMDrawCanvasModel.CanvasGlyph, Integer> glyphColours = SFMDrawCanvasSyntaxHighlightingHelper.buildSyntaxHighlightColours(
                model().glyphs(),
                this.font.width(" "),
                this.font.lineHeight,
                GLYPH
        );
        for (SFMDrawCanvasModel.CanvasGlyph glyph : model().glyphs()) {
            poseStack.pushPose();
            poseStack.translate(canvasToScreenX(glyph.x()), canvasToScreenY(glyph.y()), 0.0D);
            poseStack.scale((float) zoom, (float) zoom, 1.0F);
            drawString(poseStack, this.font, glyph.text(), 0, 0, glyphColours.getOrDefault(glyph, GLYPH));
            poseStack.popPose();
        }
    }

    private void renderGlyphBoundingBoxes(PoseStack poseStack) {
        for (SFMDrawCanvasModel.CanvasGlyph glyph : model().glyphs()) {
            int left = (int) Math.floor(canvasToScreenX(glyph.x()));
            int top = (int) Math.floor(canvasToScreenY(glyph.y()));
            int right = (int) Math.ceil(left + this.font.width(glyph.text()) * zoom);
            int bottom = (int) Math.ceil(top + this.font.lineHeight * zoom);
            drawRectOutline(poseStack, left, top, Math.max(left + 1, right), Math.max(top + 1, bottom), GLYPH_BOUNDS);
        }
    }

    private void renderGlyphSelectionHighlights(PoseStack poseStack) {
        List<CanvasRect> mask = new ArrayList<>();
        for (SFMDrawCanvasModel.CanvasGlyph glyph : model().glyphs()) {
            if (uniqueCursorInGlyphBounds(glyph) == null) {
                continue;
            }
            mask.add(new CanvasRect(
                    (int) Math.floor(canvasToScreenX(glyph.x())),
                    (int) Math.floor(canvasToScreenY(glyph.y())),
                    (int) Math.ceil(canvasToScreenX(glyph.x() + glyph.width())),
                    (int) Math.ceil(canvasToScreenY(glyph.y() + this.font.lineHeight))
            ));
        }
        for (CanvasRect rect : unionRects(mask)) {
            SFMScreenRenderUtils.renderHighlight(
                    poseStack,
                    rect.left(),
                    rect.top(),
                    Math.max(rect.left() + 1, rect.right()),
                    Math.max(rect.top() + 1, rect.bottom())
            );
        }
    }

    private List<CanvasRect> unionRects(List<CanvasRect> sourceRects) {
        List<CanvasRect> merged = new ArrayList<>();
        for (CanvasRect source : sourceRects) {
            CanvasRect pending = source;
            boolean changed;
            do {
                changed = false;
                for (int i = 0; i < merged.size(); i++) {
                    CanvasRect existing = merged.get(i);
                    if (pending.touchesOrOverlaps(existing)) {
                        pending = pending.union(existing);
                        merged.remove(i);
                        changed = true;
                        break;
                    }
                }
            } while (changed);
            merged.add(pending);
        }
        return merged;
    }

    private void renderCursorTrail(PoseStack poseStack) {
        int count = cursorTrail.size();
        for (int i = 0; i < count; i++) {
            CanvasPoint point = cursorTrail.get(i);
            double age = count <= 1 ? 1.0D : (double) i / (double) (count - 1);
            int alpha = 32 + (int) Math.round(age * 176.0D);
            int color = (alpha << 24) | (CURSOR_TRAIL & 0x00FFFFFF);
            int screenX = (int) Math.round(canvasToScreenX(point.x()));
            int screenY = (int) Math.round(canvasToScreenY(point.y()));
            int size = Math.max(1, (int) Math.round(2.0D * zoom));
            fill(poseStack, screenX - size, screenY - size, screenX + size + 1, screenY + size + 1, color);
        }
    }

    private void drawRectOutline(
            PoseStack poseStack,
            int left,
            int top,
            int right,
            int bottom,
            int color
    ) {
        fill(poseStack, left, top, right, top + 1, color);
        fill(poseStack, left, bottom - 1, right, bottom, color);
        fill(poseStack, left, top, left + 1, bottom, color);
        fill(poseStack, right - 1, top, right, bottom, color);
    }

    private void renderCanvasCursor(PoseStack poseStack) {
        for (int i = 0; i < model().cursors().size(); i++) {
            SFMDrawCanvasModel.CanvasCursor cursor = model().cursors().get(i);
            if (!hideSelection && isUniqueCursorInAnyGlyphBounds(cursor)) {
                continue;
            }
            renderCanvasCursor(poseStack, cursor, i == model().focusedCursorIndex());
        }
    }

    private void renderCanvasCursor(
            PoseStack poseStack,
            SFMDrawCanvasModel.CanvasCursor cursor,
            boolean focused
    ) {
        int mouseX = (int) Math.round(canvasToScreenX(cursor.x()));
        int mouseY = (int) Math.round(canvasToScreenY(cursor.y()));
        int size = panning ? 8 : 6;
        int cursorSize = cursor.active() ? size + 2 : size;
        if (focused) {
            drawCrosshair(poseStack, mouseX, mouseY, cursorSize + 2, focusedCursorOutlineColor(cursor.color()));
        }
        drawCrosshair(poseStack, mouseX, mouseY, cursorSize, cursor.active() ? cursor.color() : inactiveCursorColor(cursor.color()));
    }

    private void drawCrosshair(
            PoseStack poseStack,
            int mouseX,
            int mouseY,
            int size,
            int color
    ) {
        fill(poseStack, mouseX - size, mouseY, mouseX - 2, mouseY + 1, color);
        fill(poseStack, mouseX + 3, mouseY, mouseX + size + 1, mouseY + 1, color);
        fill(poseStack, mouseX, mouseY - size, mouseX + 1, mouseY - 2, color);
        fill(poseStack, mouseX, mouseY + 3, mouseX + 1, mouseY + size + 1, color);
        fill(poseStack, mouseX, mouseY, mouseX + 1, mouseY + 1, color);
    }

    private int inactiveCursorColor(int color) {
        return 0x88000000 | (color & 0x00FFFFFF);
    }

    private int focusedCursorOutlineColor(int color) {
        int red = Math.min(255, ((color >> 16) & 0xFF) + 56);
        int green = Math.min(255, ((color >> 8) & 0xFF) + 56);
        int blue = Math.min(255, (color & 0xFF) + 56);
        return 0xFF000000 | (red << 16) | (green << 8) | blue;
    }

    private SFMDrawCanvasModel.CanvasCursor uniqueCursorInGlyphBounds(SFMDrawCanvasModel.CanvasGlyph glyph) {
        SFMDrawCanvasModel.CanvasCursor selected = null;
        for (SFMDrawCanvasModel.CanvasCursor cursor : model().cursors()) {
            if (!cursorInGlyphBounds(cursor, glyph)) {
                continue;
            }
            if (selected != null) {
                return null;
            }
            selected = cursor;
        }
        return selected;
    }

    private boolean isUniqueCursorInAnyGlyphBounds(SFMDrawCanvasModel.CanvasCursor cursor) {
        for (SFMDrawCanvasModel.CanvasGlyph glyph : model().glyphs()) {
            if (uniqueCursorInGlyphBounds(glyph) == cursor) {
                return true;
            }
        }
        return false;
    }

    private boolean cursorInGlyphBounds(
            SFMDrawCanvasModel.CanvasCursor cursor,
            SFMDrawCanvasModel.CanvasGlyph glyph
    ) {
        return cursor.x() >= glyph.x()
               && cursor.x() < glyph.x() + glyph.width()
               && cursor.y() >= glyph.y()
               && cursor.y() < glyph.y() + this.font.lineHeight;
    }

    private void rememberCursorPosition() {
        if (!showCursorTrail && cursorTrail.isEmpty()) {
            return;
        }
        if (!cursorTrail.isEmpty()) {
            CanvasPoint previous = cursorTrail.get(cursorTrail.size() - 1);
            double dx = model().cursorCanvasX() - previous.x();
            double dy = model().cursorCanvasY() - previous.y();
            if (dx * dx + dy * dy < CURSOR_TRAIL_MIN_DISTANCE * CURSOR_TRAIL_MIN_DISTANCE) {
                return;
            }
        }
        cursorTrail.add(new CanvasPoint(model().cursorCanvasX(), model().cursorCanvasY()));
        while (cursorTrail.size() > CURSOR_TRAIL_LIMIT) {
            cursorTrail.remove(0);
        }
    }

    private void insertLineBreak() {
        model().insertLineBreak(this.font.lineHeight);
        rememberCursorPosition();
    }

    private void rememberInputEvent(String event) {
        inputEvents.add(event);
        while (inputEvents.size() > INPUT_LOG_LIMIT) {
            inputEvents.remove(0);
        }
    }

    private void renderHud(PoseStack poseStack) {
        int left = 8;
        int top = diagnosticControlsVisible ? 104 : 8;
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
                        model().cursorCanvasX(),
                        model().cursorCanvasY(),
                        zoom
                ),
                left + 8,
                top + 22,
                HUD_MUTED
        );
    }

    private void renderInputDiagnostics(PoseStack poseStack) {
        if (inputEvents.isEmpty()) {
            return;
        }
        int left = 8;
        int lineHeight = this.font.lineHeight + 2;
        int height = inputEvents.size() * lineHeight + 12;
        int top = Math.max(112, this.height - height - 8);
        int right = Math.min(this.width - 8, 420);
        int bottom = top + height;
        fill(poseStack, left, top, right, bottom, HUD_BACKGROUND);
        fill(poseStack, left, top, right, top + 1, HUD_BORDER);
        fill(poseStack, left, bottom - 1, right, bottom, HUD_BORDER);
        fill(poseStack, left, top, left + 1, bottom, HUD_BORDER);
        fill(poseStack, right - 1, top, right, bottom, HUD_BORDER);
        int y = top + 6;
        for (String event : inputEvents) {
            drawString(poseStack, this.font, event, left + 6, y, HUD_MUTED);
            y += lineHeight;
        }
    }

    private String modifierText(int modifiers) {
        List<String> parts = new ArrayList<>();
        if ((modifiers & GLFW.GLFW_MOD_CONTROL) != 0) {
            parts.add("ctrl");
        }
        if ((modifiers & GLFW.GLFW_MOD_SHIFT) != 0) {
            parts.add("shift");
        }
        if ((modifiers & GLFW.GLFW_MOD_ALT) != 0) {
            parts.add("alt");
        }
        if ((modifiers & GLFW.GLFW_MOD_SUPER) != 0) {
            parts.add("super");
        }
        return parts.isEmpty() ? "none" : String.join("+", parts);
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

    private record CanvasPoint(
            double x,
            double y
    ) {
    }

    private record CanvasRect(
            int left,
            int top,
            int right,
            int bottom
    ) {
        public boolean touchesOrOverlaps(CanvasRect other) {
            return this.left <= other.right
                   && this.right >= other.left
                   && this.top <= other.bottom
                   && this.bottom >= other.top;
        }

        public CanvasRect union(CanvasRect other) {
            return new CanvasRect(
                    Math.min(this.left, other.left),
                    Math.min(this.top, other.top),
                    Math.max(this.right, other.right),
                    Math.max(this.bottom, other.bottom)
            );
        }
    }

    private interface ToggleReader {
        boolean get();
    }

    private interface ToggleWriter {
        void set(boolean value);
    }

    private static class CanvasFocusTarget extends Button {
        public CanvasFocusTarget(
                int x,
                int y,
                int width,
                int height
        ) {
            super(x, y, width, height, Component.empty(), button -> { });
        }

        @Override
        public void renderButton(
                PoseStack poseStack,
                int mouseX,
                int mouseY,
                float partialTick
        ) {
            // Invisible focus target for vanilla tab navigation.
        }

        @Override
        public boolean mouseClicked(
                double mouseX,
                double mouseY,
                int button
        ) {
            return false;
        }

        @Override
        public boolean keyPressed(
                int keyCode,
                int scanCode,
                int modifiers
        ) {
            return keyCode == GLFW.GLFW_KEY_SPACE
                   || keyCode == GLFW.GLFW_KEY_ENTER
                   || keyCode == GLFW.GLFW_KEY_KP_ENTER;
        }
    }
}
