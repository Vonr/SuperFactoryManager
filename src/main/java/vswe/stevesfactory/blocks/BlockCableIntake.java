package vswe.stevesfactory.blocks;

import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import vswe.stevesfactory.interfaces.IItemBlockProvider;
import vswe.stevesfactory.tiles.TileEntityClusterElement;
import vswe.stevesfactory.tiles.TileEntityIntake;

public class BlockCableIntake extends BlockCableDirectionAdvanced implements IItemBlockProvider {
	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityIntake();
	}

	@Override
	protected Class<? extends TileEntityClusterElement> getTeClass() {
		return TileEntityIntake.class;
	}

	@Override
	public ItemBlock getItem() {
		return new ItemIntake(this);
	}
}
