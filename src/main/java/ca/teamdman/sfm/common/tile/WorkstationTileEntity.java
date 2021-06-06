package ca.teamdman.sfm.common.tile;

import ca.teamdman.sfm.common.item.CraftingContractItem;
import ca.teamdman.sfm.common.registrar.SFMTiles;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class WorkstationTileEntity extends TileEntity {

	public final ItemStackHandler INVENTORY = new WorkstationInventory();
	public final LazyOptional<ItemStackHandler> INVENTORY_CAPABILITY = LazyOptional
		.of(() -> INVENTORY);

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
		INVENTORY.deserializeNBT(nbt.getCompound("inv"));
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		CompoundNBT tag = super.write(compound);
		tag.put("inv", INVENTORY.serializeNBT());
		return tag;
	}

	private class WorkstationInventory extends ItemStackHandler {

		@Override
		public boolean isItemValid(
			int slot, @Nonnull ItemStack stack
		) {
			return stack.getItem() instanceof CraftingContractItem;
		}

		public WorkstationInventory() {
			super(27);
		}

		@Override
		protected void onContentsChanged(int slot) {
			WorkstationTileEntity.this.markDirty();
		}
	}
}
