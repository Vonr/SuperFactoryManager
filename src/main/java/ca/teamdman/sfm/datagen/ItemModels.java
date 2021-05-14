package ca.teamdman.sfm.datagen;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.registrar.SFMBlocks;
import ca.teamdman.sfm.common.registrar.SFMItems;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;

public class ItemModels extends ItemModelProvider {

	public ItemModels(
		DataGenerator generator,
		ExistingFileHelper existingFileHelper
	) {
		super(generator, SFM.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		justParent(SFMItems.MANAGER, SFMBlocks.MANAGER);
		justParent(SFMItems.CABLE, SFMBlocks.CABLE);
		justParent(SFMItems.CRAFTER, SFMBlocks.CRAFTER);
		justParent(SFMItems.WORKSTATION, SFMBlocks.WORKSTATION);
	}

	private void justParent(
		RegistryObject<? extends Item> item,
		RegistryObject<? extends Block> block
	) {
		withExistingParent(
			block.getId().getPath(),
			SFM.MOD_ID + ":block/" + item.getId().getPath()
		);
	}
}
