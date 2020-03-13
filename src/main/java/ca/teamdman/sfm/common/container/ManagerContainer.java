package ca.teamdman.sfm.common.container;

import ca.teamdman.sfm.common.registrar.ContainerRegistrar;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;

public class ManagerContainer extends Container {
	public ManagerContainer(int windowId) {
		super(ContainerRegistrar.Containers.MANAGER, windowId);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}
}
