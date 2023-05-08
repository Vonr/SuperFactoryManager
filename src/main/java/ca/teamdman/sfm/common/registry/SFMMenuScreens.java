package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.client.gui.screen.ManagerScreen;
import net.minecraft.client.gui.screens.MenuScreens;

public class SFMMenuScreens {
    public static void register() {
        MenuScreens.register(SFMMenus.MANAGER_MENU.get(), ManagerScreen::new);
    }
}
