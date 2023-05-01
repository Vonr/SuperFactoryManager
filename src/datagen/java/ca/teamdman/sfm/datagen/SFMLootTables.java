package ca.teamdman.sfm.datagen;

import ca.teamdman.sfm.common.registry.SFMBlocks;
import com.google.common.collect.ImmutableList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;

import java.util.Collections;
import java.util.function.BiConsumer;

public class SFMLootTables extends LootTableProvider {

    public SFMLootTables(
            PackOutput pOutput
    ) {
        super(
                pOutput,
                // specify registry names of the tables that are required to generate, or can leave empty
                Collections.emptySet(),
                // Sub providers which generate the loot
                ImmutableList.of(new SubProviderEntry(SFMBlockLootProvider::new, LootContextParamSets.BLOCK))
        );
    }

    public static class SFMBlockLootProvider implements LootTableSubProvider {

        @Override
        protected void addTables() {
            dropSelf(SFMBlocks.MANAGER_BLOCK.get());
            dropSelf(SFMBlocks.CABLE_BLOCK.get());
            dropSelf(SFMBlocks.PRINTING_PRESS_BLOCK.get());
            dropSelf(SFMBlocks.WATER_TANK_BLOCK.get());
        }
        public void generate(BiConsumer<ResourceLocation, LootTable.Builder> writer) {
            dropSelf(SFMBlocks.MANAGER_BLOCK, writer);
            dropSelf(SFMBlocks.CABLE_BLOCK, writer);
            dropSelf(SFMBlocks.WATER_TANK_BLOCK, writer);
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return Arrays.asList(
                    SFMBlocks.MANAGER_BLOCK.get(),
                    SFMBlocks.CABLE_BLOCK.get(),
                    SFMBlocks.WATER_TANK_BLOCK.get(),
                    SFMBlocks.PRINTING_PRESS_BLOCK.get()
            );
        }
        private void dropSelf(RegistryObject<Block> block, BiConsumer<ResourceLocation, LootTable.Builder> writer) {
            var pool = LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1))
                    .add(LootItem.lootTableItem(block.get()));
            writer.accept(block.get().getLootTable(), LootTable.lootTable().withPool(pool));
        }
    }
}
