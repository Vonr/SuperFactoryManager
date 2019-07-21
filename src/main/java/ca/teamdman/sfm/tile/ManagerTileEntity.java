package ca.teamdman.sfm.tile;

import ca.teamdman.sfm.container.ManagerContainer;
import ca.teamdman.sfm.registrar.TileEntityRegistrar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;

public class ManagerTileEntity extends TileEntity implements INamedContainerProvider {
	public ManagerTileEntity(final TileEntityType<?> type) {
		super(type);
	}

	public ManagerTileEntity() {
		this(TileEntityRegistrar.Tiles.MANAGER);
	}


	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("container.sfm.manager");
	}

	@Nullable
	@Override
	public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
		return new ManagerContainer(windowId, new Inventory(), player);
	}
}
