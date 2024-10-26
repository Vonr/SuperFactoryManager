package ca.teamdman.sfm.client.gui;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.ClientStuff;
import ca.teamdman.sfm.client.registry.SFMKeyMappings;
import ca.teamdman.sfm.common.item.LabelGunItem;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import ca.teamdman.sfm.common.net.ServerboundLabelGunToggleLabelViewPacket;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.common.registry.SFMPackets;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.FastColor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class LabelGunReminderOverlay implements IGuiOverlay {
    private static AltState altState = AltState.Idle;
    public static void setExternalDebounce() {
        altState = AltState.PressCancelledExternally;
    }


    @Override
    public void render(
            ForgeGui gui,
            PoseStack poseStack,
            float partialTick,
            int screenWidth,
            int screenHeight
    ) {
        Minecraft minecraft = gui.getMinecraft();
        if (minecraft.options.hideGui) {
            return;
        }
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }
        if (!shouldRender(minecraft)) {
            return;
        }
        Font font = minecraft.font;
        var reminder = LocalizationKeys.LABEL_GUN_LABEL_VIEW_REMINDER.getComponent(
                SFMKeyMappings.TOGGLE_LABEL_VIEW_KEY
                        .get()
                        .getTranslatedKeyMessage().plainCopy().withStyle(ChatFormatting.YELLOW)
        );
        int reminderWidth = font.width(reminder);
        int x = screenWidth / 2 - reminderWidth / 2;
        int y = 20;
        font.drawShadow(
                poseStack,
                reminder,
                x,
                y,
                FastColor.ARGB32.color(255, 172, 208, 255)
        );
    }

    public static @Nullable InteractionHand getHandHoldingLabelGun(Player player) {
        if (player.getMainHandItem().getItem() == SFMItems.LABEL_GUN_ITEM.get()) {
            return InteractionHand.MAIN_HAND;
        } else if (player.getOffhandItem().getItem() == SFMItems.LABEL_GUN_ITEM.get()) {
            return InteractionHand.OFF_HAND;
        }
        return null;
    }

    private enum AltState {
        Idle,
        Pressed,
        PressCancelledExternally,
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        Minecraft minecraft = Minecraft.getInstance();

        // don't do anything if a screen is open
        if (minecraft.screen != null) return;

        // only do something if the key was pressed
        boolean alt_down = ClientStuff.isKeyDown(SFMKeyMappings.TOGGLE_LABEL_VIEW_KEY);
        switch(altState) {
            case Idle -> {
                if (alt_down) {
                    altState = AltState.Pressed;
                }
            }
            case Pressed -> {
                if (!alt_down) {
                    altState = AltState.Idle;
                    assert minecraft.player != null;
                    InteractionHand hand = getHandHoldingLabelGun(minecraft.player);
                    if (hand == null) return;
                    // send packet to server to toggle mode
                    SFMPackets.LABEL_GUN_ITEM_CHANNEL.sendToServer(new ServerboundLabelGunToggleLabelViewPacket(hand));
                }
            }
            case PressCancelledExternally -> {
                if (!alt_down) {
                    altState = AltState.Idle;
                }
            }
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean shouldRender(Minecraft minecraft) {
        LocalPlayer player = minecraft.player;
        if (player == null) return false;
        InteractionHand validHand = getHandHoldingLabelGun(player);
        if (validHand == null) return false;
        ItemStack stack = player.getItemInHand(validHand);
        return LabelGunItem.getOnlyShowActiveLabel(stack);
    }
}
