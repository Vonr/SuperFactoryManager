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
	protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
		ShapedRecipeBuilder.shapedRecipe(SFMBlocks.CABLE.get(), 16)
			.key('D', Tags.Items.DYES_BLACK)
			.key('G', Items.LIGHT_WEIGHTED_PRESSURE_PLATE)
			.key('C', Tags.Items.CHESTS)
			.key('B', Items.IRON_BARS)
			.patternLine("DGD")
			.patternLine("BCB")
			.patternLine("DGD")
			.addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT))
			.addCriterion("has_chest", hasItem(Tags.Items.CHESTS))
			.build(consumer);

		ShapedRecipeBuilder.shapedRecipe(SFMBlocks.CRAFTER.get())
			.key('A', Tags.Items.DUSTS_REDSTONE)
			.key('B', Items.REPEATER)
			.key('C',Items.CRAFTING_TABLE)
			.patternLine("ABA")
			.patternLine("BCB")
			.patternLine("ABA")
			.addCriterion("has_redstone", hasItem(Tags.Items.DUSTS_REDSTONE))
			.build(consumer);

		ShapedRecipeBuilder.shapedRecipe(SFMBlocks.MANAGER.get())
			.key('A', Tags.Items.CHESTS)
			.key('B', SFMBlocks.CABLE.get())
			.key('C', SFMBlocks.CRAFTER.get())
			.addCriterion("has_iron_ingot", hasItem(Items.IRON_INGOT))
			.addCriterion("has_chest", hasItem(Tags.Items.CHESTS))
			.patternLine("ABA")
			.patternLine("BCB")
			.patternLine("ABA")
			.build(consumer);
	}
}
