package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.menu.ManagerMenu;
import ca.teamdman.sfm.common.net.ServerboundManagerProgramPacket;
import ca.teamdman.sfm.common.net.ServerboundManagerResetPacket;
import ca.teamdman.sfm.common.registry.SFMPackets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.gui.widget.ExtendedButton;

import java.util.Locale;

public class ManagerScreen extends AbstractContainerScreen<ManagerMenu> {
    private static final ResourceLocation BACKGROUND_TEXTURE_LOCATION = new ResourceLocation(
            SFM.MOD_ID,
            "textures/gui/container/manager.png"
    );
    private final        float            STATUS_DURATION             = 40;
    private              BaseComponent    status                      = new TextComponent("");
    private              float            statusCountdown             = 0;

    public ManagerScreen(ManagerMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(new ExtendedButton(
                (this.width - this.imageWidth) / 2 + 70,
                (this.height - this.imageHeight) / 2 + 37,
                100,
                16,
                new TranslatableComponent("gui.sfm.manager.button.import_clipboard"),
                button -> this.onLoadClipboard()
        ));
        this.addRenderableWidget(new ExtendedButton(
                (this.width - this.imageWidth) / 2 + 70,
                (this.height - this.imageHeight) / 2 + 37 + 20,
                100,
                16,
                new TranslatableComponent("gui.sfm.manager.button.export_clipboard"),
                button -> this.onSaveClipboard()
        ));
        this.addRenderableWidget(new ExtendedButton(
                (this.width - this.imageWidth) / 2 + 120,
                (this.height - this.imageHeight) / 2 + 10,
                50,
                12,
                new TranslatableComponent("gui.sfm.manager.button.reset"),
                button -> sendReset()
        ));
    }

    private void sendReset() {
        SFMPackets.MANAGER_CHANNEL.sendToServer(new ServerboundManagerResetPacket(
                menu.containerId,
                menu.BLOCK_ENTITY_POSITION
        ));
        status          = new TranslatableComponent("gui.sfm.manager.status.reset");
        statusCountdown = STATUS_DURATION;
    }

    private void sendProgram(String program) {
        SFMPackets.MANAGER_CHANNEL.sendToServer(new ServerboundManagerProgramPacket(
                menu.containerId,
                menu.BLOCK_ENTITY_POSITION,
                program
        ));
        menu.program    = program;
        status          = new TranslatableComponent("gui.sfm.manager.status.loaded_clipboard");
        statusCountdown = STATUS_DURATION;
    }

    private void onSaveClipboard() {
        try {
            Minecraft.getInstance().keyboardHandler.setClipboard(menu.program);
            status          = new TranslatableComponent("gui.sfm.manager.status.saved_clipboard");
            statusCountdown = STATUS_DURATION;
        } catch (Throwable t) {
            t.printStackTrace();
        }
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
        } else if (Screen.isCopy(p_97765_)) {
            onSaveClipboard();
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
                        "gui.sfm.manager.state",
                        new TranslatableComponent("gui.sfm.manager.state." + state
                                .name()
                                .toLowerCase(
                                        Locale.ROOT)).withStyle(state.COLOR)
                ),
                titleLabelX,
                20,
                0
        );
        if (statusCountdown <= 0) return;
        this.font.draw(
                matrixStack,
                status,
                titleLabelX,
                20f + font.lineHeight + 0.1f,
                0
        );
    }

    @Override
    public void render(PoseStack poseStack, int mx, int my, float partialTicks) {
        this.renderBackground(poseStack);
        super.render(poseStack, mx, my, partialTicks);
        this.renderTooltip(poseStack, mx, my);
        statusCountdown -= partialTicks;
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
