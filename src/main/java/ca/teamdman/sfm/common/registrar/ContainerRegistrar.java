package ca.teamdman.sfm.common.registrar;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.container.CrafterContainer;
import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.client.gui.CrafterScreen;
import ca.teamdman.sfm.client.gui.ManagerScreen;
import ca.teamdman.sfm.common.container.factory.CrafterContainerProvider;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ObjectHolder;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ContainerRegistrar {
	@SuppressWarnings("rawtypes")
	private static final ContainerType WAITING = null;

	@SubscribeEvent
	public static void onRegister(final RegistryEvent.Register<ContainerType<?>> e) {
		e.getRegistry().register(IForgeContainerType.create(ManagerContainer::new).setRegistryName(SFM.MOD_ID, "manager"));
		e.getRegistry().register(IForgeContainerType.create(new CrafterContainerProvider.CrafterContainerFactory()).setRegistryName(SFM.MOD_ID, "crafter"));
	}

	@SubscribeEvent
	public static void setup(FMLClientSetupEvent e) {
		ScreenManager.registerFactory(Containers.MANAGER, ManagerScreen::new);
		ScreenManager.registerFactory(Containers.CRAFTER, CrafterScreen::new);
	}

	@SuppressWarnings("unchecked")
	@ObjectHolder(SFM.MOD_ID)
	public static class Containers {
		public static final ContainerType<ManagerContainer> MANAGER = WAITING;
		public static final ContainerType<CrafterContainer> CRAFTER = WAITING;
	}
}
