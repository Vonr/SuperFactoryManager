package ca.teamdman.sfm.datagen;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class SFMBlockStates extends BlockStateProvider {
    public SFMBlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, SFM.MOD_ID, exFileHelper);
    }


    @Override
    protected void registerStatesAndModels() {
        simpleBlock(SFMBlocks.MANAGER_BLOCK.get(), models().cubeBottomTop(
                SFMBlocks.MANAGER_BLOCK.getId().getPath(),
                modLoc("block/manager_side"),
                modLoc("block/manager_bot"),
                modLoc("block/manager_top")
        ).texture("particle", "#top"));
    }
}
