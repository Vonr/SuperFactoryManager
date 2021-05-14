package ca.teamdman.sfm.common.tile;

import ca.teamdman.sfm.common.registrar.SFMContainers;
import ca.teamdman.sfm.common.registrar.SFMTiles;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class WorkstationTileEntity extends TileEntity implements
	INamedContainerProvider {

	public final ItemStackHandler INVENTORY = new ItemStackHandler(27) {
		@Override
		protected void onContentsChanged(int slot) {
			WorkstationTileEntity.this.markDirty();
		}
	};
	public final LazyOptional<ItemStackHandler> INVENTORY_CAPABILITY = LazyOptional
		.of(() -> INVENTORY);

	public WorkstationTileEntity() {
		super(SFMTiles.WORKSTATION.get());
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("container.sfm.workstation");
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

	@Nullable
	@Override
	public Container createMenu(
		int windowId,
		PlayerInventory playerInventory,
		PlayerEntity player
	) {
		return SFMContainers.WORKSTATION.get().createServerContainer(
			windowId,
			this,
			((ServerPlayerEntity) player)
		);
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
}
