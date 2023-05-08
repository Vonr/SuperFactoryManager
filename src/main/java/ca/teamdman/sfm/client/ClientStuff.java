package ca.teamdman.sfm.client;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.gui.screen.LabelGunScreen;
import ca.teamdman.sfm.client.gui.screen.ProgramEditScreen;
import ca.teamdman.sfm.client.render.PrintingPressBlockEntityRenderer;
import ca.teamdman.sfm.common.registry.SFMBlockEntities;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientStuff {
    public static void showLabelGunScreen(ItemStack stack, InteractionHand hand) {
        Minecraft
                .getInstance()
                .setScreen(new LabelGunScreen(stack, hand));
    }

    public static void showTextEditorScreen(ItemStack diskItem, Consumer<String> programSetter) {
        Minecraft
                .getInstance()
                .pushGuiLayer(new ProgramEditScreen(diskItem, programSetter));
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(
                SFMBlockEntities.PRINTING_PRESS_BLOCK_ENTITY.get(),
                PrintingPressBlockEntityRenderer::new
        );
    }

    public static boolean isMoreInfoKeyDown() {
        // special effort is needed to ensure this works properly when the manager screen is open
        // https://github.com/mekanism/Mekanism/blob/f92b48a49e0766cd3aa78e95c9c4a47ba90402f5/src/main/java/mekanism/client/key/MekKeyHandler.java
        long handle = Minecraft.getInstance().getWindow().getWindow();
        return InputConstants.isKeyDown(
                handle,
                SFMKeyMappings.MORE_INFO_TOOLTIP_KEY
                        .get()
                        .getKey()
                        .getValue()
        );
    }
}
