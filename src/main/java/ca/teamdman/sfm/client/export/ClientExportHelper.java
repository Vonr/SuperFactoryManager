package ca.teamdman.sfm.client.export;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.jei.SFMJEIPlugin;
import ca.teamdman.sfm.common.registry.SFMResourceTypes;
import ca.teamdman.sfm.common.resourcetype.ResourceType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.library.gui.recipes.RecipeLayoutBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.stream.Stream;

public class ClientExportHelper {
    public static Collection<ItemStack> gatherItems() {
        assert Minecraft.getInstance().player != null;
        assert Minecraft.getInstance().level != null;
        CreativeModeTabs.tryRebuildTabContents(
                Minecraft.getInstance().player.connection.enabledFeatures(),
                true,
                Minecraft.getInstance().level.registryAccess()
        );
        return CreativeModeTabs.searchTab().getDisplayItems();
    }

    // https://github.com/TeamDman/tell-me-my-items/blob/6fb767f0145abebff503b87a10a1810ca24580b9/mod/src/main/java/ca/teamdman/tellmemyitems/TellMeMyItems.java#L36
    public static void dumpItems(@Nullable Player player) throws IOException {
        // manually build JSON array
        JsonArray jsonArray = new JsonArray();

        var items = gatherItems();
        for (ItemStack stack : items) {
            JsonObject jsonObject = new JsonObject();

            // Add the id field
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(stack.getItem());
            assert id != null;
            jsonObject.addProperty("id", id.toString());

            // Add the data field if it exists
            if (stack.getShareTag() != null) {
                jsonObject.addProperty("data", stack.getShareTag().toString());
            }

            // Add the tooltip field (requires player)
            String tooltip = stack.getTooltipLines(player, TooltipFlag.ADVANCED)
                    .stream()
                    .map(Component::getString)
                    .reduce((line1, line2) -> line1 + "\n" + line2)
                    .orElse("");
            jsonObject.addProperty("tooltip", tooltip);

            jsonArray.add(jsonObject);
        }

        // serialize to JSON with pretty printing
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String content = gson.toJson(jsonArray);

        // ensure folder exists
        var gameDir = FMLPaths.GAMEDIR.get();
        var folder = Paths.get(gameDir.toString(), SFM.MOD_ID);
        Files.createDirectories(folder);

        // write to file
        File itemFile = new File(folder.toFile(), "items.json");
        try (FileOutputStream str = new FileOutputStream(itemFile)) {
            str.write(content.getBytes(StandardCharsets.UTF_8));
        }
        SFM.LOGGER.info("Exported item data to {}", itemFile);
        assert Minecraft.getInstance().player != null;
        Minecraft.getInstance().player.sendSystemMessage(Component.literal(String.format(
                "Exported %d items to \"%s\"",
                items.size(),
                itemFile.getAbsolutePath()
        )));
    }

    public static void dumpJei(@Nullable Player player) throws IOException {
        IJeiRuntime jeiRuntime = SFMJEIPlugin.getJeiRuntime();
        if (jeiRuntime == null) {
            SFM.LOGGER.error("No JEI runtime detected, no recipes have been exported");
            return;
        }
        IJeiHelpers jeiHelpers = jeiRuntime.getJeiHelpers();
        IIngredientManager ingredientManager = jeiRuntime.getIngredientManager();
        IRecipeManager recipeManager = jeiRuntime.getRecipeManager();
        Stream<IRecipeCategory<?>> recipeCategoryStream = recipeManager
                .createRecipeCategoryLookup()
                .includeHidden()
                .get();

        // manually build JSON array
        JsonArray jsonArray = new JsonArray();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Collection<ResourceType<?, ?, ?>> resourceTypes = SFMResourceTypes.DEFERRED_TYPES.get().getValues();
        for (IRecipeCategory<?> recipeCategory : ((Iterable<IRecipeCategory<?>>) recipeCategoryStream::iterator)) {
            extractCategory(gson, jeiHelpers, recipeCategory, recipeManager, ingredientManager, jsonArray,
                            resourceTypes
            );
        }

        // serialize to JSON with pretty printing
        String content = gson.toJson(jsonArray);


        // ensure folder exists
        var gameDir = FMLPaths.GAMEDIR.get();
        var folder = Paths.get(gameDir.toString(), SFM.MOD_ID);
        Files.createDirectories(folder);

        // write to file
        File file = new File(folder.toFile(), "jei.json");
        try (FileOutputStream str = new FileOutputStream(file)) {
            str.write(content.getBytes(StandardCharsets.UTF_8));
        }
        SFM.LOGGER.info("Exported item data to {}", file);
        assert Minecraft.getInstance().player != null;
        Minecraft.getInstance().player.sendSystemMessage(Component.literal(String.format(
                "Exported %d JEI entries to \"%s\"",
                jsonArray.size(),
                file.getAbsolutePath()
        )));
    }

