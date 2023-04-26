package ca.teamdman.sfm.common.registry;


import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SFMMenus {
    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister
            .create(ForgeRegistries.MENU_TYPES, SFM.MOD_ID);

    public static void register(IEventBus bus) {
        MENU_TYPES.register(bus);
    }

    public static final RegistryObject<MenuType<ManagerContainerMenu>> MANAGER_MENU = MENU_TYPES
            .register(
                    "manager",
                    () -> IForgeMenuType.create(new IContainerFactory<>() {
                        @Override
                        public ManagerContainerMenu create(int windowId, Inventory inv, FriendlyByteBuf data) {
                            return new ManagerContainerMenu(windowId, inv, data);
                        }

                        @Override
                        public ManagerContainerMenu create(int windowId, Inventory inv) {
                            // we gotta do this to make sure spectator mode works
                            try {
                                assert inv.player instanceof LocalPlayer;
                                HitResult hr = Minecraft.getInstance().hitResult;
                                if (hr == null) return IContainerFactory.super.create(windowId, inv);
                                if (hr.getType() != HitResult.Type.BLOCK)
                                    return IContainerFactory.super.create(windowId, inv);
                                var pos = ((BlockHitResult) hr).getBlockPos();
                                BlockEntity be = inv.player.level.getBlockEntity(pos);
                                if (!(be instanceof ManagerBlockEntity mbe))
                                    return IContainerFactory.super.create(windowId, inv);
                                return new ManagerContainerMenu(windowId, inv, mbe);
                            } catch (Throwable t) {
                                return IContainerFactory.super.create(windowId, inv);
                            }
                        }
                    })
            );


}
