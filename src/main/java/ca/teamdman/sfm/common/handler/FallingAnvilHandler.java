package ca.teamdman.sfm.common.handler;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.item.FormItem;
import ca.teamdman.sfm.common.recipe.PrintingPressRecipe;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.common.registry.SFMRecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@EventBusSubscriber(modid = SFM.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class FallingAnvilHandler {
    @SubscribeEvent
    public static void onLeave(EntityLeaveLevelEvent event) {
        if (!(event.getEntity() instanceof FallingBlockEntity fbe)) {
            return;
        }
        if (!(fbe.getBlockState().getBlock() instanceof AnvilBlock)) {
            return;
        }
        var landPosition = fbe.blockPosition();
        Level level = event.getLevel();
        if (!level.isLoaded(landPosition.below())) {
            // avoid problems when the server is shutting down
            // https://github.com/TeamDman/SuperFactoryManager/issues/114
            return;
        }

        Block block = level.getBlockState(landPosition.below()).getBlock();
        if (block == Blocks.IRON_BLOCK) { // create a form
            var items = getItemEntities(level, landPosition);
            handleCreatePrintingPressForm(level, items, landPosition);
        } else if (block == Blocks.OBSIDIAN) { // crush and disenchant items
            List<ItemEntity> items = getItemEntities(level, landPosition);
            handleEnchantedBookCrushing(items);
            handleEnchantmentStripping(items, level, landPosition);
        }
    }

    private static void handleEnchantmentStripping(
            List<ItemEntity> items,
            Level level,
            BlockPos landPosition
    ) {
        // find enchanted items
        List<ItemEntity> enchantedItemEntities = items
                .stream()
                .filter(entity -> !entity
                        .getItem()
                        .getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY)
                        .isEmpty())
                .toList();
        if (enchantedItemEntities.isEmpty()) return;

        // count how many destination books we have
        List<ItemEntity> bookEntities = items.stream().filter(e -> e.getItem().is(Items.BOOK)).toList();
        int booksAvailable = bookEntities.stream().mapToInt(e -> e.getItem().getCount()).sum();

        // for each enchanted item, strip the enchantments
        for (ItemEntity enchantedEntity : enchantedItemEntities) {

            ItemStack enchantedStack = enchantedEntity.getItem();
            int enchantedStackSize = enchantedStack.getCount();
            ItemEnchantments startingEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(enchantedStack);
            ItemEnchantments.Mutable resultEnchantments = new ItemEnchantments.Mutable(startingEnchantments);

            for (var entry : startingEnchantments.entrySet()) {
                // Get enchantment to strip
                var enchantmentKind = entry.getKey();
                var enchantmentLevel = entry.getIntValue();

                // Abort when not enough books
                if (booksAvailable < enchantedStackSize) break;

                // Create enchanted book
                ItemStack resultBook = new ItemStack(Items.ENCHANTED_BOOK, enchantedStackSize);
                resultBook.enchant(enchantmentKind, enchantmentLevel);
                level.addFreshEntity(new ItemEntity(
                        level,
                        landPosition.getX(),
                        landPosition.getY(),
                        landPosition.getZ(),
                        resultBook
                ));
                booksAvailable -= enchantedStackSize;

                // Strip the enchantment from the source item
                //noinspection deprecation
                resultEnchantments.removeIf(e -> e.is(enchantmentKind));
                if (resultEnchantments.keySet().isEmpty()) {
                    break;
                }
            }

            // Apply enchantment removal to source item
            EnchantmentHelper.setEnchantments(enchantedStack, resultEnchantments.toImmutable());
        }

        // Ensure remaining book count correct
        for (ItemEntity bookEntity : bookEntities) {
            bookEntity.kill();
        }
        while (booksAvailable > 0) {
            int stackSize = Math.min(booksAvailable, 64);
            level.addFreshEntity(new ItemEntity(
                    level,
                    landPosition.getX(),
                    landPosition.getY(),
                    landPosition.getZ(),
                    new ItemStack(Items.BOOK, stackSize)
            ));
            booksAvailable -= stackSize;
        }
    }

    private static void handleEnchantedBookCrushing(List<ItemEntity> items) {
        items
                .stream()
                .filter(e -> e.getItem().is(Items.ENCHANTED_BOOK))
                .forEach(e -> e.setItem(new ItemStack(
                        SFMItems.EXPERIENCE_SHARD_ITEM.get(),
                        e.getItem().getCount()
                )));
    }

    private static void handleCreatePrintingPressForm(
            Level level,
            List<ItemEntity> items,
            BlockPos landPosition
    ) {
        var recipes = level
                .getRecipeManager()
                .getAllRecipesFor(SFMRecipeTypes.PRINTING_PRESS.get());
        boolean didForm = false;

        for (ItemEntity item : items) {
            for (RecipeHolder<PrintingPressRecipe> recipe : recipes) {
                // check if the item can be turned into a form
                if (recipe.value().form().test(item.getItem())) {
                    didForm = true;
                    item.setItem(FormItem.getForm(item.getItem()));
                    break;
                }
            }
        }
        if (didForm) {
            level.setBlockAndUpdate(landPosition.below(), Blocks.AIR.defaultBlockState());
        }
    }

    private static @NotNull List<ItemEntity> getItemEntities(
            Level level,
            BlockPos landPosition
    ) {
        return level
                .getEntitiesOfClass(ItemEntity.class, new AABB(landPosition))
                .stream()
                .filter(Entity::isAlive)
                .filter(e -> !e.getItem().isEmpty())
                .toList();
    }
}
