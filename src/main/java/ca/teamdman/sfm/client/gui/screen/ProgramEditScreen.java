package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.client.ProgramSyntaxHighlightingHelper;
import ca.teamdman.sfm.client.gui.IndentationUtils;
import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.item.DiskItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.gui.components.Whence;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static ca.teamdman.sfm.common.Constants.LocalizationKeys.PROGRAM_EDIT_SCREEN_DONE_BUTTON_TOOLTIP;

public class ProgramEditScreen extends Screen {
    private final Consumer<String> CALLBACK;
    private final ItemStack DISK_STACK;
    @SuppressWarnings("NotNullFieldNotInitialized")
    private MyMultiLineEditBox textarea;
    private String lastProgram = "";
    private List<MutableComponent> lastProgramWithSyntaxHighlighting = Collections.emptyList();

    public ProgramEditScreen(ItemStack diskStack, Consumer<String> callback) {
        super(Constants.LocalizationKeys.PROGRAM_EDIT_SCREEN_TITLE.getComponent());
        this.DISK_STACK = diskStack;
        this.CALLBACK = callback;
    }

    public static MutableComponent substring(MutableComponent component, int start, int end) {
        var rtn = Component.empty();
        AtomicInteger seen = new AtomicInteger(0);
        component.visit((style, content) -> {
            int contentStart = Math.max(start - seen.get(), 0);
            int contentEnd = Math.min(end - seen.get(), content.length());

            if (contentStart < contentEnd) {
                rtn.append(Component.literal(content.substring(contentStart, contentEnd)).withStyle(style));
            }
            seen.addAndGet(content.length());
            return Optional.empty();
        }, Style.EMPTY);
        return rtn;
    }

    @Override
    protected void init() {
        super.init();
        assert this.minecraft != null;
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.textarea = this.addRenderableWidget(new MyMultiLineEditBox());
        textarea.setValue(DiskItem.getProgram(DISK_STACK));
        this.setInitialFocus(textarea);

        this.addRenderableWidget(new Button(
                this.width / 2 - 2 - 150,
                this.height / 2 - 100 + 195,
                300,
                20,
                CommonComponents.GUI_DONE,
                (p_97691_) -> this.onDone(),
                (btn, pose, mx, my) -> renderTooltip(
                        pose,
                        font.split(
                                PROGRAM_EDIT_SCREEN_DONE_BUTTON_TOOLTIP.getComponent(),
                                Math.max(
                                        width
                                        / 2
                                        - 43,
                                        170
                                )
                        ),
                        mx,
                        my
                )
        ));
    }

    public void onDone() {
        CALLBACK.accept(textarea.getValue());
        onClose();
    }

    /**
     * Remove a layer of indentation from a line of text
     */
    public static String leftTrim4(String s) {
        int i = 0;
        while (i < s.length() && s.charAt(i) == ' ' && i < 4) {
            i++;
        }
        return s.substring(i);
    }

    public static int findStartOfLine(String content, int start) {
        for (int i = start; i > 0; i--) {
            if (content.charAt(i - 1) == '\n') {
                return i;
            }
        }
        return 0;
    }

