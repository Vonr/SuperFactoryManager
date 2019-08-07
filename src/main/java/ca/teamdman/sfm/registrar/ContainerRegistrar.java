package ca.teamdman.sfm.registrar;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.container.ManagerContainer;
import ca.teamdman.sfm.gui.ManagerGui;
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
	private static final ContainerType WAITING = null;

	@SuppressWarnings("unchecked")
	@ObjectHolder(SFM.MOD_ID)
	public static class Containers {
		public static final ContainerType<ManagerContainer> MANAGER = WAITING;
	}

	@SubscribeEvent
	public static void onRegister(final RegistryEvent.Register<ContainerType<?>> e) {
		e.getRegistry().register(IForgeContainerType.create(ManagerContainer::new).setRegistryName(SFM.MOD_ID, "manager"));
	}

	@SubscribeEvent
	public static void setup(FMLClientSetupEvent e) {
		ScreenManager.registerFactory(Containers.MANAGER, ManagerGui::new);
	}
}
