package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.client.ProgramSyntaxHighlightingHelper;
import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.item.DiskItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static ca.teamdman.sfm.common.Constants.LocalizationKeys.PROGRAM_EDIT_SCREEN_DONE_BUTTON_TOOLTIP;

public class ProgramEditScreen extends Screen {
    private final Consumer<String> CALLBACK;
    private final ItemStack DISK_STACK;
    private MultiLineEditBox textarea;
    private Button doneButton;

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

    public static MutableComponent insertStringAt(MutableComponent component, String seq, int position) {
        var rtn = Component.empty();
        int currentPosition = 0;
        for (Component sibling : component.getSiblings()) {
            String siblingText = sibling.getString();
            int siblingLength = siblingText.length();

            if (currentPosition + siblingLength <= position) {
                rtn.append(sibling);
                currentPosition += siblingLength;
            } else {
                int insertPositionInSibling = position - currentPosition;

                String beforeInsert = siblingText.substring(0, insertPositionInSibling);
                String afterInsert = siblingText.substring(insertPositionInSibling);

                if (!beforeInsert.isEmpty()) {
                    rtn.append(Component.literal(beforeInsert).withStyle(sibling.getStyle()));
                }
                rtn.append(Component.literal(seq).withStyle(sibling.getStyle()));
                if (!afterInsert.isEmpty()) {
                    rtn.append(Component.literal(afterInsert).withStyle(sibling.getStyle()));
                }

                currentPosition = position + 1;
                break;
            }
        }

        // Append the remaining siblings after the inserted sequence
        for (int i = currentPosition; i < component.getSiblings().size(); i++) {
            rtn.append(component.getSiblings().get(i));
        }

        return rtn;
    }

    @Override
    protected void init() {
        super.init();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.textarea = this.addRenderableWidget(new MyMultiLineEditBox());
        textarea.setValue(DiskItem.getProgram(DISK_STACK));
        this.setInitialFocus(textarea);

        this.doneButton = this.addRenderableWidget(new Button(
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
        onClose();
        CALLBACK.accept(textarea.getValue());
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        if ((pKeyCode == GLFW.GLFW_KEY_ENTER || pKeyCode == GLFW.GLFW_KEY_KP_ENTER) && Screen.hasShiftDown()) {
            onDone();
            return true;
        }
        if (pKeyCode == GLFW.GLFW_KEY_TAB) {
//            textarea.charTyped('\t', pKeyCode);
            textarea.charTyped(' ', GLFW.GLFW_KEY_SPACE);
            textarea.charTyped(' ', GLFW.GLFW_KEY_SPACE);
            textarea.charTyped(' ', GLFW.GLFW_KEY_SPACE);
            textarea.charTyped(' ', GLFW.GLFW_KEY_SPACE);
            return true;
        }
        return super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public void resize(Minecraft mc, int x, int y) {
        init(mc, x, y);
        var prev = this.textarea.getValue();
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

        @Override
        public boolean mouseClicked(double p_239101_, double p_239102_, int p_239103_) {
            try {
                return super.mouseClicked(p_239101_, p_239102_, p_239103_);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void renderContents(PoseStack poseStack, int mx, int my, float partialTicks) {
            var matrix4f = poseStack.last().pose();
            var buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

            var components = ProgramSyntaxHighlightingHelper.withSyntaxHighlighting(this.textField.value());
            boolean isCursorVisible = this.isFocused() && this.frame / 6 % 2 == 0;
            boolean isCursorAtEndOfLine = false;
            int cursorIndex = textField.cursor();
            int lineX = this.x + this.innerPadding(), lineY = this.y
                                                              + this.innerPadding(), charCount = 0, cursorX = 0, cursorY = 0;
            for (int line = 0; line < components.size(); ++line) {
                var component = components.get(line);
                int lineLength = component.getString().length();
                int lineHeight = this.font.lineHeight + (line == 0 ? 2 : 0);
                boolean cursorOnThisLine = isCursorVisible
                                           && cursorIndex >= charCount
                                           && cursorIndex <= charCount + lineLength;
                if (cursorOnThisLine) {
//                    component = insertStringAt(component, "_", cursorIndex - charCount);
                    isCursorAtEndOfLine = cursorIndex == charCount + lineLength;
                    cursorY = lineY;
                    cursorX = this.font.drawInBatch(
                            substring(component, 0, cursorIndex - charCount),
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
                            substring(component, cursorIndex - charCount, lineLength),
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
                            component,
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
                lineY += lineHeight;
                charCount += lineLength + 1;
            }
            buffer.endBatch();

            // this has to happen AFTER endBatch since it starts a new batch
            if (isCursorAtEndOfLine) {
                this.font.drawShadow(poseStack, "_", cursorX, cursorY, -1);
            } else {
                GuiComponent.fill(poseStack, cursorX, cursorY - 1, cursorX + 1, cursorY + 1 + 9, -1);
            }
        }
    }
}

