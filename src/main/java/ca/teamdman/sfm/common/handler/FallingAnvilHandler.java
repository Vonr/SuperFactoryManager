package ca.teamdman.sfm.common.handler;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.item.FormItem;
import ca.teamdman.sfm.common.recipe.PrintingPressRecipe;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.common.registry.SFMRecipeTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FallingAnvilHandler {
    @SubscribeEvent
    public static void onLeave(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof FallingBlockEntity fbe) {
            if (fbe.getBlockState().getBlock() instanceof AnvilBlock) {
                var landPosition = fbe.blockPosition();
                Level level = event.getLevel();
                Block block = level.getBlockState(landPosition.below()).getBlock();
                if (block == Blocks.IRON_BLOCK) { // create a form
                    List<PrintingPressRecipe> recipes = level
                            .getRecipeManager()
                            .getAllRecipesFor(SFMRecipeTypes.PRINTING_PRESS.get());
                    var items = level
                            .getEntitiesOfClass(ItemEntity.class, new AABB(landPosition))
                            .stream()
                            .filter(Entity::isAlive)
                            .filter(e -> !e.getItem().isEmpty())
                            .toList();
                    boolean didForm = false;

                    for (ItemEntity item : items) {
                        for (PrintingPressRecipe recipe : recipes) {
                            // check if the item can be turned into a form
                            if (recipe.FORM.test(item.getItem())) {
                                didForm = true;
                                item.setItem(FormItem.getForm(item.getItem()));
                                break;
                            }
                        }
                    }
                    if (didForm) {
                        level.setBlockAndUpdate(landPosition.below(), Blocks.AIR.defaultBlockState());
                    }
                } else if (block == Blocks.OBSIDIAN) { // crush and disenchant items
                    List<ItemEntity> items = level
                            .getEntitiesOfClass(ItemEntity.class, new AABB(landPosition))
                            .stream()
                            .filter(Entity::isAlive)
                            .filter(e -> !e.getItem().isEmpty())
                            .toList();
                    { // crush enchanted books into xp shards
                        items
                                .stream()
                                .filter(e -> e.getItem().is(Items.ENCHANTED_BOOK))
                                .forEach(e -> e.setItem(new ItemStack(
                                        SFMItems.EXPERIENCE_SHARD_ITEM.get(),
                                        e.getItem().getCount()
                                )));
                    }
                    { // remove enchantments from items
                        List<ItemEntity> bookEntities = items.stream().filter(e -> e.getItem().is(Items.BOOK)).toList();
                        int booksAvailable = bookEntities.stream().mapToInt(e -> e.getItem().getCount()).sum();
                        List<ItemEntity> enchanted = items
                                .stream()
                                .filter(e -> !e.getItem().getEnchantmentTags().isEmpty())
                                .toList();


                        for (ItemEntity enchItemEntity : enchanted) {
                            ItemStack enchStack = enchItemEntity.getItem();
                            int enchStackSize = enchStack.getCount();
                            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(enchStack);
                            var enchIter = enchantments.entrySet().iterator();
                            while (enchIter.hasNext()) {
                                var entry = enchIter.next();
                                if (booksAvailable < enchStackSize) break;

                                // Create an enchanted book with the enchantment
                                ItemStack toSpawn = new ItemStack(Items.ENCHANTED_BOOK, enchStackSize);
                                EnchantedBookItem.addEnchantment(
                                        toSpawn,
                                        new EnchantmentInstance(entry.getKey(), entry.getValue())
                                );
                                level.addFreshEntity(new ItemEntity(
                                        level,
                                        landPosition.getX(),
                                        landPosition.getY(),
                                        landPosition.getZ(),
                                        toSpawn
                                ));

                                // Remove the enchantment from the item
                                enchIter.remove();
                                EnchantmentHelper.setEnchantments(enchantments, enchStack);
                                booksAvailable -= enchStackSize;
                                if (enchantments.isEmpty()) {
                                    break;
                                }
                            }
                        }

                        for (ItemEntity bookEntity : bookEntities) {
                            bookEntity.kill();
                        }
                        while (booksAvailable > 0) {
                            int toSpawn = Math.min(booksAvailable, 64);
                            level.addFreshEntity(new ItemEntity(
                                    level,
                                    landPosition.getX(),
                                    landPosition.getY(),
                                    landPosition.getZ(),
                                    new ItemStack(Items.BOOK, toSpawn)
                            ));
                            booksAvailable -= toSpawn;
                        }
                    }
                }
            }
        }
    }
}
