package ca.teamdman.sfm.datagen;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import ca.teamdman.sfm.common.registry.SFMItems;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fmllegacy.RegistryObject;

public class SFMItemModels extends ItemModelProvider {
    public SFMItemModels(
            DataGenerator generator, ExistingFileHelper existingFileHelper
    ) {
        super(generator, SFM.MOD_ID, existingFileHelper);
    }


    @Override
    protected void registerModels() {
        justParent(SFMItems.MANAGER_ITEM, SFMBlocks.MANAGER_BLOCK);
        justParent(SFMItems.CABLE_ITEM, SFMBlocks.CABLE_BLOCK);
        basicItem(SFMItems.DISK_ITEM);
        basicItem(SFMItems.LABEL_GUN_ITEM);
    }

    private void justParent(
            RegistryObject<? extends Item> item,
            RegistryObject<? extends Block> block
    ) {
        justParent(item, block, "");
    }

    private void justParent(
            RegistryObject<? extends Item> item,
            RegistryObject<? extends Block> block,
            String extra
    ) {
        withExistingParent(
                block.getId().getPath(),
                SFM.MOD_ID + ":block/" + item.getId().getPath() + extra
        );
    }

    private void basicItem(
            RegistryObject<? extends Item> item
    ) {
        withExistingParent(
                item.getId().getPath(),
                mcLoc("item/generated")
        ).texture("layer0", modLoc("item/" + item.getId().getPath()));
    }
}
