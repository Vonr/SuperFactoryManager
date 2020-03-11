package ca.teamdman.sfm.common.tile;

import ca.teamdman.sfm.common.container.CrafterContainer;
import ca.teamdman.sfm.common.registrar.TileEntityRegistrar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class CrafterTileEntity extends TileEntity {
	public final IItemHandler         craftingInv    = new ItemStackHandler(9);
	private final CraftResultInventory craftResultInv = new CraftResultInventory();

	public CrafterTileEntity() {
		this(TileEntityRegistrar.Tiles.CRAFTER);
	}

	public CrafterTileEntity(final TileEntityType<?> type) {
		super(type);
	}
}
