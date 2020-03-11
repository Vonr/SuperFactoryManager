package ca.teamdman.sfm.common.tile;

import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.registrar.TileEntityRegistrar;
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
	public ManagerTileEntity() {
		this(TileEntityRegistrar.Tiles.MANAGER);
	}

	public ManagerTileEntity(final TileEntityType<?> type) {
		super(type);
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
