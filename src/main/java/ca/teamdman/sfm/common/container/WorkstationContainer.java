package ca.teamdman.sfm.common.container;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.registrar.SFMContainers;
import ca.teamdman.sfm.common.tile.WorkstationTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraftforge.items.SlotItemHandler;

public class WorkstationContainer extends BaseContainer<WorkstationTileEntity> {

	public WorkstationContainer(
		int windowId,
		WorkstationTileEntity tile,
		PlayerInventory playerInv,
		boolean isRemote,
		String data
	) {
		super(SFMContainers.WORKSTATION.get(), windowId, tile, isRemote);
//		MinecraftForge.EVENT_BUS.register(this);
		SFM.LOGGER.debug(
			"Created container REMOTE={} data={}",
			this.IS_REMOTE,
			data
		);

		for (int row = 0; row < 3; row++) {
			for (int slot = 0; slot < 9; slot++) {
				this.addSlot(new SlotItemHandler(
					tile.INVENTORY,
					slot + row * 9,
					8 + slot * 18,
					18 + row * 18
				));
			}
		}

		// player inventory
		for (int row = 0; row < 3; ++row) {
			for (int slot = 0; slot < 9; ++slot) {
				this.addSlot(new Slot(
					playerInv,
					slot + row * 9 + 9,
					8 + slot * 18,
					85 + row * 18
				));
			}
		}

		// player hotbar
		for (int slot = 0; slot < 9; ++slot) {
			this.addSlot(new Slot(playerInv, slot, 8 + slot * 18, 143));
		}
	}


}
