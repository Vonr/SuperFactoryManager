package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.recipe.DiskResetRecipe;
import ca.teamdman.sfm.common.recipe.PrintingPressRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SFMRecipeSerializers {
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(
            BuiltInRegistries.RECIPE_SERIALIZER,
            SFM.MOD_ID
    );

    public static final Supplier<RecipeSerializer<PrintingPressRecipe>> PRINTING_PRESS = RECIPE_SERIALIZERS.register(
            "printing_press",
            PrintingPressRecipe.Serializer::new
    );

    public static final Supplier<SimpleCraftingRecipeSerializer<DiskResetRecipe>> DISK_RESET = RECIPE_SERIALIZERS.register(
            "disk_reset",
            () -> new SimpleCraftingRecipeSerializer<>(DiskResetRecipe::new)
    );

    public static void register(IEventBus bus) {
        RECIPE_SERIALIZERS.register(bus);
    }
}
