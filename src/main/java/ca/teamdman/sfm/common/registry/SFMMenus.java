package ca.teamdman.sfm.common.registry;


import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.menu.ManagerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SFMMenus {
    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister
            .create(ForgeRegistries.CONTAINERS, SFM.MOD_ID);

    public static void register(IEventBus bus) {
        MENU_TYPES.register(bus);
    }

    public static final RegistryObject<MenuType<ManagerMenu>> MANAGER_MENU = MENU_TYPES
            .register(
                    "manager",
                    () -> IForgeMenuType.create(ManagerMenu::new)
            );


}
