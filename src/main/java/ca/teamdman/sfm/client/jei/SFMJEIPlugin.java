package ca.teamdman.sfm.client.jei;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.recipe.PrintingPressRecipe;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import ca.teamdman.sfm.common.registry.SFMRecipeTypes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class SFMJEIPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(SFM.MOD_ID, "sfm");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new PrintingPressJEICategory(registration.getJeiHelpers()),
                new FallingAnvilJEICategory(registration.getJeiHelpers())
        );
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(
                new ItemStack(SFMBlocks.PRINTING_PRESS_BLOCK.get()),
                PrintingPressJEICategory.RECIPE_TYPE
        );
        registration.addRecipeCatalyst(
                new ItemStack(Blocks.ANVIL),
                FallingAnvilJEICategory.RECIPE_TYPE
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<PrintingPressRecipe> printingPressRecipes = new ArrayList<>();
        List<FallingAnvilRecipe> fallingAnvilRecipes = new ArrayList<>();
        var level = Minecraft.getInstance().level;
        assert level != null;
        RecipeManager recipeManager = level.getRecipeManager();
        recipeManager.getAllRecipesFor(SFMRecipeTypes.PRINTING_PRESS.get()).forEach(r -> {
            printingPressRecipes.add(r);
            fallingAnvilRecipes.add(new FallingAnvilFormRecipe(r));
        });
        fallingAnvilRecipes.add(new FallingAnvilDisenchantRecipe());
        fallingAnvilRecipes.add(new FallingAnvilExperienceShardRecipe());
        registration.addRecipes(PrintingPressJEICategory.RECIPE_TYPE, printingPressRecipes);
        registration.addRecipes(FallingAnvilJEICategory.RECIPE_TYPE, fallingAnvilRecipes);
    }
}
