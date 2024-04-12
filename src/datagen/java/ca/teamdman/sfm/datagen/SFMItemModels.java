package ca.teamdman.sfm.datagen;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import ca.teamdman.sfm.common.registry.SFMItems;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.function.Supplier;

public class SFMItemModels extends ItemModelProvider {
    public SFMItemModels(
            GatherDataEvent event
    ) {
        super(event.getGenerator().getPackOutput(), SFM.MOD_ID, event.getExistingFileHelper());
    }

    @Override
    protected void registerModels() {
        justParent(SFMItems.MANAGER_ITEM, SFMBlocks.MANAGER_BLOCK);
        justParent(SFMItems.CABLE_ITEM, SFMBlocks.CABLE_BLOCK);
        justParent(SFMItems.PRINTING_PRESS_ITEM, SFMBlocks.PRINTING_PRESS_BLOCK);
        justParent(SFMItems.WATER_TANK_ITEM, SFMBlocks.WATER_TANK_BLOCK, "_active");
        basicItem(SFMItems.DISK_ITEM);
        basicItem(SFMItems.LABEL_GUN_ITEM);
        basicItem(SFMItems.EXPERIENCE_GOOP_ITEM);
        basicItem(SFMItems.EXPERIENCE_SHARD_ITEM);
        basicItem(SFMItems.NETWORK_TOOL_ITEM);

        // force custom renderer
        getBuilder(BuiltInRegistries.ITEM.getKey(SFMItems.FORM_ITEM.get()).toString())
                .parent(new ModelFile.UncheckedModelFile("builtin/entity"))
                .guiLight(BlockModel.GuiLight.FRONT);
        getBuilder("form_base")
                .parent(new ModelFile.UncheckedModelFile("item/generated"))
                .texture("layer0", modLoc("item/form"));
    }

    private void justParent(
            Supplier<? extends Item> item, Supplier<? extends Block> block
    ) {
        justParent(item, block, "");
    }

    private void justParent(
            Supplier<? extends Item> item, Supplier<? extends Block> block, String extra
    ) {
        var blockPath = BuiltInRegistries.BLOCK.getKey(block.get()).getPath();
        var itemPath = BuiltInRegistries.ITEM.getKey(item.get()).getPath();
        withExistingParent(blockPath, SFM.MOD_ID + ":block/" + itemPath + extra);
    }

    private void basicItem(
            Supplier<? extends Item> item
    ) {
        var itemPath = BuiltInRegistries.ITEM.getKey(item.get()).getPath();
        withExistingParent(itemPath, mcLoc("item/generated")).texture(
                "layer0",
                modLoc("item/" + itemPath)
        );
    }
}
