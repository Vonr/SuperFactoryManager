package ca.teamdman.sfm.common.item;

import ca.teamdman.sfm.common.registrar.SFMItems;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class CraftingContractItem extends Item {

	public CraftingContractItem() {
		super(new Item.Properties()
			.tab(SFMItems.GROUP)
			.setISTER(() -> CraftingContractItemStackTileEntityRenderer::new)
		);
	}

//	public static ItemStack withRecipe(ICraftingRecipe recipe) {
//		ItemStack stack = new ItemStack(SFMItems.CRAFTING_CONTRACT.get());
//		stack.getOrCreateTag().putString("recipeId", recipe.getId().toString());
//		return stack;
//	}

	public static ItemStack withRecipe(IRecipe<CraftingInventory> recipe) {
		ItemStack stack = new ItemStack(SFMItems.CRAFTING_CONTRACT.get());
		stack.getOrCreateTag().putString("recipeId", recipe.getId().toString());
		return stack;
	}

	@Override
	public void appendHoverText(
		ItemStack stack,
		@Nullable World worldIn,
		List<ITextComponent> tooltip,
		ITooltipFlag flagIn
	) {
		getRecipe(stack, worldIn).ifPresent(recipe -> {
			tooltip.add(new TranslationTextComponent(
				"gui.sfm.tooltip.crafting_contract.crafts",
				recipe.getResultItem().getCount(),
				recipe.getResultItem().getDisplayName()
			).withStyle(TextFormatting.GRAY));
			if (Screen.hasShiftDown()) {
				recipe
					.getIngredients()
					.stream()
					.map(Ingredient::getItems)
					.map(x -> x.length > 0 ? x[0] : null)
					.filter(Objects::nonNull)
					.collect(Collectors.toMap(
						Function.identity(),
						ItemStack::getCount,
						Integer::sum
					))
					.forEach((ingredient, amount) -> {
						tooltip.add(new TranslationTextComponent(
							"gui.sfm.tooltip.crafting_contract.consumes",
							amount,
							ingredient.getDisplayName()
						).withStyle(TextFormatting.DARK_GRAY));
					});
			}
		});
	}

	public static Optional<ICraftingRecipe> getRecipe(
		ItemStack stack,
		@Nullable World world
	) {
		if (world == null) return Optional.empty();
		if (stack.getItem() != SFMItems.CRAFTING_CONTRACT.get()) {
			return Optional.empty();
		}
		CompoundNBT tag = stack.getTag();
		if (tag == null) return Optional.empty();
		ResourceLocation recipeId = ResourceLocation.tryParse(
			tag.getString("recipeId")
		);
		if (recipeId == null) return Optional.empty();

		return world
			.getRecipeManager()
			.byKey(recipeId)
			.filter(x -> x instanceof ICraftingRecipe)
			.map(ICraftingRecipe.class::cast);
	}

}