    private static <T> void extractCategory(
            Gson gson,
            IJeiHelpers jeiHelpers,
            IRecipeCategory<T> recipeCategory,
            IRecipeManager recipeManager,
            IIngredientManager ingredientManager,
            JsonArray jsonArray,
            Collection<ResourceType<?, ?, ?>> resourceTypes
    ) {
        RecipeType<T> recipeType = recipeCategory.getRecipeType();
        Stream<T> recipes = recipeManager.createRecipeLookup(recipeType).includeHidden().get();
        recipes.forEach(recipe -> {
            // Get the recipe data
            // from mezz.jei.library.gui.recipes.RecipeLayout.java
            int ingredientCycleOffset = (int) ((Math.random() * 10000) % Integer.MAX_VALUE);
            RecipeLayoutBuilder recipeLayoutBuilder = new RecipeLayoutBuilder(ingredientManager, ingredientCycleOffset);
            recipeCategory.setRecipe(recipeLayoutBuilder, recipe, jeiHelpers.getFocusFactory().getEmptyFocusGroup());

            // Build ingredient info
            JsonArray ingredientArray = new JsonArray();
            for (RecipeIngredientRole recipeIngredientRole : RecipeIngredientRole.values()) {
                recipeLayoutBuilder
                        .getIngredientTypes(recipeIngredientRole)
                        .forEachOrdered((IIngredientType<?> ingredientType) -> {
                            recipeLayoutBuilder
                                    .getIngredientStream(ingredientType, recipeIngredientRole)
                                    .forEachOrdered(ingredient -> {
                                        JsonObject ingredientObject = new JsonObject();
                                        ingredientObject.addProperty("role", recipeIngredientRole.toString());
                                        ingredientObject.addProperty(
                                                "ingredientType",
                                                ingredientType.getIngredientClass().getName()
                                        );
                                        //noinspection rawtypes
                                        for (ResourceType resourceType : resourceTypes) {
                                            if (resourceType.matchesStackType(ingredient)) {
                                                //noinspection unchecked
                                                addIngredientInfo(resourceType, ingredient, ingredientObject);
                                            }
                                        }
                                        ingredientObject.addProperty("ingredient", ingredient.toString());
                                        ingredientArray.add(ingredientObject);
                                    });
                        });
            }

            // Add results
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("category", recipeCategory.toString());
            jsonObject.addProperty("recipeTypeId", recipeType.getUid().toString());
            jsonObject.addProperty("recipeClass", recipeType.getRecipeClass().toString());
            jsonObject.addProperty("recipeObject", recipe.toString());
            jsonObject.add("ingredients", ingredientArray);
            jsonArray.add(jsonObject);
        });
    }

    private static <STACK,ITEM,CAP> void addIngredientInfo(
            ResourceType<STACK,ITEM,CAP> resourceType,
            STACK stack,
            JsonObject ingredientObject
    ) {
        long amount = resourceType.getAmount(stack);
        ingredientObject.addProperty("ingredientAmount", amount);

        ResourceLocation stackRegistryKey = resourceType.getRegistryKey(stack);
        ingredientObject.addProperty("ingredientId", stackRegistryKey.toString());
    }
}
