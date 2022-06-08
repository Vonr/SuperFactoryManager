package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.common.net.ServerboundLabelGunUpdatePacket;
import ca.teamdman.sfm.common.registry.SFMPackets;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public class LabelGunScreen extends Screen {
    private final InteractionHand HAND;
    private final ItemStack       ITEM_STACK;
    private final String          label = "";
    private       EditBox         labelField;
    private       Button          doneButton;

    public LabelGunScreen(ItemStack labelGunStack, InteractionHand hand) {
        super(Component.translatable("gui.sfm.title.labelgun"));
        ITEM_STACK = labelGunStack;
        HAND       = hand;
    }

    @Override
    protected void init() {
        super.init();
        this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
        this.labelField = addRenderableWidget(new EditBox(
                this.font,
                this.width / 2 - 150,
                50,
                300,
                20,
                Component.translatable("gui.sfm.labels.labelgun.placeholder")
        ));
        this.setInitialFocus(labelField);
        this.labelField.setFocus(true);

        this.doneButton = this.addRenderableWidget(new Button(
                this.width / 2 - 2 - 150,
                this.height / 4 + 120 + 12,
                300,
                20,
                CommonComponents.GUI_DONE,
                (p_97691_) -> this.onDone()
        ));
    }

    @Override
    public boolean keyPressed(int key, int mod1, int mod2) {
        if (super.keyPressed(key, mod1, mod2)) return true;
        if (key != GLFW.GLFW_KEY_ENTER && key != GLFW.GLFW_KEY_KP_ENTER) return false;
        onDone();
        return true;
    }

    public void onDone() {
        SFMPackets.LABEL_GUN_CHANNEL.sendToServer(new ServerboundLabelGunUpdatePacket(
                labelField.getValue(),
                HAND
        ));
        onClose();
    }

    @Override
    public void resize(Minecraft mc, int x, int y) {
        init(mc, x, y);
        var prev = this.labelField.getValue();
        super.resize(mc, x, y);
        this.labelField.setValue(prev);
    }

    @Override
    public void render(PoseStack poseStack, int mx, int my, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mx, my, partialTicks);
    }
}
