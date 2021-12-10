package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.menu.ManagerMenu;
import ca.teamdman.sfm.common.net.ServerboundManagerProgramPacket;
import ca.teamdman.sfm.common.registry.SFMPackets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fmlclient.gui.widget.ExtendedButton;

import java.util.Locale;

public class ManagerScreen extends AbstractContainerScreen<ManagerMenu> {
    private static final ResourceLocation BACKGROUND_TEXTURE_LOCATION = new ResourceLocation(
            SFM.MOD_ID,
            "textures/gui/container/manager.png"
    );
    protected            Button           clipboardButton;
    protected            Button           resetButton;

    public ManagerScreen(ManagerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        clipboardButton = this.addRenderableWidget(new ExtendedButton(
                (this.width - this.imageWidth) / 2 + 70,
                (this.height - this.imageHeight) / 2 + 47,
                100,
                16,
                new TranslatableComponent("sfm.manager.button.load_clipboard"),
                button -> this.onLoadClipboard()
        ));
        resetButton     = this.addRenderableWidget(new ExtendedButton(
                (this.width - this.imageWidth) / 2 + 70,
                (this.height - this.imageHeight) / 2 + 67,
                100,
                16,
                new TranslatableComponent("sfm.manager.button.reset"),
                button -> this.sendProgram("")
        ));
    }

    private void sendProgram(String program) {
        SFMPackets.LABEL_GUN_CHANNEL.sendToServer(new ServerboundManagerProgramPacket(
                menu.containerId,
                menu.BLOCK_ENTITY_POSITION,
                program
        ));
    }

    private void onLoadClipboard() {
        try {
            String contents = Minecraft.getInstance().keyboardHandler.getClipboard();
            sendProgram(contents);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public boolean keyPressed(int p_97765_, int p_97766_, int p_97767_) {
        if (super.keyPressed(p_97765_, p_97766_, p_97767_)) return true;
        if (Screen.isPaste(p_97765_)) {
            onLoadClipboard();
            return true;
        }
        return false;
    }

    @Override
    protected void renderLabels(PoseStack matrixStack, int mx, int my) {
        super.renderLabels(matrixStack, mx, my);
        var states = ManagerBlockEntity.State.values();
        var key    = menu.CONTAINER_DATA.get(ManagerBlockEntity.STATE_DATA_ACCESS_KEY);
        var state  = states[key];
        this.font.draw(
                matrixStack,
                new TranslatableComponent(
                        "sfm.manager.state",
                        new TranslatableComponent("sfm.manager.state." + state
                                .name()
                                .toLowerCase(
                                        Locale.ROOT)).withStyle(state.COLOR)
                ),
                titleLabelX,
                20,
                0
        );
    }

    @Override
    public void render(PoseStack poseStack, int mx, int my, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mx, my, partialTicks);
        this.renderTooltip(poseStack, mx, my);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mx, int my) {
        //        Lighting.setupForFlatItems();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE_LOCATION);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }
}
