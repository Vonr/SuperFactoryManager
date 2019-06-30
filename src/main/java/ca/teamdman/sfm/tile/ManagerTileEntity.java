package ca.teamdman.sfm.tile;

import ca.teamdman.sfm.registrar.TileEntityRegistrar;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class ManagerTileEntity extends TileEntity {
	public ManagerTileEntity(final TileEntityType<?> type) {
		super(type);
	}

	public ManagerTileEntity() {
		this(TileEntityRegistrar.Tiles.MANAGER);
	}
}
