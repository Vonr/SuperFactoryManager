package ca.teamdman.sfm.common.inventory;

import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

public class PersistentCraftingInventory extends ItemStackHandler {

	private final int WIDTH;
	private final int HEIGHT;
	private final Runnable CHANGE_CALLBACK;
	private final Supplier<World> WORLD_SUPPLIER;
	private boolean debounce = false;
	private IRecipe<CraftingInventory> latest;

	public PersistentCraftingInventory(
		int width,
		int height,
		Runnable changeCallback,
		Supplier<World> worldSupplier
	) {
		super(width * height + 1);
		WIDTH = width;
		HEIGHT = height;
		CHANGE_CALLBACK = changeCallback;
		WORLD_SUPPLIER = worldSupplier;
	}

	@Override
	protected void onContentsChanged(int slot) {
		// prevent recursion
		if (debounce) return;
		debounce = true;

		// pulled from last slot, consume ingredients
		if (slot == getSlots()-1) {
			if (getStackInSlot(getSlots()-1).isEmpty()) {
				consumeIngredients();
			}
		}

		// inventory updated, refresh recipe
		World world = WORLD_SUPPLIER.get();
		if (world != null) {
			updateOutputSlot(world);
		}

		// notify of inventory change
		CHANGE_CALLBACK.run();

		debounce = false;
	}

	public void consumeIngredients() {
		for (int i = 0; i < getSlots() - 1; i++) {
			extractItem(i, 1, false);
		}
	}

	private void updateOutputSlot(@Nonnull World world) {
		Optional<ICraftingRecipe> recipe = identifyRecipe(world);
		if (recipe.isPresent()) {
			latest = recipe.get();
			setStackInSlot(getSlots() - 1, recipe.get().getResultItem().copy());
		} else {
			latest = null;
			setStackInSlot(getSlots() - 1, ItemStack.EMPTY);
		}
	}

	private Optional<ICraftingRecipe> identifyRecipe(
		@Nonnull World world
	) {
		CraftingInventory craftingInventory = new CraftingInventory(
			new CraftingContainer(),
			WIDTH,
			HEIGHT
		);
		for (int i = 0; i < getSlots() - 1; i++) {
			craftingInventory.setItem(i, getStackInSlot(i));
		}
		return world.getRecipeManager()
			.getRecipesFor(IRecipeType.CRAFTING, craftingInventory, world)
			.stream()
			.findFirst();
	}

	public IRecipe<CraftingInventory> getLatestRecipe() {
		return latest;
	}

	private static class CraftingContainer extends Container {

		public CraftingContainer() {
			super(ContainerType.CRAFTING, -1);
		}

		@Override
		public void slotsChanged(IInventory p_75130_1_) {
//			super.slotsChanged(p_75130_1_);
		}

		@Override
		public boolean stillValid(PlayerEntity p_75145_1_) {
			return false;
		}
	}
}
