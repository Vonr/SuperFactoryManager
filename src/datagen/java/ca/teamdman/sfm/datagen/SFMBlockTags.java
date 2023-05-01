package ca.teamdman.sfm.datagen;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class SFMBlockTags extends BlockTagsProvider {

    public SFMBlockTags(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, SFM.MOD_ID, existingFileHelper);
    }

    @Override
    public String getName() {
        return "SuperFactoryManager Tags";
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(SFMBlocks.CABLE_BLOCK.get())
                .add(SFMBlocks.MANAGER_BLOCK.get())
                .add(SFMBlocks.PRINTING_PRESS_BLOCK.get())
                .add(SFMBlocks.CABLE_BLOCK.get());
        tag(BlockTags.MINEABLE_WITH_AXE)
                .add(SFMBlocks.PRINTING_PRESS_BLOCK.get());
    }
}
