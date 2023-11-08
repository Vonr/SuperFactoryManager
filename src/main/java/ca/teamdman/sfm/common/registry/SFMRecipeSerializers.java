package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.recipe.DiskResetRecipe;
import ca.teamdman.sfm.common.recipe.PrintingPressRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

public class SFMRecipeSerializers {
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(
            ForgeRegistries.RECIPE_SERIALIZERS,
            SFM.MOD_ID
    );

    public static final RegistryObject<RecipeSerializer<PrintingPressRecipe>> PRINTING_PRESS = RECIPE_SERIALIZERS.register(
            "printing_press",
            PrintingPressRecipe.Serializer::new
    );

    public static final RegistryObject<SimpleCraftingRecipeSerializer<DiskResetRecipe>> DISK_RESET = RECIPE_SERIALIZERS.register(
            "disk_reset",
            () -> new SimpleCraftingRecipeSerializer<>(DiskResetRecipe::new)
    );

    public static void register(IEventBus bus) {
        RECIPE_SERIALIZERS.register(bus);
    }
}
