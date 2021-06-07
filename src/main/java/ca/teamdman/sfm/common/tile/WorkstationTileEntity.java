package ca.teamdman.sfm.common.tile;

import ca.teamdman.sfm.common.inventory.ContractInventory;
import ca.teamdman.sfm.common.inventory.PersistentCraftingInventory;
import ca.teamdman.sfm.common.item.CraftingContractItem;
import ca.teamdman.sfm.common.registrar.SFMTiles;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class WorkstationTileEntity extends TileEntity {

	public final ItemStackHandler CONTRACT_INVENTORY
		= new ContractInventory(this);

	public final PersistentCraftingInventory CRAFTING_INVENTORY
		= new PersistentCraftingInventory(
		3,
		3,
		this::onCraftingOutputChanged,
		this::getWorld
	);

	public void onCraftingOutputChanged() {
		this.markDirty();
		IRecipe<CraftingInventory> latest = this.CRAFTING_INVENTORY.getLatestRecipe();
		ItemStack result = ItemStack.EMPTY;
		if (latest != null) {
			result = CraftingContractItem.withRecipe(latest);
		}
		CONTRACT_OUTPUT_INVENTORY.setStackInSlot(0, result);
	}

	public final LazyOptional<ItemStackHandler> INVENTORY_CAPABILITY
		= LazyOptional.of(() -> CONTRACT_INVENTORY);

	public final ItemStackHandler CONTRACT_OUTPUT_INVENTORY = new ItemStackHandler(
		1);

	public WorkstationTileEntity() {
		super(SFMTiles.WORKSTATION.get());
	}


	@Nonnull
	@Override
	public <T> LazyOptional<T> getCapability(
		@Nonnull Capability<T> cap, @Nullable Direction side
	) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return INVENTORY_CAPABILITY.cast();
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void read(
		BlockState state, CompoundNBT nbt
	) {
		super.read(state, nbt);
		CONTRACT_INVENTORY.deserializeNBT(nbt.getCompound("contracts"));
		CRAFTING_INVENTORY.deserializeNBT(nbt.getCompound("crafting"));
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		CompoundNBT tag = super.write(compound);
		tag.put("contracts", CONTRACT_INVENTORY.serializeNBT());
		tag.put("crafting", CRAFTING_INVENTORY.serializeNBT());
		return tag;
	}

}
