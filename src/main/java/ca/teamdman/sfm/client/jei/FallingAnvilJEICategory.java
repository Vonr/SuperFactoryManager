package ca.teamdman.sfm.client.jei;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.item.FormItem;
import ca.teamdman.sfm.common.registry.SFMItems;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FallingAnvilJEICategory implements IRecipeCategory<FallingAnvilRecipe> {

    public static final RecipeType<FallingAnvilRecipe> RECIPE_TYPE = RecipeType.create(
            SFM.MOD_ID,
            "falling_anvil",
            FallingAnvilRecipe.class
    );
    private final IDrawable background;
    private final IDrawable icon;

    public FallingAnvilJEICategory(IJeiHelpers jeiHelpers) {
        background = jeiHelpers.getGuiHelper().createBlankDrawable(80, 54);
        icon = jeiHelpers.getGuiHelper().createDrawableItemStack(new ItemStack(Blocks.ANVIL));
    }

    @Override
    public RecipeType<FallingAnvilRecipe> getRecipeType() {
        return RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Constants.LocalizationKeys.FALLING_ANVIL_JEI_CATEGORY_TITLE.getComponent();
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
    public void setRecipe(IRecipeLayoutBuilder builder, FallingAnvilRecipe recipe, IFocusGroup focuses) {
        var anvil = List.of(
                new ItemStack(Items.ANVIL),
                new ItemStack(Items.CHIPPED_ANVIL),
                new ItemStack(Items.DAMAGED_ANVIL)
        );
        if (recipe instanceof FallingAnvilFormRecipe formRecipe) {
            builder.addSlot(RecipeIngredientRole.CATALYST, 0, 0).addItemStacks(anvil);
            builder.addSlot(RecipeIngredientRole.INPUT, 0, 18).addIngredients(formRecipe.PARENT.FORM);
            ItemStack ironBlock = new ItemStack(Blocks.IRON_BLOCK);
            var displayTag = ironBlock.getOrCreateTag().getCompound("display");
            var lore = new ListTag();
            lore.add(StringTag.valueOf(Component.Serializer.toJson(Constants.LocalizationKeys.FALLING_ANVIL_JEI_CONSUMED.getComponent())));
            displayTag.put("Lore", lore);
            ironBlock.getOrCreateTag().put("display", displayTag);
            builder.addSlot(RecipeIngredientRole.INPUT, 0, 36).addItemStack(ironBlock);
            builder
                    .addSlot(RecipeIngredientRole.OUTPUT, 50, 18)
                    .addItemStacks(Arrays.stream(formRecipe.PARENT.FORM.getItems()).map(FormItem::getForm).toList());
        } else if (recipe instanceof FallingAnvilDisenchantRecipe) {
            Collection<Enchantment> enchants = ForgeRegistries.ENCHANTMENTS.getValues();

            var tools = List.of(
                    Items.DIAMOND_HELMET,
                    Items.DIAMOND_CHESTPLATE,
                    Items.DIAMOND_LEGGINGS,
                    Items.DIAMOND_BOOTS,
                    Items.DIAMOND_PICKAXE,
                    Items.DIAMOND_SHOVEL,
                    Items.DIAMOND_AXE,
                    Items.DIAMOND_HOE,
                    Items.DIAMOND_SWORD,
                    Items.GOLDEN_HELMET,
                    Items.GOLDEN_CHESTPLATE,
                    Items.GOLDEN_LEGGINGS,
                    Items.GOLDEN_BOOTS,
                    Items.GOLDEN_PICKAXE,
                    Items.GOLDEN_SHOVEL,
                    Items.GOLDEN_AXE,
                    Items.GOLDEN_HOE,
                    Items.GOLDEN_SWORD,
                    Items.IRON_HELMET,
                    Items.IRON_CHESTPLATE,
                    Items.IRON_LEGGINGS,
                    Items.IRON_BOOTS,
                    Items.IRON_PICKAXE,
                    Items.IRON_SHOVEL,
                    Items.IRON_AXE,
                    Items.IRON_HOE,
                    Items.IRON_SWORD,
                    Items.LEATHER_HELMET,
                    Items.LEATHER_CHESTPLATE,
                    Items.LEATHER_LEGGINGS,
                    Items.LEATHER_BOOTS,
                    Items.CHAINMAIL_HELMET,
                    Items.CHAINMAIL_CHESTPLATE,
                    Items.CHAINMAIL_LEGGINGS,
                    Items.CHAINMAIL_BOOTS,
                    Items.WOODEN_PICKAXE,
                    Items.WOODEN_SHOVEL,
                    Items.WOODEN_AXE,
                    Items.WOODEN_HOE,
                    Items.WOODEN_SWORD,
                    Items.BOW
            );
            var enchanted = new ArrayList<ItemStack>();
            var books = new ArrayList<ItemStack>();
            for (Enchantment enchant : enchants) {
                for (Item tool : tools) {
                    var stack = new ItemStack(tool);
                    if (enchant.canEnchant(stack)) {
                        stack.enchant(enchant, enchant.getMaxLevel());
                        enchanted.add(stack);
                        books.add(EnchantedBookItem.createForEnchantment(new EnchantmentInstance(
                                enchant,
                                enchant.getMaxLevel()
                        )));
                    }
                }
            }

            builder.addSlot(RecipeIngredientRole.CATALYST, 8, 0).addItemStacks(anvil);
            builder.addSlot(RecipeIngredientRole.INPUT, 0, 18).addItemStacks(enchanted);
            builder.addSlot(RecipeIngredientRole.INPUT, 18, 18).addItemStack(new ItemStack(Items.BOOK));
            ItemStack obsidian = new ItemStack(Blocks.OBSIDIAN);
            var displayTag = obsidian.getOrCreateTag().getCompound("display");
            var lore = new ListTag();
            lore.add(StringTag.valueOf(Component.Serializer.toJson(Constants.LocalizationKeys.FALLING_ANVIL_JEI_NOT_CONSUMED.getComponent())));
            displayTag.put("Lore", lore);
            obsidian.getOrCreateTag().put("display", displayTag);
            builder.addSlot(RecipeIngredientRole.INPUT, 8, 36).addItemStack(obsidian);
            builder.addSlot(RecipeIngredientRole.OUTPUT, 50, 18).addItemStacks(books);
        } else if (recipe instanceof FallingAnvilExperienceShardRecipe) {
            builder.addSlot(RecipeIngredientRole.CATALYST, 0, 0).addItemStacks(anvil);
            builder.addSlot(RecipeIngredientRole.INPUT, 0, 18).addIngredients(Ingredient.of(Items.ENCHANTED_BOOK));
            ItemStack obsidian = new ItemStack(Blocks.OBSIDIAN);
            var displayTag = obsidian.getOrCreateTag().getCompound("display");
            var lore = new ListTag();
            lore.add(StringTag.valueOf(Component.Serializer.toJson(Constants.LocalizationKeys.FALLING_ANVIL_JEI_NOT_CONSUMED.getComponent())));
            displayTag.put("Lore", lore);
            obsidian.getOrCreateTag().put("display", displayTag);
            builder.addSlot(RecipeIngredientRole.INPUT, 0, 36).addItemStack(obsidian);
            builder
                    .addSlot(RecipeIngredientRole.OUTPUT, 50, 18)
                    .addItemStack(new ItemStack(SFMItems.EXPERIENCE_SHARD_ITEM.get()));
        }
    }
}
