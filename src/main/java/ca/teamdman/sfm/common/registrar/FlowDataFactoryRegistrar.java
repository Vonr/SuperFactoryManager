package ca.teamdman.sfm.common.registrar;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.flowdata.FlowDataFactory;
import ca.teamdman.sfm.common.flowdata.FlowDataFactory.DummyFlowDataFactory;
import ca.teamdman.sfm.common.flowdata.FlowDataFactory.MissingFlowDataFactory;
import ca.teamdman.sfm.common.flowdata.InputData.InputDataFactory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FlowDataFactoryRegistrar {

	private static final InputDataFactory WAITING = null;

	@SubscribeEvent
	public static void onRegister(final RegistryEvent.Register<FlowDataFactory<?>> e) {
		e.getRegistry().registerAll(
			new InputDataFactory(new ResourceLocation(SFM.MOD_ID, "input"))
		);
		SFM.LOGGER.debug("Registered commands");
	}

	@SubscribeEvent
	public static void onRegisterRegistry(final RegistryEvent.NewRegistry e) {
		MinecraftForge.EVENT_BUS.register(new FlowDataFactoryRegistry());
	}

	@ObjectHolder(SFM.MOD_ID)
	public static final class FlowDataFactories {
		public static final FlowDataFactory<?> INPUT = WAITING;
	}

	public static class FlowDataFactoryRegistry {

		public FlowDataFactoryRegistry() {
			new RegistryBuilder<FlowDataFactory>()
				.setName(new ResourceLocation(SFM.MOD_ID, "flow_components"))
				.setType(FlowDataFactory.class)
				.add(
					(IForgeRegistry.AddCallback<FlowDataFactory>) (owner, stage, id, obj, oldObj) -> System.out
						.println("new entry " + obj.getRegistryName()))
				.set(DummyFlowDataFactory::new)
				.set(MissingFlowDataFactory::new)
				.allowModification()
				.create();
		}
	}
}
