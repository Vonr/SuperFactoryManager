package ca.teamdman.sfm.common.tile;

import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.registrar.TileEntityRegistrar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class ManagerTileEntity extends TileEntity {
	private int myNumber = 0;

	public ManagerTileEntity() {
		this(TileEntityRegistrar.Tiles.MANAGER);
	}

	public ManagerTileEntity(final TileEntityType<?> type) {
		super(type);
	}

	public int getMyNumber() {
		return myNumber;
	}

	public void setMyNumber(int myNumber) {
		this.myNumber = myNumber;
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("myNumber", myNumber);
		return compound;
	}

	@Override
	public void read(CompoundNBT compound) {
		this.myNumber = compound.getInt("myNumber");
	}
}
