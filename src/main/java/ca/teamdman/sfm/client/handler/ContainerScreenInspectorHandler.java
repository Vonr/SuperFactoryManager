package ca.teamdman.sfm.client.handler;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.ClientStuff;
import ca.teamdman.sfm.client.registry.SFMKeyMappings;
import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.net.ServerboundContainerExportsInspectionRequestPacket;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import net.neoforged.neoforge.network.PacketDistributor;

import javax.annotation.Nullable;

@EventBusSubscriber(modid = SFM.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ContainerScreenInspectorHandler {
    private static boolean visible = false;
    @Nullable
    private static AbstractContainerScreen<?> lastScreen = null;
    private static final ExtendedButton exportInspectorButton = new ExtendedButton(
            5,
            50,
            100,
            20,
            Constants.LocalizationKeys.CONTAINER_INSPECTOR_SHOW_EXPORTS_BUTTON.getComponent(),
            (button) -> {
                BlockEntity lookBlockEntity = ClientStuff.getLookBlockEntity();
                if (lastScreen != null && lookBlockEntity != null) {
                    PacketDistributor.sendToServer(new ServerboundContainerExportsInspectionRequestPacket(
                            lastScreen.getMenu().containerId,
                            lookBlockEntity.getBlockPos()
                    ));
                }
            }
    );

    @SubscribeEvent
    public static void onMouseClick(ScreenEvent.KeyPressed.MouseButtonPressed.Pre event) {
        boolean shouldCapture = Minecraft.getInstance().screen instanceof AbstractContainerScreen<?>;
        if (shouldCapture && visible && exportInspectorButton.clicked(event.getMouseX(), event.getMouseY())) {
            exportInspectorButton.playDownSound(Minecraft.getInstance().getSoundManager());
            exportInspectorButton.onClick(event.getMouseX(), event.getMouseY());
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onGuiRender(ScreenEvent.Render.Post event) {
        if (!visible) return;
        if (event.getScreen() instanceof AbstractContainerScreen<?> acs) {
            lastScreen = acs;
            AbstractContainerMenu menu = acs.getMenu();
            int containerSlotCount = 0;
            int inventorySlotCount = 0;
            GuiGraphics graphics = event.getGuiGraphics();
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();
            poseStack.translate(0, 0, 350); // render text over the items but under the tooltips

            // draw the button
            exportInspectorButton.render(graphics, event.getMouseX(), event.getMouseY(), event.getPartialTick());


            // draw index on each slot
            for (var slot : menu.slots) {
                int colour;
                if (slot.container instanceof Inventory) {
                    //noinspection DataFlowIssue
                    colour = ChatFormatting.YELLOW.getColor();
                    inventorySlotCount++;
                } else {
                    colour = 0xFFF;
                    containerSlotCount++;
                }
                graphics.drawString(
                        Minecraft.getInstance().font,
                        Component.literal(Integer.toString(slot.getSlotIndex())),
                        acs.getGuiLeft() + slot.x,
                        acs.getGuiTop() + slot.y,
                        colour,
                        false
                );
            }

            // draw text for slot totals
            graphics.drawString(
                    Minecraft.getInstance().font,
                    Constants.LocalizationKeys.CONTAINER_INSPECTOR_CONTAINER_SLOT_COUNT.getComponent(Component
                                                                                                             .literal(
                                                                                                                     String.valueOf(
                                                                                                                             containerSlotCount))
                                                                                                             .withStyle(
                                                                                                                     ChatFormatting.BLUE)),
                    5,
                    5,
                    0xFFFFFF,
                    true
            );
            graphics.drawString(
                    Minecraft.getInstance().font,
                    Constants.LocalizationKeys.CONTAINER_INSPECTOR_INVENTORY_SLOT_COUNT.getComponent(Component
                                                                                                             .literal(
                                                                                                                     String.valueOf(
                                                                                                                             inventorySlotCount))
                                                                                                             .withStyle(
                                                                                                                     ChatFormatting.YELLOW)),
                    5,
                    25,
                    0xFFFFFF,
                    true
            );
            poseStack.popPose();
        }
    }

    @SubscribeEvent
    public static void onKeyDown(ScreenEvent.KeyPressed.Pre event) {
        // Handle Ctrl+I hotkey to toggle overlay
        var toggleKey = SFMKeyMappings.CONTAINER_INSPECTOR_KEY.get();
        var toggleKeyPressed = toggleKey.isActiveAndMatches(InputConstants.Type.KEYSYM.getOrCreate(event.getKeyCode()));
        if (toggleKeyPressed) {
            visible = !visible;
            event.setCanceled(true);
            return;
        }

        // Handle ~ hotkey to inspect hovered item
        var activateKey = SFMKeyMappings.ITEM_INSPECTOR_KEY.get();
        var activateKeyPressed = activateKey.isActiveAndMatches(InputConstants.Type.KEYSYM.getOrCreate(event.getKeyCode()));
        if (activateKeyPressed) {
            // This doesn't work when activated hovering a JEI item.
            if (event.getScreen() instanceof AbstractContainerScreen<?> acs) {
                Slot hoveredSlot = acs.hoveredSlot;
                if (hoveredSlot != null) {
                    ItemStack hoveredStack = hoveredSlot.getItem();
                    if (!hoveredStack.isEmpty()) {
                        ClientStuff.showItemInspectorScreen(hoveredStack);
                    }
                }
            }
        }
    }
}
