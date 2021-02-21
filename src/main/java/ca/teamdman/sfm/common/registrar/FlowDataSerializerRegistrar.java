/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.registrar;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.flow.data.CursorFlowData;
import ca.teamdman.sfm.common.flow.data.CursorFlowData.CursorFlowDataSerializer;
import ca.teamdman.sfm.common.flow.data.FlowDataSerializer;
import ca.teamdman.sfm.common.flow.data.ItemStackComparerMatcherFlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackComparerMatcherFlowData.ItemStackComparerMatcherFlowDataSerializer;
import ca.teamdman.sfm.common.flow.data.ItemStackModIdMatcherFlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackModIdMatcherFlowData.ItemStackModIdMatcherFlowDataSerializer;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData.FlowTileEntityRuleDataSerializer;
import ca.teamdman.sfm.common.flow.data.LineNodeFlowData;
import ca.teamdman.sfm.common.flow.data.LineNodeFlowData.LineNodeFlowDataSerializer;
import ca.teamdman.sfm.common.flow.data.RelationshipFlowData;
import ca.teamdman.sfm.common.flow.data.RelationshipFlowData.FlowRelationshipDataSerializer;
import ca.teamdman.sfm.common.flow.data.TileInputFlowData;
import ca.teamdman.sfm.common.flow.data.TileInputFlowData.FlowInputDataSerializer;
import ca.teamdman.sfm.common.flow.data.TileOutputFlowData;
import ca.teamdman.sfm.common.flow.data.TileOutputFlowData.FlowOutputDataSerializer;
import ca.teamdman.sfm.common.flow.data.TimerTriggerFlowData;
import ca.teamdman.sfm.common.flow.data.TimerTriggerFlowData.FlowTimerTriggerDataSerializer;
import ca.teamdman.sfm.common.flow.data.ToolboxFlowData;
import ca.teamdman.sfm.common.flow.data.ToolboxFlowData.ToolboxFlowDataSerializer;
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
			new FlowInputDataSerializer(new ResourceLocation(SFM.MOD_ID, "input")),
			new FlowOutputDataSerializer(new ResourceLocation(SFM.MOD_ID, "output")),
			new FlowRelationshipDataSerializer(new ResourceLocation(SFM.MOD_ID, "relationship")),
			new LineNodeFlowDataSerializer(new ResourceLocation(SFM.MOD_ID, "line_node")),
			new FlowTimerTriggerDataSerializer(new ResourceLocation(SFM.MOD_ID, "timer_trigger")),
			new FlowTileEntityRuleDataSerializer(new ResourceLocation(SFM.MOD_ID, "tile_entity_rule")),
			new ItemStackComparerMatcherFlowDataSerializer(new ResourceLocation(SFM.MOD_ID, "item_stack_comparer_matcher")),
			new ItemStackModIdMatcherFlowDataSerializer(new ResourceLocation(SFM.MOD_ID, "item_stack_mod_id_matcher")),
			new CursorFlowDataSerializer(new ResourceLocation(SFM.MOD_ID, "cursor")),
			new ToolboxFlowDataSerializer(new ResourceLocation(SFM.MOD_ID, "toolbox"))
		);
	}

	@SubscribeEvent
	public static void onRegisterRegistry(final RegistryEvent.NewRegistry e) {
		MinecraftForge.EVENT_BUS.register(new FlowDataFactoryRegistry());
	}

	// Need the makeRegistry method to trick java into ignoring type errors!?!?
	private static <T extends IForgeRegistryEntry<T>> RegistryBuilder<T> makeRegistry(
		ResourceLocation name, Class<T> type) {
		return new RegistryBuilder<T>().setName(name).setType(type);
	}

	@ObjectHolder(SFM.MOD_ID)
	public static final class FlowDataSerializers {

		public static final FlowDataSerializer<TileInputFlowData> INPUT = WAITING;
		public static final FlowDataSerializer<TileOutputFlowData> OUTPUT = WAITING;
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
