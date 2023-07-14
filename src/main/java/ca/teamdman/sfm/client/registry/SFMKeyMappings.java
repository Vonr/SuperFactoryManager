package ca.teamdman.sfm.client.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.Constants;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = SFM.MOD_ID, value = Dist.CLIENT)

public class SFMKeyMappings {
    public static final Lazy<KeyMapping> MORE_INFO_TOOLTIP_KEY = Lazy.of(() -> new KeyMapping(
            Constants.LocalizationKeys.MORE_HOVER_INFO_KEY.key().get(),
            KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            Constants.LocalizationKeys.SFM_KEY_CATEGORY.key().get()
    ));

    public static final Lazy<KeyMapping> CONTAINER_INSPECTOR_KEY = Lazy.of(() -> new KeyMapping(
            Constants.LocalizationKeys.CONTAINER_INSPECTOR_TOGGLE_KEY.key().get(),
            KeyConflictContext.GUI,
            KeyModifier.CONTROL,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_I,
            Constants.LocalizationKeys.SFM_KEY_CATEGORY.key().get()
    ));

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(MORE_INFO_TOOLTIP_KEY.get());
        event.register(CONTAINER_INSPECTOR_KEY.get());
    }
}
