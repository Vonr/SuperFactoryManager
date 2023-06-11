package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.client.ProgramSyntaxHighlightingHelper;
import ca.teamdman.sfm.client.gui.IndentationUtils;
import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.item.DiskItem;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.Whence;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
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
        this.textarea = this.addRenderableWidget(new MyMultiLineEditBox());
        textarea.setValue(DiskItem.getProgram(DISK_STACK));
        this.setInitialFocus(textarea);

        this.addRenderableWidget(new ExtendedButtonWithTooltip(
                this.width / 2 - 2 - 150,
                this.height / 2 - 100 + 195,
                300,
                20,
                CommonComponents.GUI_DONE,
                (p_97691_) -> this.onDone(),
                Tooltip.create(PROGRAM_EDIT_SCREEN_DONE_BUTTON_TOOLTIP.getComponent())
        ));
    }

    public void onDone() {
        CALLBACK.accept(textarea.getValue());
        onClose();
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
    public void render(GuiGraphics graphics, int mx, int my, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mx, my, partialTicks);
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
        public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
            // we need to override the default behaviour because Mojang broke it
            // if it's not scrolling, it should return false for cursor click movement
            boolean rtn;
            if (!this.visible) {
                rtn = false;
            } else {
                boolean flag = this.withinContentAreaPoint(pMouseX, pMouseY);
                boolean flag1 = this.scrollbarVisible()
                                && pMouseX >= (double) (this.getX() + this.width)
                                && pMouseX <= (double) (this.getX() + this.width + 8)
                                && pMouseY >= (double) this.getY()
                                && pMouseY < (double) (this.getY() + this.height);
                if (flag1 && pButton == 0) {
                    this.scrolling = true;
                    rtn = true;
                } else {
                    //1.19.4 behaviour:
                    //rtn=flag || flag1;
                    // instead, we want to return false if we're not scrolling
                    // (like how it was in 1.19.2)
                    // https://bugs.mojang.com/browse/MC-262754
                    rtn = false;
                }
            }

            if (rtn) {
                return true;
            } else if (this.withinContentAreaPoint(pMouseX, pMouseY) && pButton == 0) {
                this.textField.setSelecting(Screen.hasShiftDown());
                this.seekCursorScreen(pMouseX, pMouseY);
                return true;
            } else {
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
        protected void renderContents(GuiGraphics graphics, int mx, int my, float partialTicks) {
            Matrix4f matrix4f = graphics.pose().last().pose();
            if (!lastProgram.equals(this.textField.value())) {
                lastProgram = this.textField.value();
                lastProgramWithSyntaxHighlighting = ProgramSyntaxHighlightingHelper.withSyntaxHighlighting(lastProgram);
            }
            List<MutableComponent> lines = lastProgramWithSyntaxHighlighting;
            boolean isCursorVisible = this.isFocused() && this.frame / 6 % 2 == 0;
            boolean isCursorAtEndOfLine = false;
            int cursorIndex = textField.cursor();
            int lineX = this.getX() + this.innerPadding();
            int lineY = this.getY() + this.innerPadding();
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
                            Font.DisplayMode.NORMAL,
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
                            Font.DisplayMode.NORMAL,
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
                            Font.DisplayMode.NORMAL,
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
                            graphics,
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
                graphics.drawString(this.font, "_", cursorX, cursorY, -1);
            } else {
                graphics.fill(cursorX, cursorY - 1, cursorX + 1, cursorY + 1 + 9, -1);
            }
        }
    }
}

