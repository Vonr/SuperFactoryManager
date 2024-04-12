package ca.teamdman.sfm.datagen;

import ca.teamdman.sfm.common.registry.SFMBlocks;
import com.google.common.collect.ImmutableList;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SFMLootTables extends LootTableProvider {

    public SFMLootTables(GatherDataEvent event) {
        super(
                event.getGenerator().getPackOutput(),
              // specify registry names of the tables that are required to generate, or can leave empty
              Collections.emptySet(),
              // Sub providers which generate the loot
              ImmutableList.of(new SubProviderEntry(SFMBlockLootProvider::new, LootContextParamSets.BLOCK)));
    }

    public static class SFMBlockLootProvider implements LootTableSubProvider {

        public void generate(BiConsumer<ResourceLocation, LootTable.Builder> writer) {
            dropSelf(SFMBlocks.MANAGER_BLOCK, writer);
            dropSelf(SFMBlocks.CABLE_BLOCK, writer);
            dropSelf(SFMBlocks.WATER_TANK_BLOCK, writer);
            dropSelf(SFMBlocks.PRINTING_PRESS_BLOCK, writer);

        }

        private void dropSelf(Supplier<Block> block, BiConsumer<ResourceLocation, LootTable.Builder> writer) {
            var pool = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(block.get()));
            writer.accept(block.get().getLootTable(), LootTable.lootTable().withPool(pool));
        }
    }
}
