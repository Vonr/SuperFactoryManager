package ca.teamdman.sfm.client.handler;

import ca.teamdman.sfm.SFM;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LabelGunWorldRenderer {
    //    @SubscribeEvent
    //    public static void highlightGhostBlock(RenderWorldLastEvent event) {
    //        var player = Minecraft.getInstance().player;
    //        if (player == null) return;
    //
    //        var labelGun = player.getItemInHand(InteractionHand.MAIN_HAND);
    //        if (!(labelGun.getItem() instanceof LabelGunItem)) labelGun = player.getItemInHand(InteractionHand.OFF_HAND);
    //        if (!(labelGun.getItem() instanceof LabelGunItem)) return;
    //    }
}
