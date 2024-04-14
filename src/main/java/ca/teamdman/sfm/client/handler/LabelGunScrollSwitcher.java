package ca.teamdman.sfm.client.handler;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.item.LabelGunItem;
import ca.teamdman.sfm.common.net.ServerboundLabelGunUpdatePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class LabelGunScrollSwitcher {
    @SubscribeEvent
    public static void onScroll(InputEvent.MouseScrollingEvent event) {
        var player = Minecraft.getInstance().player;
        if (player == null) return;
        if (!player.isShiftKeyDown()) return;
        var gun = player.getMainHandItem();
        var hand = InteractionHand.MAIN_HAND;
        if (!(gun.getItem() instanceof LabelGunItem)) {
            gun = player.getOffhandItem();
            hand = InteractionHand.OFF_HAND;
        }
        if (!(gun.getItem() instanceof LabelGunItem)) return;

        var next = LabelGunItem.getNextLabel(gun, event.getScrollDeltaY() < 0 ? -1 : 1);
        PacketDistributor.SERVER.noArg().send(new ServerboundLabelGunUpdatePacket(
                next,
                hand
        ));

        event.setCanceled(true);
    }
}
