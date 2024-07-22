package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.recipe.PrintingPressRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SFMRecipeTypes {
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(
            BuiltInRegistries.RECIPE_TYPE,
            SFM.MOD_ID
    );

    public static final Supplier<RecipeType<PrintingPressRecipe>> PRINTING_PRESS = RECIPE_TYPES.register(
            "printing_press",
            () -> RecipeType.simple(ResourceLocation.fromNamespaceAndPath(SFM.MOD_ID, "printing_press"))
    );

    public static void register(IEventBus bus) {
        RECIPE_TYPES.register(bus);
    }
}
