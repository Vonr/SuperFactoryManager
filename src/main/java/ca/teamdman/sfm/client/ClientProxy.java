package ca.teamdman.sfm.client;

import ca.teamdman.sfm.client.gui.screen.LabelGunScreen;
import ca.teamdman.sfm.common.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class ClientProxy extends CommonProxy {
    @Override
    public void showLabelGunScreen(ItemStack stack, InteractionHand hand) {
        Minecraft
                .getInstance()
                .setScreen(new LabelGunScreen(stack, hand));
    }
}
