package ca.teamdman.sfm.container;

import ca.teamdman.sfm.registrar.ContainerRegistrar;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;

public class ManagerContainer extends Container {

	public ManagerContainer(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
		this(windowId, new Inventory(), null);
	}

	public ManagerContainer(int windowId, Inventory inv, PlayerEntity player) {
		super(ContainerRegistrar.Containers.MANAGER, windowId);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}
}
