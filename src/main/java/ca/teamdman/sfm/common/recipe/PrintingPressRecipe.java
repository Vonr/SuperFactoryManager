package ca.teamdman.sfm.common.recipe;

import ca.teamdman.sfm.common.blockentity.PrintingPressBlockEntity;
import ca.teamdman.sfm.common.item.FormItem;
import ca.teamdman.sfm.common.registry.SFMRecipeSerializers;
import ca.teamdman.sfm.common.registry.SFMRecipeTypes;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Printing press copies a form using ink and paper.
 */
public class PrintingPressRecipe implements Recipe<PrintingPressBlockEntity> {
    public final Ingredient FORM;
    public final Ingredient INK;
    public final Ingredient PAPER;

    public PrintingPressRecipe(
            Ingredient form,
            Ingredient ink,
            Ingredient paper
    ) {
        this.FORM = form;
        this.INK = ink;
        this.PAPER = paper;
    }

    @Override
    public boolean matches(PrintingPressBlockEntity pContainer, Level pLevel) {
        return PAPER.test(pContainer.getPaper()) && INK.test(pContainer.getInk()) && FORM.test(FormItem.getReference(
                pContainer.getForm()));
    }

    @Override
    public ItemStack assemble(PrintingPressBlockEntity pContainer, RegistryAccess p_267165_) {
        ItemStack rtn = FormItem.getReference(pContainer.getForm());
        rtn.setCount(pContainer.getPaper().getCount());
        return rtn;
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return ItemStack.EMPTY;

    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SFMRecipeSerializers.PRINTING_PRESS.get();
    }

    @Override
    public RecipeType<?> getType() {
        return SFMRecipeTypes.PRINTING_PRESS.get();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (PrintingPressRecipe) obj;
        return
                Objects.equals(this.FORM, that.FORM) &&
                Objects.equals(this.INK, that.INK) &&
                Objects.equals(this.PAPER, that.PAPER);
    }

    @Override
    public int hashCode() {
        return Objects.hash(FORM, INK, PAPER);
    }

    @Override
    public String toString() {
        return "PrintingPressRecipe[" +
               "form=" + FORM + ", " +
               "ink=" + INK + ", " +
               "paper=" + PAPER + ']';
    }

    public static class Serializer implements RecipeSerializer<PrintingPressRecipe> {
        private final Codec<PrintingPressRecipe> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Ingredient.CODEC.fieldOf("form").forGetter(recipe -> recipe.FORM),
                Ingredient.CODEC.fieldOf("ink").forGetter(recipe -> recipe.INK),
                Ingredient.CODEC.fieldOf("paper").forGetter(recipe -> recipe.PAPER)
        ).apply(instance, PrintingPressRecipe::new));

        @Override
        public Codec<PrintingPressRecipe> codec() {
            return CODEC;
        }

        @Override
        public PrintingPressRecipe fromNetwork(FriendlyByteBuf friendlyByteBuf) {
            Ingredient form = Ingredient.fromNetwork(friendlyByteBuf);
            Ingredient ink = Ingredient.fromNetwork(friendlyByteBuf);
            Ingredient paper = Ingredient.fromNetwork(friendlyByteBuf);
            return new PrintingPressRecipe(form, ink, paper);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, PrintingPressRecipe pRecipe) {
            pRecipe.FORM.toNetwork(pBuffer);
            pRecipe.INK.toNetwork(pBuffer);
            pRecipe.PAPER.toNetwork(pBuffer);
        }
    }

}
