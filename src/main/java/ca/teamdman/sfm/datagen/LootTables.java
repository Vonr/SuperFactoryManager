package ca.teamdman.sfm.datagen;

import ca.teamdman.sfm.common.registrar.SFMBlocks;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTable.Builder;
import net.minecraft.loot.LootTableManager;
import net.minecraft.loot.ValidationTracker;
import net.minecraft.util.ResourceLocation;

public class LootTables extends LootTableProvider {

	public LootTables(DataGenerator dataGeneratorIn) {
		super(dataGeneratorIn);
	}

	@Override
	protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, Builder>>>, LootParameterSet>> getTables() {
		return Lists.newArrayList(Pair.of(
			BlockLoot::new,
			LootParameterSets.BLOCK
		));
	}

	@Override
	protected void validate(
		Map<ResourceLocation, LootTable> map,
		ValidationTracker validationtracker
	) {
		map.forEach((k, v) -> LootTableManager.validateLootTable(
			validationtracker,
			k,
			v
		));
	}

	private static class BlockLoot extends BlockLootTables {

		@Override
		protected void addTables() {
			registerDropSelfLootTable(SFMBlocks.MANAGER.get());
			registerDropSelfLootTable(SFMBlocks.CABLE.get());
			registerDropSelfLootTable(SFMBlocks.CRAFTER.get());
		}

		@Override
		protected Iterable<Block> getKnownBlocks() {
			return Arrays.asList(
				SFMBlocks.MANAGER.get(),
				SFMBlocks.CABLE.get(),
				SFMBlocks.CRAFTER.get()
			);
		}
	}
}
