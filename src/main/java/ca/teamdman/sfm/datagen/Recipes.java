package ca.teamdman.sfm.datagen;

import ca.teamdman.sfm.common.registrar.SFMBlocks;
import java.util.function.Consumer;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ForgeRecipeProvider;

public class Recipes extends ForgeRecipeProvider {

	public Recipes(DataGenerator generatorIn) {
		super(generatorIn);
	}

	@Override
	protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer) {
		ShapedRecipeBuilder.shaped(SFMBlocks.CABLE.get(), 16)
			.define('D', Tags.Items.DYES_BLACK)
			.define('G', Items.LIGHT_WEIGHTED_PRESSURE_PLATE)
			.define('C', Tags.Items.CHESTS)
			.define('B', Items.IRON_BARS)
			.pattern("DGD")
			.pattern("BCB")
			.pattern("DGD")
			.unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
			.unlockedBy("has_chest", has(Tags.Items.CHESTS))
			.save(consumer);

		ShapedRecipeBuilder.shaped(SFMBlocks.CRAFTER.get())
			.define('A', Tags.Items.DUSTS_REDSTONE)
			.define('B', Items.REPEATER)
			.define('C',Items.CRAFTING_TABLE)
			.pattern("ABA")
			.pattern("BCB")
			.pattern("ABA")
			.unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
			.save(consumer);

		ShapedRecipeBuilder.shaped(SFMBlocks.MANAGER.get())
			.define('A', Tags.Items.CHESTS)
			.define('B', SFMBlocks.CABLE.get())
			.define('C', SFMBlocks.CRAFTER.get())
			.unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
			.unlockedBy("has_chest", has(Tags.Items.CHESTS))
			.pattern("ABA")
			.pattern("BCB")
			.pattern("ABA")
			.save(consumer);

		ShapedRecipeBuilder.shaped(SFMBlocks.WATER_INTAKE.get())
			.define('I', Tags.Items.STORAGE_BLOCKS_IRON)
			.define('B', Items.BUCKET)
			.define('G', Items.IRON_BARS)
			.define('O', Items.OBSERVER)
			.unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
			.pattern("GBG")
			.pattern("GIG")
			.pattern("GOG")
			.save(consumer);
	}
}
