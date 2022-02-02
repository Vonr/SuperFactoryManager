package ca.teamdman.sfm.common;

import ca.teamdman.sfm.common.registry.SFMScreens;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

public class CommonProxy {
    public void showLabelGunScreen(ItemStack stack, InteractionHand hand) {

    }

    public void setupScreens() {
        SFMScreens.register();
    }
}
