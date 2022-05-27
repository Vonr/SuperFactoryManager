package ca.teamdman.sfm.client;

import ca.teamdman.sfm.client.gui.screen.LabelGunScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class ClientStuff {
    public static void showLabelGunScreen(ItemStack stack, InteractionHand hand) {
        Minecraft
                .getInstance()
                .setScreen(new LabelGunScreen(stack, hand));
    }
}
