package ca.teamdman.sfm.datagen;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.registrar.SFMBlocks;
import ca.teamdman.sfm.common.registrar.SFMItems;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModels extends ItemModelProvider {

	public ItemModels(
		DataGenerator generator,
		ExistingFileHelper existingFileHelper
	) {
		super(generator, SFM.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		withExistingParent(
			SFMItems.MANAGER.getId().getPath(),
			SFM.MOD_ID + ":block/" + SFMBlocks.MANAGER.getId().getPath()
		);
		withExistingParent(
			SFMItems.CABLE.getId().getPath(),
			SFM.MOD_ID + ":block/" + SFMBlocks.CABLE.getId().getPath()
		);
		withExistingParent(
			SFMItems.CRAFTER.getId().getPath(),
			SFM.MOD_ID + ":block/" + SFMBlocks.CRAFTER.getId().getPath()
		);
	}
}
