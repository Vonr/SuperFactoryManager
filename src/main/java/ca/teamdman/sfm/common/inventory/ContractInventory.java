package ca.teamdman.sfm.common.inventory;

import ca.teamdman.sfm.common.item.CraftingContractItem;
import ca.teamdman.sfm.common.tile.WorkstationTileEntity;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ContractInventory extends ItemStackHandler {

	private final WorkstationTileEntity workstationTileEntity;

	public ContractInventory(
		WorkstationTileEntity workstationTileEntity
	) {
		super(9 * 5);
		this.workstationTileEntity = workstationTileEntity;
	}

	@Override
	public boolean isItemValid(
		int slot, @Nonnull ItemStack stack
	) {
		return stack.getItem() instanceof CraftingContractItem;
	}

	@Override
	protected void onContentsChanged(int slot) {
		workstationTileEntity.setChanged();
	}
}
