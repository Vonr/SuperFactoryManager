package ca.teamdman.sfm.common.item;

import ca.teamdman.sfm.common.registrar.SFMItems;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class CraftingContract extends Item {

	public CraftingContract() {
		super(new Item.Properties()
			.group(SFMItems.GROUP)
			.setISTER(() -> CraftingContractItemStackTileEntityRenderer::new)
		);
	}


	public static ItemStack withRecipe(ICraftingRecipe recipe) {
		ItemStack stack = new ItemStack(SFMItems.CRAFTING_CONTRACT.get());
		stack.getOrCreateTag().putString("recipeId", recipe.getId().toString());
		return stack;
	}

	public static Optional<ICraftingRecipe> getRecipe(
		ItemStack stack,
		@Nonnull World world
	) {
		if (stack.getItem() != SFMItems.CRAFTING_CONTRACT.get()) {
			return Optional.empty();
		}
		CompoundNBT tag = stack.getTag();
		if (tag == null) return Optional.empty();
		ResourceLocation recipeId = ResourceLocation.tryCreate(
			tag.getString("recipeId")
		);
		if (recipeId == null) return Optional.empty();

		return world
			.getRecipeManager()
			.getRecipe(recipeId)
			.filter(x -> x instanceof ICraftingRecipe)
			.map(ICraftingRecipe.class::cast);
	}
}
