package ca.teamdman.sfm.common.handler;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.item.FormItem;
import ca.teamdman.sfm.common.recipe.PrintingPressRecipe;
import ca.teamdman.sfm.common.registry.SFMRecipeTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AnvilCreateFormHandler {
    @SubscribeEvent
    public static void onLeave(EntityLeaveLevelEvent event) {
        if (event.getEntity() instanceof FallingBlockEntity fbe) {
            if (fbe.getBlockState().getBlock() instanceof AnvilBlock) {
                var landPosition = fbe.blockPosition();
                Level level = event.getLevel();
                if (level.getBlockState(landPosition.below()).getBlock() == Blocks.IRON_BLOCK) {
                    List<PrintingPressRecipe> recipes = level
                            .getRecipeManager()
                            .getAllRecipesFor(SFMRecipeTypes.PRINTING_PRESS.get());
                    var items = level.getEntitiesOfClass(ItemEntity.class, new AABB(landPosition)).stream()
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
                }
            }
        }
    }
}
