package ca.teamdman.sfm.datagen;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.block.WaterTankBlock;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class SFMBlockStates extends BlockStateProvider {
    public SFMBlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, SFM.MOD_ID, exFileHelper);
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

        {
            ModelFile barrelModel = models().getExistingFile(mcLoc("block/barrel"));
            ModelFile barrelOpenModel = models().getExistingFile(mcLoc("block/barrel_open"));

            getVariantBuilder(SFMBlocks.TEST_BARREL_BLOCK.get())
                    .forAllStates(state -> {
                        Direction facing = state.getValue(BlockStateProperties.FACING);
                        boolean open = state.getValue(BlockStateProperties.OPEN);
                        int x = 0;
                        int y = 0;

                        switch (facing) {
                            case DOWN:
                                x = 180;
                                break;
                            case UP:
                                x = 0;
                                break;
                            case NORTH:
                                x = 90;
                                y = 0;
                                break;
                            case SOUTH:
                                x = 90;
                                y = 180;
                                break;
                            case WEST:
                                x = 90;
                                y = 270;
                                break;
                            case EAST:
                                x = 90;
                                y = 90;
                                break;
                        }

                        return ConfiguredModel.builder()
                                .modelFile(open ? barrelOpenModel : barrelModel)
                                .rotationX(x)
                                .rotationY(y)
                                .build();
                    });
        }
    }
}
