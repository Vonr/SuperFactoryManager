package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.common.localization.LocalizationKeys;
import ca.teamdman.sfm.common.net.ServerboundLabelGunClearPacket;
import ca.teamdman.sfm.common.net.ServerboundLabelGunPrunePacket;
import ca.teamdman.sfm.common.net.ServerboundLabelGunUpdatePacket;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import ca.teamdman.sfm.common.registry.SFMPackets;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

import java.util.Comparator;

public class LabelGunScreen extends Screen {
    private final InteractionHand HAND;
    private final LabelPositionHolder LABEL_HOLDER;
    @SuppressWarnings("NotNullFieldNotInitialized")
    private EditBox labelField;
    private boolean shouldRebuildWidgets = false;

    public LabelGunScreen(ItemStack labelGunStack, InteractionHand hand) {
        super(LocalizationKeys.LABEL_GUN_GUI_TITLE.getComponent());
        LABEL_HOLDER = LabelPositionHolder.from(labelGunStack);
        HAND = hand;
    }

    @Override
    protected void init() {
        super.init();
        assert this.minecraft != null;
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.labelField = addRenderableWidget(new EditBox(
                this.font,
                this.width / 2 - 150,
                50,
                300,
                20,
                LocalizationKeys.LABEL_GUN_GUI_LABEL_PLACEHOLDER.getComponent()
        ));
        this.setInitialFocus(labelField);
        this.setFocused(labelField);
        this.labelField.setFocus(true);

        this.addRenderableWidget(new Button(
                this.width / 2 - 210,
                50,
                50,
                20,
                LocalizationKeys.LABEL_GUN_GUI_CLEAR_BUTTON.getComponent(),
                (btn) -> {
                    SFMPackets.LABEL_GUN_ITEM_CHANNEL.sendToServer(new ServerboundLabelGunClearPacket(HAND));
                    LABEL_HOLDER.clear();
                    shouldRebuildWidgets = true;
                }
        ));
        this.addRenderableWidget(new Button(
                this.width / 2 + 160,
                50,
                50,
                20,
                LocalizationKeys.LABEL_GUN_GUI_PRUNE_BUTTON.getComponent(),
                (btn) -> {
                    SFMPackets.LABEL_GUN_ITEM_CHANNEL.sendToServer(new ServerboundLabelGunPrunePacket(HAND));
                    LABEL_HOLDER.prune();
                    shouldRebuildWidgets = true;
                }
        ));
        this.addRenderableWidget(new Button(
                this.width / 2 - 2 - 150,
                this.height - 50,
                300,
                20,
                CommonComponents.GUI_DONE,
                (p_97691_) -> this.onDone()
        ));
        {
            var labels = LABEL_HOLDER.get().keySet().stream().sorted(Comparator.naturalOrder()).toList();
            int i = 0;
            int buttonWidth = LABEL_HOLDER.get()
                                      .entrySet()
                                      .stream()
                                      .map(entry -> LocalizationKeys.LABEL_GUN_GUI_LABEL_BUTTON.getComponent(
                                              entry.getKey(),
                                              entry.getValue().size()
                                      ).getString())
                                      .mapToInt(this.font::width)
                                      .max().orElse(50) + 10;
            int buttonHeight = 20;
            int paddingX = 5;
            int paddingY = 5;
            int buttonsPerRow = this.width / (buttonWidth + paddingX);
            for (var label : labels) {
                int x = (this.width - (buttonWidth + paddingX) * Math.min(buttonsPerRow, labels.size())) / 2
                        + paddingX
                        + (i % buttonsPerRow) * (
                        buttonWidth
                        + paddingX
                );
                int y = 80 + (i / buttonsPerRow) * (buttonHeight + paddingY);
                int count = LABEL_HOLDER.getPositions(label).size();
                this.addRenderableWidget(new Button(
                        x,
                        y,
                        buttonWidth,
                        buttonHeight,
                        LocalizationKeys.LABEL_GUN_GUI_LABEL_BUTTON.getComponent(label, count),
                        (btn) -> {
                            this.labelField.setValue(label);
                            this.onDone();
                        }
                ));
                i++;
            }
        }
    }

    @Override
    public boolean keyPressed(int key, int mod1, int mod2) {
        if (super.keyPressed(key, mod1, mod2)) return true;
        if (key != GLFW.GLFW_KEY_ENTER && key != GLFW.GLFW_KEY_KP_ENTER) return false;
        onDone();
        return true;
    }

    public void onDone() {
        SFMPackets.LABEL_GUN_ITEM_CHANNEL.sendToServer(new ServerboundLabelGunUpdatePacket(
                labelField.getValue(),
                HAND
        ));
        onClose();
    }

    @Override
    public void resize(Minecraft mc, int x, int y) {
        var prev = this.labelField.getValue();
        init(mc, x, y);
        super.resize(mc, x, y);
        this.labelField.setValue(prev);
    }

    @Override
    public void render(PoseStack poseStack, int mx, int my, float partialTicks) {
        if (shouldRebuildWidgets) {
            // we delay this because focus gets reset _after_ the button event handler
            // we want to end with the label input field focused
            shouldRebuildWidgets = false;
            rebuildWidgets();
        }
        this.renderBackground(poseStack);
        super.render(poseStack, mx, my, partialTicks);
    }
}
