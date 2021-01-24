/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.container;

import ca.teamdman.sfm.common.container.slot.CraftingOutputSlot;
import ca.teamdman.sfm.common.registrar.ContainerRegistrar;
import ca.teamdman.sfm.common.tile.CrafterTileEntity;
import ca.teamdman.sfm.common.util.SFMUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.items.SlotItemHandler;

public class CrafterContainer extends Container {

	public CrafterContainer(int windowId, PlayerInventory playerInv, CrafterTileEntity tile) {
		super(ContainerRegistrar.CRAFTER.get(), windowId);
		this.addSlot(new CraftingOutputSlot(tile.inventory, 9, 124, 35));
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.addSlot(
					new SlotItemHandler(tile.inventory, j + i * 3, 30 + j * 18, 17 + i * 18));
			}
		}
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}
		for (int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(playerInv, i, 8 + i * 18, 142));
		}
	}

	public static CrafterContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
		return SFMUtil.getClientTile(
			IWorldPosCallable.of(inv.player.world, data.readBlockPos()),
			CrafterTileEntity.class
		)
			.map(crafterTileEntity -> new CrafterContainer(windowId, inv, crafterTileEntity))
			.orElse(null);
	}

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		System.out.println(index);
		return ItemStack.EMPTY;
	}

	@Override
	public void onCraftMatrixChanged(IInventory inventoryIn) {
		super.onCraftMatrixChanged(inventoryIn);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}
}
