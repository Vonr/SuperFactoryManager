package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.client.ClientStuff;
import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.logging.TranslatableLogEvent;
import ca.teamdman.sfm.common.net.ServerboundManagerClearLogsPacket;
import ca.teamdman.sfm.common.net.ServerboundManagerSetLogLevelPacket;
import ca.teamdman.sfm.common.registry.SFMPackets;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.time.MutableInstant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ca.teamdman.sfm.common.Constants.LocalizationKeys.PROGRAM_EDIT_SCREEN_DONE_BUTTON_TOOLTIP;

// todo: checkbox for auto-scrolling
// todo: clear button
public class LogsScreen extends Screen {
    private final ManagerContainerMenu MENU;
    @SuppressWarnings("NotNullFieldNotInitialized")
    private MyMultiLineEditBox textarea;
    private List<MutableComponent> content = Collections.emptyList();
    private int lastSize = 0;


    public LogsScreen(ManagerContainerMenu menu) {
        super(Constants.LocalizationKeys.LOGS_SCREEN_TITLE.getComponent());
        this.MENU = menu;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private boolean shouldRebuildText() {
        return MENU.logs.size() != lastSize;
//        return false;
    }

    private void rebuildText() {
        List<MutableComponent> processedLogs = new ArrayList<>();
        List<TranslatableLogEvent> toProcess = MENU.logs;
        if (toProcess.isEmpty() && MENU.logLevel.equals(Level.OFF.name())) {
            MutableInstant instant = new MutableInstant();
            instant.initFromEpochMilli(System.currentTimeMillis(), 0);
            toProcess.add(new TranslatableLogEvent(
                    Level.WARN,
                    instant,
                    Constants.LocalizationKeys.LOGS_GUI_NO_CONTENT.get()
            ));
        }
        for (TranslatableLogEvent log : toProcess) {
            int seconds = (int) (System.currentTimeMillis() - log.instant().getEpochMillisecond()) / 1000;
            int minutes = seconds / 60;
            seconds = seconds % 60;
            var ago = Component.literal(minutes + "m" + seconds + "s ago").withStyle(ChatFormatting.GRAY);

            var level = Component.literal(" [" + log.level() + "] ");
            if (log.level() == Level.ERROR) {
                level = level.withStyle(ChatFormatting.RED);
            } else if (log.level() == Level.WARN) {
                level = level.withStyle(ChatFormatting.YELLOW);
            } else if (log.level() == Level.INFO) {
                level = level.withStyle(ChatFormatting.GREEN);
            } else if (log.level() == Level.DEBUG) {
                level = level.withStyle(ChatFormatting.AQUA);
            } else if (log.level() == Level.TRACE) {
                level = level.withStyle(ChatFormatting.DARK_GRAY);
            }

            String[] lines = ClientStuff.resolveTranslation(log.contents()).split("\n", -1);

            for (int i = 0; i < lines.length; i++) {
                MutableComponent lineComponent;
                if (i == 0) {
                    lineComponent = ago
                            .append(level)
                            .append(Component.literal(lines[i]).withStyle(ChatFormatting.WHITE));
                } else {
                    lineComponent = Component.literal(lines[i]).withStyle(ChatFormatting.WHITE);
                }
                processedLogs.add(lineComponent);
            }
        }
        this.content = processedLogs;


        // update textarea with plain string contents so select and copy works
        StringBuilder sb = new StringBuilder();
        for (var line : this.content) {
            sb.append(line.getString()).append("\n");
        }
        textarea.setValue(sb.toString());
        lastSize = MENU.logs.size();
    }

    @Override
    protected void init() {
        super.init();
        assert this.minecraft != null;
        this.textarea = this.addRenderableWidget(new MyMultiLineEditBox());

        rebuildText();

        this.setInitialFocus(textarea);


        var buttons = new Level[]{
                Level.OFF,
                Level.TRACE,
                Level.DEBUG,
                Level.INFO,
                Level.WARN,
                Level.ERROR
        };
        int buttonWidth = 60;
        int buttonHeight = 20;
        int spacing = 5;
        int startX = (this.width - (buttonWidth * buttons.length + spacing * 4)) / 2;
        int startY = this.height / 2 - 115;
        int buttonIndex = 0;

        for (var level : buttons) {
            this.addRenderableWidget(new Button(
                    startX + (buttonWidth + spacing) * buttonIndex,
                    startY,
                    buttonWidth,
                    buttonHeight,
                    Component.literal(level.name()),
                    button -> {
                        String logLevel = level.name();
                        SFMPackets.MANAGER_CHANNEL.sendToServer(new ServerboundManagerSetLogLevelPacket(
                                MENU.containerId,
                                MENU.MANAGER_POSITION,
                                logLevel
                        ));
                        MENU.logLevel = logLevel;
                    }
            ));
            buttonIndex++;
        }

        this.addRenderableWidget(new Button(
                this.width / 2 - 2 - 100,
                this.height / 2 - 100 + 195,
                200,
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
        this.addRenderableWidget(new Button(
                this.width / 2 - 2 + 150,
                this.height / 2 - 100 + 195,
                80,
                20,
                Constants.LocalizationKeys.LOGS_GUI_CLEAR_LOGS_BUTTON.getComponent(),
                (button) -> {
                    SFMPackets.MANAGER_CHANNEL.sendToServer(new ServerboundManagerClearLogsPacket(
                            MENU.containerId,
                            MENU.MANAGER_POSITION
                    ));
                    MENU.logs.clear();
                }
        ));
    }

    public void onClosePerformCallback() {
        assert this.minecraft != null;
        this.minecraft.popGuiLayer();
    }

    public void scrollToBottom() {
        textarea.setScrollAmount(Double.MAX_VALUE);
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

        @Override
        protected void renderContents(PoseStack poseStack, int mx, int my, float partialTicks) {
            Matrix4f matrix4f = poseStack.last().pose();
            if (shouldRebuildText()) {
                rebuildText();
            }
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

            for (int line = 0; line < content.size(); ++line) {
                var componentColoured = content.get(line);
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
                            ProgramEditScreen.substring(componentColoured, 0, cursorIndex - charCount),
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
                            ProgramEditScreen.substring(componentColoured, cursorIndex - charCount, lineLength),
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

                    int highlightStartX = this.font.width(ProgramEditScreen.substring(
                            componentColoured,
                            0,
                            lineSelectionStart
                    ));
                    int highlightEndX = this.font.width(ProgramEditScreen.substring(
                            componentColoured,
                            0,
                            lineSelectionEnd
                    ));

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

