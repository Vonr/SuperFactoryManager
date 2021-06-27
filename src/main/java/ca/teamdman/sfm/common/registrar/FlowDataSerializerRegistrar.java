/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.registrar;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.flow.data.ConditionLineNodeFlowData;
import ca.teamdman.sfm.common.flow.data.CursorFlowData;
import ca.teamdman.sfm.common.flow.data.FlowDataSerializer;
import ca.teamdman.sfm.common.flow.data.ItemConditionFlowData;
import ca.teamdman.sfm.common.flow.data.ItemConditionRuleFlowData;
import ca.teamdman.sfm.common.flow.data.ItemInputFlowData;
import ca.teamdman.sfm.common.flow.data.ItemModMatcherFlowData;
import ca.teamdman.sfm.common.flow.data.ItemMovementRuleFlowData;
import ca.teamdman.sfm.common.flow.data.ItemOutputFlowData;
import ca.teamdman.sfm.common.flow.data.ItemPickerMatcherFlowData;
import ca.teamdman.sfm.common.flow.data.LineNodeFlowData;
import ca.teamdman.sfm.common.flow.data.RelationshipFlowData;
import ca.teamdman.sfm.common.flow.data.TileModMatcherFlowData;
import ca.teamdman.sfm.common.flow.data.TilePositionMatcherFlowData;
import ca.teamdman.sfm.common.flow.data.TileTypeMatcherFlowData;
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
			new ItemMovementRuleFlowData
				.Serializer(new ResourceLocation(SFM.MOD_ID, "item_movement_rule")),
			new ItemPickerMatcherFlowData
				.Serializer(new ResourceLocation(SFM.MOD_ID, "item_picker_matcher")),
			new ItemModMatcherFlowData
				.Serializer(new ResourceLocation(SFM.MOD_ID, "item_mod_matcher")),
			new CursorFlowData
				.Serializer(new ResourceLocation(SFM.MOD_ID, "cursor")),
			new Serializer(new ResourceLocation(SFM.MOD_ID, "toolbox")),
			new ItemInputFlowData
				.Serializer(new ResourceLocation(SFM.MOD_ID, "basic_input")),
			new ItemOutputFlowData.Serializer(new ResourceLocation(SFM.MOD_ID, "basic_output")),
			new TilePositionMatcherFlowData.Serializer(new ResourceLocation(SFM.MOD_ID, "tile_position_matcher")),
			new TileModMatcherFlowData.Serializer(new ResourceLocation(SFM.MOD_ID, "tile_mod_matcher")),
			new ItemConditionRuleFlowData.Serializer(new ResourceLocation(SFM.MOD_ID, "item_condition_rule")),
			new ItemConditionFlowData.Serializer(new ResourceLocation(SFM.MOD_ID, "item_condition")),
			new ConditionLineNodeFlowData.Serializer(new ResourceLocation(SFM.MOD_ID, "condition_line_node")),
			new TileTypeMatcherFlowData.Serializer(new ResourceLocation(SFM.MOD_ID, "tile_type_matcher"))
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
		public static final FlowDataSerializer<ItemInputFlowData> BASIC_INPUT = WAITING;
		public static final FlowDataSerializer<ItemOutputFlowData> BASIC_OUTPUT = WAITING;
		public static final FlowDataSerializer<RelationshipFlowData> RELATIONSHIP = WAITING;
		public static final FlowDataSerializer<LineNodeFlowData> LINE_NODE = WAITING;
		public static final FlowDataSerializer<ConditionLineNodeFlowData> CONDITION_LINE_NODE = WAITING;
		public static final FlowDataSerializer<TimerTriggerFlowData> TIMER_TRIGGER = WAITING;
		public static final FlowDataSerializer<ItemMovementRuleFlowData> ITEM_MOVEMENT_RULE = WAITING;
		public static final FlowDataSerializer<ItemPickerMatcherFlowData> ITEM_PICKER_MATCHER = WAITING;
		public static final FlowDataSerializer<ItemModMatcherFlowData> ITEM_MOD_MATCHER = WAITING;
		public static final FlowDataSerializer<TileModMatcherFlowData> TILE_MOD_MATCHER = WAITING;
		public static final FlowDataSerializer<CursorFlowData> CURSOR = WAITING;
		public static final FlowDataSerializer<ToolboxFlowData> TOOLBOX = WAITING;
		public static final FlowDataSerializer<TilePositionMatcherFlowData> TILE_POSITION_MATCHER = WAITING;
		public static final FlowDataSerializer<TileTypeMatcherFlowData> TILE_TYPE_MATCHER = WAITING;
		public static final FlowDataSerializer<ItemConditionRuleFlowData> ITEM_CONDITION_RULE = WAITING;
		public static final FlowDataSerializer<ItemConditionFlowData> ITEM_CONDITION = WAITING;
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
