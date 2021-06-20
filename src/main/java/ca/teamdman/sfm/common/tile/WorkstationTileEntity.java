package ca.teamdman.sfm.common.tile;

import ca.teamdman.sfm.common.inventory.ContractInventory;
import ca.teamdman.sfm.common.inventory.PersistentCraftingInventory;
import ca.teamdman.sfm.common.item.CraftingContractItem;
import ca.teamdman.sfm.common.registrar.SFMTiles;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

public class WorkstationTileEntity extends TileEntity implements
	ContainerListenerTracker {

	public final ItemStackHandler CONTRACT_INVENTORY
		= new ContractInventory(this);
	public final LazyOptional<ItemStackHandler> INVENTORY_CAPABILITY
		= LazyOptional.of(() -> CONTRACT_INVENTORY);
	public final ItemStackHandler CONTRACT_OUTPUT_INVENTORY = new ItemStackHandler(
		1);
	private Map<ServerPlayerEntity, Integer> LISTENERS = new WeakHashMap<>();
	private boolean autoLearnEnabled = true;
	public final PersistentCraftingInventory CRAFTING_INVENTORY
		= new PersistentCraftingInventory(
		3,
		3,
		this::onCraftingOutputChanged,
		this::getWorld
	);

	public WorkstationTileEntity() {
		super(SFMTiles.WORKSTATION.get());
	}

	public void onCraftingOutputChanged() {
		this.markDirty();
		IRecipe<CraftingInventory> latest = this.CRAFTING_INVENTORY.getLatestRecipe();
		ItemStack contract = ItemStack.EMPTY;
		if (latest != null) {
			contract = CraftingContractItem.withRecipe(latest);
		}
		CONTRACT_OUTPUT_INVENTORY.setStackInSlot(0, contract);
		if (isAutoLearnEnabled() && !isContractPresent(contract)) {
			learnContract(contract);
		}
	}

	public boolean isAutoLearnEnabled() {
		return autoLearnEnabled;
	}

	public void setAutoLearnEnabled(boolean autoLearn) {
		this.autoLearnEnabled = autoLearn;
	}

	private boolean isContractPresent(ItemStack contract) {
		for (int i = 0; i < CONTRACT_INVENTORY.getSlots(); i++) {
			if (ItemHandlerHelper.canItemStacksStack(
				contract,
				CONTRACT_INVENTORY.getStackInSlot(i)
			)) {
				return true;
			}
		}
		return false;
	}

	private void learnContract(ItemStack contract) {
		ItemHandlerHelper.insertItem(
			CONTRACT_INVENTORY,
			contract,
			false
		);
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
		setAutoLearnEnabled(nbt.getBoolean("autolearn"));
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		CompoundNBT tag = super.write(compound);
		tag.put("contracts", CONTRACT_INVENTORY.serializeNBT());
		tag.put("crafting", CRAFTING_INVENTORY.serializeNBT());
		tag.putBoolean("autolearn", isAutoLearnEnabled());
		return tag;
	}

	@Override
	public Map<ServerPlayerEntity, Integer> getListeners() {
		return LISTENERS;
	}
}
