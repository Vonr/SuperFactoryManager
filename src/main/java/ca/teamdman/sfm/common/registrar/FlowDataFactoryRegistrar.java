/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.registrar;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.flow.data.core.FlowDataFactory;
import ca.teamdman.sfm.common.flow.data.impl.FlowInputData;
import ca.teamdman.sfm.common.flow.data.impl.FlowInputData.FlowInputDataFactory;
import ca.teamdman.sfm.common.flow.data.impl.FlowLineNodeData;
import ca.teamdman.sfm.common.flow.data.impl.FlowLineNodeData.LineNodeFlowDataFactory;
import ca.teamdman.sfm.common.flow.data.impl.FlowOutputData;
import ca.teamdman.sfm.common.flow.data.impl.FlowOutputData.FlowOutputDataFactory;
import ca.teamdman.sfm.common.flow.data.impl.FlowRelationshipData;
import ca.teamdman.sfm.common.flow.data.impl.FlowRelationshipData.FlowRelationshipDataFactory;
import ca.teamdman.sfm.common.flow.data.impl.FlowTimerTriggerData;
import ca.teamdman.sfm.common.flow.data.impl.FlowTimerTriggerData.FlowTimerTriggerDataFactory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FlowDataFactoryRegistrar {

	private static final FlowDataFactory WAITING = null;

	@SubscribeEvent
	public static void onRegister(final RegistryEvent.Register<FlowDataFactory<?>> e) {
		e.getRegistry().registerAll(
			new FlowInputDataFactory(new ResourceLocation(SFM.MOD_ID, "input")),
			new FlowOutputDataFactory(new ResourceLocation(SFM.MOD_ID, "output")),
			new FlowRelationshipDataFactory(new ResourceLocation(SFM.MOD_ID, "relationship")),
			new LineNodeFlowDataFactory(new ResourceLocation(SFM.MOD_ID, "line_node")),
			new FlowTimerTriggerDataFactory(new ResourceLocation(SFM.MOD_ID, "timer_trigger"))
		);
		SFM.LOGGER.debug("Registered commands");
	}

	@SubscribeEvent
	public static void onRegisterRegistry(final RegistryEvent.NewRegistry e) {
		MinecraftForge.EVENT_BUS.register(new FlowDataFactoryRegistry());
	}

	private static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(
		ResourceLocation name, Class<T> type) {
		return new RegistryBuilder<T>().setName(name).setType(type);
	}

	@ObjectHolder(SFM.MOD_ID)
	public static final class FlowDataFactories {
		public static final FlowDataFactory<FlowInputData> INPUT = WAITING;
		public static final FlowDataFactory<FlowOutputData> OUTPUT = WAITING;
		public static final FlowDataFactory<FlowRelationshipData> RELATIONSHIP = WAITING;
		public static final FlowDataFactory<FlowLineNodeData> LINE_NODE = WAITING;
		public static final FlowDataFactory<FlowTimerTriggerData> TIMER_TRIGGER = WAITING;
	}

	public static class FlowDataFactoryRegistry {

		public FlowDataFactoryRegistry() {
			makeRegistry(new ResourceLocation(SFM.MOD_ID, "flow_data_factory_registry"),
				FlowDataFactory.class).create();
//			new RegistryBuilder<FlowDataFactory<?>>()
//				.setName(new ResourceLocation(SFM.MOD_ID, "flow_components"))
//				.setType(FlowDataFactory.class)
//				.add(
//					(IForgeRegistry.AddCallback<FlowDataFactory<?>>) (owner, stage, id, obj, oldObj) -> System.out
//						.println("new entry " + obj.getRegistryName()))
//				.set(DummyFlowDataFactory::new)
//				.set(MissingFlowDataFactory::new)
//				.allowModification()
//				.create();
		}
	}
}
