package ca.teamdman.sfm.datagen;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.block.WaterIntakeBlock;
import ca.teamdman.sfm.common.registrar.SFMBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockStates extends BlockStateProvider {

	public BlockStates(
		DataGenerator gen, ExistingFileHelper exFileHelper
	) {
		super(gen, SFM.MOD_ID, exFileHelper);
	}

	@Override
	protected void registerStatesAndModels() {
		simpleBlock(
			SFMBlocks.MANAGER.get(),
			models().cubeBottomTop(
				SFMBlocks.MANAGER.getId().getPath(),
				modLoc("block/manager_side"),
				modLoc("block/manager_bot"),
				modLoc("block/manager_top")
			).texture("particle", "#top")
		);
		simpleBlock(SFMBlocks.CABLE.get());
		simpleBlock(
			SFMBlocks.CRAFTER.get(),
			models().cube(
				SFMBlocks.CRAFTER.getId().getPath(),
				modLoc("block/crafter_top"),
				modLoc("block/crafter_top"),
				modLoc("block/crafter_side"),
				modLoc("block/crafter_side"),
				modLoc("block/crafter_front"),
				modLoc("block/crafter_front")
			).texture("particle", "#up")
		);
		simpleBlock(
			SFMBlocks.WORKSTATION.get(),
			models().cube(
				SFMBlocks.WORKSTATION.getId().getPath(),
				modLoc("block/workstation_top"),
				modLoc("block/workstation_top"),
				modLoc("block/workstation_side"),
				modLoc("block/workstation_side"),
				modLoc("block/workstation_front"),
				modLoc("block/workstation_front")
			).texture("particle", "#up")
		);

		ModelFile waterIntakeModelActive = models()
			.cubeAll(
				SFMBlocks.WATER_INTAKE.getId().getPath()+"_active",
				modLoc("block/water_intake_active")
			);
		ModelFile waterIntakeModelInactive = models()
			.cubeAll(
				SFMBlocks.WATER_INTAKE.getId().getPath()+"_inactive",
				modLoc("block/water_intake_inactive")
			);
		getVariantBuilder(SFMBlocks.WATER_INTAKE.get())
			.forAllStates(state -> ConfiguredModel
				.builder()
				.modelFile(
					state.get(WaterIntakeBlock.IN_WATER)
						? waterIntakeModelActive
						: waterIntakeModelInactive
				)
				.build());
	}
}
