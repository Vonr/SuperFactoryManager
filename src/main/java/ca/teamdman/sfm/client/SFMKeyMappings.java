package ca.teamdman.sfm.client;

import ca.teamdman.sfm.common.Constants;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;


public class SFMKeyMappings {
    public static final Lazy<KeyMapping> MORE_INFO_TOOLTIP_KEY = Lazy.of(() -> new KeyMapping(
            Constants.LocalizationKeys.KEY_MORE_INFO.key().get(),
            KeyConflictContext.GUI,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_SHIFT,
            Constants.LocalizationKeys.SFM_KEY_CATEGORY.key().get()
    ));
}
