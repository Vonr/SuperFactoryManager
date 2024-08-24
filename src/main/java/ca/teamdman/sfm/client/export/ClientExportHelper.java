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
import mezz.jei.api.recipe.IRecipeLookup;
import mezz.jei.api.recipe.IRecipeManager;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.runtime.IIngredientManager;
import mezz.jei.api.runtime.IJeiRuntime;
import mezz.jei.library.gui.recipes.RecipeLayoutBuilder;
import net.minecraft.ChatFormatting;
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
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class ClientExportHelper {

    private static final Object registryReaderLock = new Object();

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

            // Add the tags
            JsonArray tags = new JsonArray();
            SFMResourceTypes.ITEM.get().getTagsForStack(stack).map(ResourceLocation::toString).forEach(tags::add);
            jsonObject.add("tags", tags);

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

    public static void dumpJei(
            Player player,
            boolean includeHidden
    ) throws IOException {
        IJeiRuntime jeiRuntime = SFMJEIPlugin.getJeiRuntime();
        if (jeiRuntime == null) {
            String msg = "No JEI runtime detected, no recipes have been exported";
            SFM.LOGGER.error(msg);
            player.sendSystemMessage(Component.literal(msg).withStyle(ChatFormatting.RED));
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
            player.sendSystemMessage(
                    Component.literal("Dumping category ").append(recipeCategory.getTitle())
            );
            extractCategory(gson, jeiHelpers, recipeCategory, recipeManager, ingredientManager, jsonArray,
                            resourceTypes,
                            includeHidden,
                            player
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

        // notify the player
        player.sendSystemMessage(Component.literal(String.format(
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
            Collection<ResourceType<?, ?, ?>> resourceTypes,
            boolean includeHidden,
            Player player
    ) {
        RecipeType<T> recipeType = recipeCategory.getRecipeType();
        // do not include hidden recipes because this takes a long time in modded packs
        IRecipeLookup<T> recipeLookup = recipeManager.createRecipeLookup(recipeType);
        if (includeHidden) {
            recipeLookup = recipeLookup.includeHidden();
        }
        Stream<T> recipes = recipeLookup.get();
        ConcurrentLinkedDeque<JsonObject> recipeResults = new ConcurrentLinkedDeque<>();
        AtomicInteger counter = new AtomicInteger();
        recipes.parallel().forEach(recipe -> {
            // Get the recipe data
            // from mezz.jei.library.gui.recipes.RecipeLayout.java
            int ingredientCycleOffset = (int) ((Math.random() * 10000) % Integer.MAX_VALUE);
            RecipeLayoutBuilder recipeLayoutBuilder = new RecipeLayoutBuilder(ingredientManager, ingredientCycleOffset);
            recipeCategory.setRecipe(recipeLayoutBuilder, recipe, jeiHelpers.getFocusFactory().getEmptyFocusGroup());

            ConcurrentLinkedDeque<JsonObject> ingredientResults = new ConcurrentLinkedDeque<>();

            // Build ingredient info
            JsonArray ingredientArray = new JsonArray();
            for (RecipeIngredientRole recipeIngredientRole : RecipeIngredientRole.values()) {
                recipeLayoutBuilder
                        .getIngredientTypes(recipeIngredientRole)
                        .parallel()
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
                                        ingredientResults.add(ingredientObject);
                                    });
                        });
            }

            ingredientResults.forEach(ingredientArray::add);

            // Add results
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("category", recipeCategory.toString());
            jsonObject.addProperty("recipeTypeId", recipeType.getUid().toString());
            jsonObject.addProperty("recipeClass", recipeType.getRecipeClass().toString());
            jsonObject.addProperty("recipeObject", recipe.toString());
            jsonObject.add("ingredients", ingredientArray);
            recipeResults.add(jsonObject);

            int count = counter.getAndIncrement();
            if (count > 0 && count % 1000 == 0) {
                // notify the player
                player.sendSystemMessage(Component.literal(String.format(
                        "Processed %d recipes so far",
                        count
                )));
            }
        });

        player.sendSystemMessage(
                Component.literal("Found ")
                        .append(Component.literal(String.valueOf(recipeResults.size())).withStyle(ChatFormatting.AQUA))
                        .append(Component.literal(" recipes in total for category ").withStyle(ChatFormatting.RESET))
                        .append(recipeCategory.getTitle()));
        recipeResults.forEach(jsonArray::add);
    }

    private static <STACK, ITEM, CAP> void addIngredientInfo(
            ResourceType<STACK, ITEM, CAP> resourceType,
            STACK stack,
            JsonObject ingredientObject
    ) {
        long amount = resourceType.getAmount(stack);
        ingredientObject.addProperty("ingredientAmount", amount);

        ResourceLocation stackRegistryKey;
        synchronized (registryReaderLock) {
            stackRegistryKey = resourceType.getRegistryKey(stack);
        }
        ingredientObject.addProperty("ingredientId", stackRegistryKey.toString());

        JsonArray tags = new JsonArray();
        resourceType.getTagsForStack(stack).map(ResourceLocation::toString).forEach(tags::add);
        ingredientObject.add("tags", tags);
    }
}
