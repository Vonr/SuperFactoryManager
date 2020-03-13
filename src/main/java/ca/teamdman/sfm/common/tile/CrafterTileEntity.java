package ca.teamdman.sfm.common.tile;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.registrar.TileEntityRegistrar;
import net.minecraft.crash.ReportedException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CrafterTileEntity extends TileEntity implements ICapabilityProvider {
	public final  ItemStackHandler                     inventory                         = new ItemStackHandler(10) {
		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			CrafterTileEntity.this.markDirty();
		}
	};
	private final LazyOptional<ItemStackHandler>       inventoryCapabilityExternal       = LazyOptional.of(() -> this.inventory);
	private final LazyOptional<IItemHandlerModifiable> inventoryInputCapabilityExternal  = LazyOptional.of(() -> new RangedWrapper(this.inventory, 0, 10));
	private final LazyOptional<IItemHandlerModifiable> inventoryOutputCapabilityExternal = LazyOptional.of(() -> new RangedWrapper(this.inventory, 10, 11));


	public CrafterTileEntity() {
		this(TileEntityRegistrar.Tiles.CRAFTER);
	}

	public CrafterTileEntity(final TileEntityType<?> type) {
		super(type);
	}

	@Override
	public void remove() {
		super.remove();
		inventoryCapabilityExternal.invalidate();
		inventoryOutputCapabilityExternal.invalidate();
		inventoryInputCapabilityExternal.invalidate();
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
	public void read(CompoundNBT compound) {
		super.read(compound);
		this.inventory.deserializeNBT(compound.getCompound("inv"));
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.put("inv", inventory.serializeNBT());
		return compound;
	}

}
