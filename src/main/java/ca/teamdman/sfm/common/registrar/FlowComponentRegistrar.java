package ca.teamdman.sfm.common.registrar;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.flow.core.BlankFlowComponent;
import ca.teamdman.sfm.common.flow.core.FlowComponent;
import ca.teamdman.sfm.common.flow.core.MissingFlowComponent;
import ca.teamdman.sfm.common.flow.impl.ItemInputFlowComponent;
import ca.teamdman.sfm.common.flow.impl.ItemOutputFlowComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryBuilder;
import org.apache.logging.log4j.LogManager;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FlowComponentRegistrar {
	private static final FlowComponent WAITING = null;

	@SubscribeEvent
	public static void onRegister(final RegistryEvent.Register<FlowComponent> e) {
		e.getRegistry().registerAll(
				new ItemInputFlowComponent(new ResourceLocation(SFM.MOD_ID, "input")),
				new ItemOutputFlowComponent(new ResourceLocation(SFM.MOD_ID, "output"))
		);
		LogManager.getLogger(SFM.MOD_NAME + " Commands Registrar").debug("Registered commands");
	}

	@SubscribeEvent
	public static void onRegisterRegistry(final RegistryEvent.NewRegistry e) {
		MinecraftForge.EVENT_BUS.register(new FlowComponentRegistry());
	}

	@ObjectHolder(SFM.MOD_ID)
	public static final class FlowComponents {
		public static final FlowComponent INPUT = WAITING;
		public static final FlowComponent OUTPUT = WAITING;
	}

	public static class FlowComponentRegistry {
		public FlowComponentRegistry() {
			new RegistryBuilder<FlowComponent>()
					.setName(new ResourceLocation(SFM.MOD_ID, "flow_components"))
					.setType(FlowComponent.class)
					.add((IForgeRegistry.AddCallback<FlowComponent>) (owner, stage, id, obj, oldObj) -> {
						System.out.println("FLOWOWO NEW ITEM!");
					})
					.set(BlankFlowComponent::new)
					.set((key, isNetwork) -> new MissingFlowComponent(key))
					.allowModification()
					.create();
		}
	}
}
