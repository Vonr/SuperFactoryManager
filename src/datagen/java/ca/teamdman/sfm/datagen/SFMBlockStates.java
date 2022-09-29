package ca.teamdman.sfm.datagen;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.block.WaterTankBlock;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
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

        simpleBlock(SFMBlocks.CABLE_BLOCK.get());


        ModelFile waterIntakeModelActive = models()
                .cubeAll(
                        SFMBlocks.WATER_TANK_BLOCK.getId().getPath() + "_active",
                        modLoc("block/water_intake_active")
                );
        ModelFile waterIntakeModelInactive = models()
                .cubeAll(
                        SFMBlocks.WATER_TANK_BLOCK.getId().getPath() + "_inactive",
                        modLoc("block/water_intake_inactive")
                );
        getVariantBuilder(SFMBlocks.WATER_TANK_BLOCK.get())
                .forAllStates(state -> ConfiguredModel
                        .builder()
                        .modelFile(
                                state.getValue(WaterTankBlock.IN_WATER)
                                ? waterIntakeModelActive
                                : waterIntakeModelInactive
                        )
                        .build());
    }
}
