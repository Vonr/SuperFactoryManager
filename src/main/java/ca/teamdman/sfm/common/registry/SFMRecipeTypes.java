package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.recipe.PrintingPressRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;

public class SFMRecipeTypes {
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(
            ForgeRegistries.RECIPE_TYPES,
            SFM.MOD_ID
    );

    public static final RegistryObject<RecipeType<PrintingPressRecipe>> PRINTING_PRESS = RECIPE_TYPES.register(
            "printing_press",
            () -> RecipeType.simple(new ResourceLocation(SFM.MOD_ID, "printing_press"))
    );

    public static void register(IEventBus bus) {
        RECIPE_TYPES.register(bus);
    }
}
