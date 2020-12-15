/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.tile;

import ca.teamdman.sfm.common.registrar.TileEntityRegistrar;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

public class CrafterTileEntity extends TileEntity implements ICapabilityProvider, IRecipeHolder {
	private final LazyOptional<ItemStackHandler>       inventoryCapabilityExternal       = LazyOptional.of(() -> this.inventory);
	private final LazyOptional<IItemHandlerModifiable> inventoryInputCapabilityExternal  = LazyOptional.of(() -> new RangedWrapper(this.inventory, 0, 10));
	private final LazyOptional<IItemHandlerModifiable> inventoryOutputCapabilityExternal = LazyOptional.of(() -> new RangedWrapper(this.inventory, 10, 11));
	private       boolean                              debounce                          = false;
	public final  ItemStackHandler                     inventory                         = new ItemStackHandler(10) {
		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			if (!debounce) {
				debounce = true;
				CrafterTileEntity.this.markDirty();
				if (slot == 9) {
					if (inventory.getStackInSlot(9) == ItemStack.EMPTY) {
						CrafterTileEntity.this.consumeIngredients(1);
					}
				}
				CrafterTileEntity.this.onInputChanged();
				debounce = false;
			}
		}
	};
	private       IRecipe<?>                           recipe;


	public CrafterTileEntity() {
		this(TileEntityRegistrar.Tiles.CRAFTER);
	}

	public CrafterTileEntity(final TileEntityType<?> type) {
		super(type);
	}

	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (side == null)
				return inventoryCapabilityExternal.cast();
			switch (side) {
				case DOWN:
					return inventoryOutputCapabilityExternal.cast();
				case UP:
				case NORTH:
				case SOUTH:
				case EAST:
				case WEST:
					return inventoryInputCapabilityExternal.cast();
			}
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.inventory.deserializeNBT(nbt);
	}

	@Override
	public CompoundNBT serializeNBT() {
		return this.inventory.serializeNBT();
	}

	@Override
	public void remove() {
		super.remove();
		inventoryCapabilityExternal.invalidate();
		inventoryOutputCapabilityExternal.invalidate();
		inventoryInputCapabilityExternal.invalidate();
	}

	public void onCraftMatrixChanged() {
		//		this.identifyRecipe();
	}

	private void identifyRecipe(World world, PlayerEntity player, CraftingInventory inv, CraftResultInventory result) {
		if (world != null && !world.isRemote) {
			ServerPlayerEntity serverPlayer = (ServerPlayerEntity) player;
			@SuppressWarnings("ConstantConditions")
			ItemStack stack = serverPlayer.getServer().getRecipeManager().getRecipe(IRecipeType.CRAFTING, inv, world)
					.filter(r -> result.canUseRecipe(world, serverPlayer, r))
					.map(r -> r.getCraftingResult(inv))
					.orElse(ItemStack.EMPTY);
			result.setInventorySlotContents(0, stack);
		}
	}

	public void consumeIngredients(int amount) {
		for (int i = 0; i < 9; i++) {
			ItemStack stack = inventory.getStackInSlot(i).copy();
			stack.setCount(stack.getCount() - amount);
			inventory.setStackInSlot(i, stack);
		}
	}

	public void onInputChanged() {
		CraftingInventory guh = new CraftingInventory(new Container(ContainerType.CRAFTING, -1) {
			@Override
			public void onCraftMatrixChanged(IInventory inventoryIn) {
			}

			@Override
			public boolean canInteractWith(PlayerEntity playerIn) {
				return false;
			}
		}, 3, 3);
		for (int i = 0; i < 9; i++)
			guh.setInventorySlotContents(i, inventory.getStackInSlot(i));
		List<ICraftingRecipe> recipes = world.getRecipeManager().getRecipes(IRecipeType.CRAFTING, guh, world);
		if (recipes.size() > 0)
			inventory.setStackInSlot(9, recipes.get(0).getRecipeOutput().copy());
		else
			inventory.setStackInSlot(9, ItemStack.EMPTY);
	}

	@Override
	public void setRecipeUsed(@Nullable IRecipe<?> recipe) {
		this.recipe = recipe;
	}

	@Nullable
	@Override
	public IRecipe<?> getRecipeUsed() {
		return recipe;
	}
}