    public static int findEndOfLine(String content, int start) {
        for (int i = start; i < content.length(); i++) {
            if (content.charAt(i) == '\n') {
                return i;
            }
        }
        return content.length();
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if ((pKeyCode == GLFW.GLFW_KEY_ENTER || pKeyCode == GLFW.GLFW_KEY_KP_ENTER) && Screen.hasShiftDown()) {
            onDone();
            return true;
        }
        if (pKeyCode == GLFW.GLFW_KEY_TAB) {
            // if tab pressed with no selection and not holding shift => insert 4 spaces
            // if tab pressed with no selection and holding shift => de-indent current line
            // if tab pressed with selection and not holding shift => de-indent lines containing selection 4 spaces
            // if tab pressed with selection and holding shift => indent lines containing selection 4 spaces
            String content = textarea.getValue();
            int cursor = textarea.getCursorPosition();
            int selectionCursor = textarea.getSelectionCursorPosition();
            IndentationUtils.IndentationResult result;
            if (Screen.hasShiftDown()) { // de-indent
                result = IndentationUtils.deindent(content, cursor, selectionCursor);
            } else { // indent
                result = IndentationUtils.indent(content, cursor, selectionCursor);
            }
            textarea.setValue(result.content());
            textarea.setCursorPosition(result.cursorPosition());
            textarea.setSelectionCursorPosition(result.selectionCursorPosition());
//            this.textarea.getSelected().ifPresentOrElse(selectionView -> { // selection present
//                int oldSelectionStart = selectionView.beginIndex();
//                int oldSelectionEnd = selectionView.endIndex();
//                int oldSelectionSize = oldSelectionEnd - oldSelectionStart;
//                boolean cursorAtStart = textarea.getCursorPosition() == oldSelectionStart;
//                int chunkStart = findStartOfLine(content, oldSelectionStart);
//                int chunkEnd = findEndOfLine(content, oldSelectionEnd);
//                String chunk = content.substring(chunkStart, chunkEnd);
//                int chunkSelectionStart = oldSelectionStart - chunkStart;
//                int chunkSelectionEnd = oldSelectionEnd - chunkStart;
//                if (Screen.hasShiftDown()) { // de-indent
//                    String[] chunkLines = chunk.split("\n", -1);
//                    String[] newChunkLines = new String[chunkLines.length];
//                    int totalRemoved = 0;
//                    int removedFromSelectionStart = 0;
//                    for (int i = 0; i < chunkLines.length; i++) {
//                        newChunkLines[i] = leftTrim4(chunkLines[i]);
//                        int removed = chunkLines[i].length() - newChunkLines[i].length();
//                        if (i == 0) {
//                            removedFromSelectionStart = Math.min(removed, chunkSelectionStart);
//                        }
//                        if (chunkSelectionStart < chunkLines[i].length() && chunkSelectionEnd > 0) {
//                            totalRemoved += removed;
//                        }
//                    }
//                    String newChunk = String.join("\n", newChunkLines);
//                    String newContent = content.substring(0, chunkStart)
//                                        + newChunk
//                                        + content.substring(chunkEnd);
//                    textarea.setValue(newContent);
//
//                    int newSelectionStart = oldSelectionStart - removedFromSelectionStart;
//                    int newSelectionEnd = newSelectionStart + oldSelectionSize - totalRemoved;
//                    textarea.setSelected(newSelectionStart, newSelectionEnd, cursorAtStart);
//                } else { // indent
//                    String[] chunkLines = chunk.split("\n", -1);
//                    String[] newChunkLines = new String[chunkLines.length];
//                    int added = 0;
//                    for (int i = 0; i < chunkLines.length; i++) {
//                        newChunkLines[i] = "    " + chunkLines[i];
//                        added += 4;
//                    }
//                    String newChunk = String.join("\n", newChunkLines);
//                    String newContent = content.substring(0, chunkStart)
//                                        + newChunk
//                                        + content.substring(chunkEnd);
//                    textarea.setValue(newContent);
//                    // the first line always gets +4 when indenting
//                    int newSelectionStart = oldSelectionStart + 4;
//                    int newSelectionEnd = oldSelectionEnd + added;
//                    textarea.setSelected(newSelectionStart, newSelectionEnd, cursorAtStart);
//                }
//            }, () -> { // no selection
//                if (Screen.hasShiftDown()) { // de-indent
//                    int oldCursor = textarea.getCursorPosition();
//                    int startOfLine = findStartOfLine(content, textarea.getCursorPosition());
//
//                    // count up to 4 whitespace characters to remove
//                    int end = startOfLine;
//                    for (int i = 4; i > 0; i--) {
//                        if (content.substring(startOfLine, startOfLine + i).isBlank()) {
//                            end = startOfLine + i;
//                            break;
//                        }
//                    }
//                    if (end > startOfLine) {
//                        // commit new content
//                        String newContent = content.substring(0, startOfLine) + content.substring(end);
//                        textarea.setValue(newContent);
//                        textarea.setSelecting(false);
//                        int newCursor = Math.max(startOfLine, oldCursor - (end - startOfLine));
//                        textarea.seekCursor(Whence.ABSOLUTE, newCursor);
//                    }
//                } else { // insert 4 spaces
//                    textarea.charTyped(' ', GLFW.GLFW_KEY_SPACE);
//                    textarea.charTyped(' ', GLFW.GLFW_KEY_SPACE);
//                    textarea.charTyped(' ', GLFW.GLFW_KEY_SPACE);
//                    textarea.charTyped(' ', GLFW.GLFW_KEY_SPACE);
//                }
//            });
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void resize(Minecraft mc, int x, int y) {
        var prev = this.textarea.getValue();
        init(mc, x, y);
        super.resize(mc, x, y);
        this.textarea.setValue(prev);
    }

    @Override
    public void render(PoseStack poseStack, int mx, int my, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mx, my, partialTicks);
    }

    private class MyMultiLineEditBox extends MultiLineEditBox {
        public MyMultiLineEditBox() {
            super(
                    ProgramEditScreen.this.font,
                    ProgramEditScreen.this.width / 2 - 200,
                    ProgramEditScreen.this.height / 2 - 110,
                    400,
                    200,
                    Component.literal(""),
                    Component.literal("")
            );
        }

        public void seekCursor(Whence whence, int amount) {
            this.textField.seekCursor(whence, amount);
        }

        public void setSelected(int start, int end, boolean cursorAtStart) {
            if (cursorAtStart) {
                // flip start and end
                int x = start;
                start = end;
                end = x;
            }
            this.textField.setSelecting(false);
            this.textField.seekCursor(Whence.ABSOLUTE, start);
            this.textField.setSelecting(true);
            this.textField.seekCursor(Whence.ABSOLUTE, end);
        }

        public int getCursorPosition() {
            return this.textField.cursor;
        }

        public void setCursorPosition(int cursor) {
            this.textField.cursor = cursor;
        }

        public void setSelecting(boolean selecting) {
            this.textField.setSelecting(selecting);
        }

        @Override
        public boolean mouseClicked(double p_239101_, double p_239102_, int p_239103_) {
            try {
                return super.mouseClicked(p_239101_, p_239102_, p_239103_);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        public Optional<MultilineTextField.StringView> getSelected() {
            if (!this.textField.hasSelection()) return Optional.empty();
            return Optional.of(this.textField.getSelected());
        }

        public int getSelectionCursorPosition() {
            return this.textField.selectCursor;
        }

        public void setSelectionCursorPosition(int cursor) {
            this.textField.selectCursor = cursor;
        }

        @Override
        protected void renderContents(PoseStack poseStack, int mx, int my, float partialTicks) {
            Matrix4f matrix4f = poseStack.last().pose();
            if (!lastProgram.equals(this.textField.value())) {
                lastProgram = this.textField.value();
                lastProgramWithSyntaxHighlighting = ProgramSyntaxHighlightingHelper.withSyntaxHighlighting(lastProgram);
            }
            List<MutableComponent> lines = lastProgramWithSyntaxHighlighting;
            boolean isCursorVisible = this.isFocused() && this.frame / 6 % 2 == 0;
            boolean isCursorAtEndOfLine = false;
            int cursorIndex = textField.cursor();
            int lineX = this.x + this.innerPadding();
            int lineY = this.y + this.innerPadding();
            int charCount = 0;
            int cursorX = 0;
            int cursorY = 0;
            MultilineTextField.StringView selectedRange = this.textField.getSelected();
            int selectionStart = selectedRange.beginIndex();
            int selectionEnd = selectedRange.endIndex();

            for (int line = 0; line < lines.size(); ++line) {
                var componentColoured = lines.get(line);
                int lineLength = componentColoured.getString().length();
                int lineHeight = this.font.lineHeight + (line == 0 ? 2 : 0);
                boolean cursorOnThisLine = isCursorVisible
                                           && cursorIndex >= charCount
                                           && cursorIndex <= charCount + lineLength;
                var buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

                if (cursorOnThisLine) {
                    isCursorAtEndOfLine = cursorIndex == charCount + lineLength;
                    cursorY = lineY;
                    // we draw the raw before coloured in case of token recognition errors
                    // draw before cursor
                    cursorX = this.font.drawInBatch(
                            substring(componentColoured, 0, cursorIndex - charCount),
                            lineX,
                            lineY,
                            -1,
                            true,
                            matrix4f,
                            buffer,
                            false,
                            0,
                            LightTexture.FULL_BRIGHT
                    ) - 1;
                    this.font.drawInBatch(
                            substring(componentColoured, cursorIndex - charCount, lineLength),
                            cursorX,
                            lineY,
                            -1,
                            true,
                            matrix4f,
                            buffer,
                            false,
                            0,
                            LightTexture.FULL_BRIGHT
                    );
                } else {
                    this.font.drawInBatch(
                            componentColoured,
                            lineX,
                            lineY,
                            -1,
                            true,
                            matrix4f,
                            buffer,
                            false,
                            0,
                            LightTexture.FULL_BRIGHT
                    );
                }
                buffer.endBatch();

                // Check if the selection is within the current line
                if (selectionStart <= charCount + lineLength && selectionEnd > charCount) {
                    int lineSelectionStart = Math.max(selectionStart - charCount, 0);
                    int lineSelectionEnd = Math.min(selectionEnd - charCount, lineLength);

                    int highlightStartX = this.font.width(substring(componentColoured, 0, lineSelectionStart));
                    int highlightEndX = this.font.width(substring(componentColoured, 0, lineSelectionEnd));

                    this.renderHighlight(
                            poseStack,
                            lineX + highlightStartX,
                            lineY,
                            lineX + highlightEndX,
                            lineY + lineHeight
                    );
                }

                lineY += lineHeight;
                charCount += lineLength + 1;
            }

            if (isCursorAtEndOfLine) {
                this.font.drawShadow(poseStack, "_", cursorX, cursorY, -1);
            } else {
                GuiComponent.fill(poseStack, cursorX, cursorY - 1, cursorX + 1, cursorY + 1 + 9, -1);
            }
        }

    }
}

