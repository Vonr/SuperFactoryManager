package ca.teamdman.sfm.common.tile;

import ca.teamdman.sfm.common.registrar.TileEntityRegistrar;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class ManagerTileEntity extends TileEntity {
	public int x, y;

	public ManagerTileEntity() {
		this(TileEntityRegistrar.Tiles.MANAGER);
	}

	public ManagerTileEntity(final TileEntityType<?> type) {
		super(type);
	}

	@Override
	public void read(CompoundNBT compound) {
		this.x = compound.getInt("x");
		this.y = compound.getInt("y");
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("x", x);
		compound.putInt("y", y);
		return compound;
	}
}
