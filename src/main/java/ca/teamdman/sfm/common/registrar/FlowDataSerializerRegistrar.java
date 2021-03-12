/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.registrar;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.flow.data.BasicTileInputFlowData;
import ca.teamdman.sfm.common.flow.data.BasicTileOutputFlowData;
import ca.teamdman.sfm.common.flow.data.CursorFlowData;
import ca.teamdman.sfm.common.flow.data.FlowDataSerializer;
import ca.teamdman.sfm.common.flow.data.ItemStackComparerMatcherFlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackModIdMatcherFlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData;
import ca.teamdman.sfm.common.flow.data.LineNodeFlowData;
import ca.teamdman.sfm.common.flow.data.RelationshipFlowData;
import ca.teamdman.sfm.common.flow.data.TimerTriggerFlowData;
import ca.teamdman.sfm.common.flow.data.ToolboxFlowData;
import ca.teamdman.sfm.common.flow.data.ToolboxFlowData.Serializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FlowDataSerializerRegistrar {

	private static final FlowDataSerializer WAITING = null;

	@SubscribeEvent
	public static void onRegister(final RegistryEvent.Register<FlowDataSerializer<?>> e) {
		e.getRegistry().registerAll(
			new RelationshipFlowData
				.Serializer(new ResourceLocation(SFM.MOD_ID, "relationship")),
			new LineNodeFlowData
				.Serializer(new ResourceLocation(SFM.MOD_ID, "line_node")),
			new TimerTriggerFlowData
				.Serializer(new ResourceLocation(SFM.MOD_ID, "timer_trigger")),
			new ItemStackTileEntityRuleFlowData
				.Serializer(new ResourceLocation(SFM.MOD_ID, "tile_entity_rule")),
			new ItemStackComparerMatcherFlowData
				.Serializer(new ResourceLocation(SFM.MOD_ID, "item_stack_comparer_matcher")),
			new ItemStackModIdMatcherFlowData
				.Serializer(new ResourceLocation(SFM.MOD_ID, "item_stack_mod_id_matcher")),
			new CursorFlowData
				.Serializer(new ResourceLocation(SFM.MOD_ID, "cursor")),
			new Serializer(new ResourceLocation(SFM.MOD_ID, "toolbox")),
			new BasicTileInputFlowData
				.Serializer(new ResourceLocation(SFM.MOD_ID, "basic_input")),
			new BasicTileOutputFlowData.Serializer(new ResourceLocation(SFM.MOD_ID, "basic_output"))
		);
	}

	@SubscribeEvent
	public static void onRegisterRegistry(final RegistryEvent.NewRegistry e) {
		MinecraftForge.EVENT_BUS.register(new FlowDataFactoryRegistry());
	}

	// Need the makeRegistry method to trick java into ignoring type errors!?!?
	private static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(
		ResourceLocation name, Class<T> type
	) {
		return new RegistryBuilder<T>().setName(name).setType(type);
	}

	@ObjectHolder(SFM.MOD_ID)
	public static final class FlowDataSerializers {
		public static final FlowDataSerializer<BasicTileInputFlowData> BASIC_INPUT = WAITING;
		public static final FlowDataSerializer<BasicTileOutputFlowData> BASIC_OUTPUT = WAITING;
		public static final FlowDataSerializer<RelationshipFlowData> RELATIONSHIP = WAITING;
		public static final FlowDataSerializer<LineNodeFlowData> LINE_NODE = WAITING;
		public static final FlowDataSerializer<TimerTriggerFlowData> TIMER_TRIGGER = WAITING;
		public static final FlowDataSerializer<ItemStackTileEntityRuleFlowData> TILE_ENTITY_RULE = WAITING;
		public static final FlowDataSerializer<ItemStackComparerMatcherFlowData> ITEM_STACK_COMPARER_MATCHER = WAITING;
		public static final FlowDataSerializer<ItemStackModIdMatcherFlowData> ITEM_STACK_MOD_ID_MATCHER = WAITING;
		public static final FlowDataSerializer<CursorFlowData> CURSOR = WAITING;
		public static final FlowDataSerializer<ToolboxFlowData> TOOLBOX = WAITING;
	}

	public static class FlowDataFactoryRegistry {

		public FlowDataFactoryRegistry() {
			makeRegistry(
				new ResourceLocation(SFM.MOD_ID, "flow_data_serializer_registry"),
				FlowDataSerializer.class
			).create();
		}
	}
}
