package ca.teamdman.sfm.client.jei;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.recipe.PrintingPressRecipe;
import ca.teamdman.sfm.common.registry.SFMBlocks;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class PrintingPressJEICategory implements IRecipeCategory<PrintingPressRecipe> {

    public static final RecipeType<PrintingPressRecipe> RECIPE_TYPE = RecipeType.create(
            SFM.MOD_ID,
            "printing_press",
            PrintingPressRecipe.class
    );
    private final IDrawable background;
    private final IDrawable icon;

    public PrintingPressJEICategory(IJeiHelpers jeiHelpers) {
        background = jeiHelpers.getGuiHelper().createBlankDrawable(100, 100);
        icon = jeiHelpers.getGuiHelper().createDrawableItemStack(new ItemStack(SFMBlocks.PRINTING_PRESS_BLOCK.get()));
    }

    @Override
    public RecipeType<PrintingPressRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Constants.LocalizationKeys.PRINTING_PRESS_JEI_CATEGORY_TITLE.getComponent();
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, PrintingPressRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 10).addIngredients(recipe.FORM);
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 40).addIngredients(recipe.INK);
        builder.addSlot(RecipeIngredientRole.INPUT, 10, 60).addIngredients(recipe.PAPER);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 60, 25).addIngredients(recipe.FORM);
    }
}
