package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.item.DiskItem;
import ca.teamdman.sfm.common.net.ServerboundManagerProgramPacket;
import ca.teamdman.sfm.common.net.ServerboundManagerSetLogLevelPacket;
import ca.teamdman.sfm.common.registry.SFMPackets;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.apache.logging.log4j.Level;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static ca.teamdman.sfm.common.Constants.LocalizationKeys.MANAGER_GUI_STATUS_LOADED_CLIPBOARD;
import static ca.teamdman.sfm.common.Constants.LocalizationKeys.PROGRAM_EDIT_SCREEN_DONE_BUTTON_TOOLTIP;

public class LogsScreen extends Screen {
    private final ManagerContainerMenu MENU;
    @SuppressWarnings("NotNullFieldNotInitialized")
    private MyMultiLineEditBox textarea;
    private String lastContent = "";
    private List<MutableComponent> lastContentWithSyntaxHighlighting = Collections.emptyList();


    public LogsScreen(ManagerContainerMenu menu) {
        super(Constants.LocalizationKeys.LOGS_SCREEN_TITLE.getComponent());
        this.MENU = menu;
    }



    private void sendSetLogLevel(String logLevel) {
        SFMPackets.MANAGER_CHANNEL.sendToServer(new ServerboundManagerSetLogLevelPacket(
                MENU.containerId,
                MENU.MANAGER_POSITION,
                logLevel
        ));
        MENU.logLevel = logLevel;
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

        String content = DiskItem.getLogs(MENU.getDisk());
        if (content.isBlank()) {
            content = Constants.LocalizationKeys.LOGS_GUI_NO_CONTENT.getString();
        }
        textarea.setValue(content);

        this.setInitialFocus(textarea);



        int buttonWidth = 60;
        int buttonHeight = 20;
        int spacing = 5;
        int startX = (this.width - (buttonWidth * 5 + spacing * 4)) / 2;
        int startY = this.height / 2 - 115;

        this.addRenderableWidget(new Button(
                startX, startY, buttonWidth, buttonHeight,
                Component.literal("TRACE"), button -> sendSetLogLevel(Level.TRACE.name())
        ));
        //noinspection PointlessArithmeticExpression
        this.addRenderableWidget(new Button(
                startX + (buttonWidth + spacing) * 1, startY, buttonWidth, buttonHeight,
                Component.literal("DEBUG"), button -> sendSetLogLevel(Level.DEBUG.name())
        ));
        this.addRenderableWidget(new Button(
                startX + (buttonWidth + spacing) * 2, startY, buttonWidth, buttonHeight,
                Component.literal("INFO"), button -> sendSetLogLevel(Level.INFO.name())
        ));
        this.addRenderableWidget(new Button(
                startX + (buttonWidth + spacing) * 3, startY, buttonWidth, buttonHeight,
                Component.literal("WARN"), button -> sendSetLogLevel(Level.WARN.name())
        ));
        this.addRenderableWidget(new Button(
                startX + (buttonWidth + spacing) * 4, startY, buttonWidth, buttonHeight,
                Component.literal("ERROR"), button -> sendSetLogLevel(Level.ERROR.name())
        ));


        this.addRenderableWidget(new Button(
                this.width / 2 - 2 - 150,
                this.height / 2 - 100 + 195,
                300,
                20,
                CommonComponents.GUI_DONE,
                (p_97691_) -> this.onClosePerformCallback(),
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

    public void onClosePerformCallback() {
        assert this.minecraft != null;
        this.minecraft.popGuiLayer();
    }

    public void scrollToTop() {
        textarea.setScrollAmount(0d);
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
                    LogsScreen.this.font,
                    LogsScreen.this.width / 2 - 200,
                    LogsScreen.this.height / 2 - 90,
                    400,
                    180,
                    Component.literal(""),
                    Component.literal("")
            );
        }

        public int getCursorPosition() {
            return this.textField.cursor;
        }

        public void setCursorPosition(int cursor) {
            this.textField.cursor = cursor;
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

        public int getSelectionCursorPosition() {
            return this.textField.selectCursor;
        }

        public void setSelectionCursorPosition(int cursor) {
            this.textField.selectCursor = cursor;
        }

        private void rebuild() {
            lastContent = this.textField.value();
            lastContentWithSyntaxHighlighting = lastContent.lines().map(Component::literal).toList();
        }

        @Override
        protected void renderContents(PoseStack poseStack, int mx, int my, float partialTicks) {
            Matrix4f matrix4f = poseStack.last().pose();
            if (!lastContent.equals(this.textField.value())) {
                rebuild();
            }
            List<MutableComponent> lines = lastContentWithSyntaxHighlighting;
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

