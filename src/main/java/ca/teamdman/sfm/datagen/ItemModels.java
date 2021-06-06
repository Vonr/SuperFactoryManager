package ca.teamdman.sfm.datagen;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.registrar.SFMBlocks;
import ca.teamdman.sfm.common.registrar.SFMItems;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
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
		justParent(SFMItems.WATER_INTAKE, SFMBlocks.WATER_INTAKE, "_active");
		basicItem(SFMItems.CRAFTING_CONTRACT);
//		getBuilder(SFMItems.CRAFTING_CONTRACT.getId().getPath())
//			.parent(HackModel.INSTANCE);
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

	/**
	 * Custom ModelFile to point to "builtin/entity" as a parent. Normal datagen
	 * stuff says it doesn't exist, and I don't know what I'm doing well enough
	 * to avoid this hack.
	 * <p>
	 * Used to make .isBuiltInRenderer return `true` by having
	 * paren="builtin/entiy" This is needed for ISTSR to work without creating a
	 * custom IBakedModel.
	 */
	private static class HackModel extends ModelFile {

		public static HackModel INSTANCE = new HackModel();

		protected HackModel() {
			super(null);
		}

		@Override
		protected boolean exists() {
			return true;
		}

		@Override
		public ResourceLocation getLocation() {
			return new ResourceLocation("builtin/entity") {
				/**
				 * Avoid "minecraft:" prefix.
				 */
				@Override
				public String toString() {
					return getPath();
				}
			};
		}

		@Override
		public void assertExistence() {
		}
	}
}
