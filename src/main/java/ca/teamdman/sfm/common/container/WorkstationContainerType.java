package ca.teamdman.sfm.common.container;

import ca.teamdman.sfm.common.tile.WorkstationTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;

public class WorkstationContainerType extends
	TileContainerType<WorkstationContainer, WorkstationTileEntity> {

	public WorkstationContainerType() {
		super(WorkstationTileEntity.class);
	}

	@Override
	public WorkstationContainer createServerContainer(
		int windowId, WorkstationTileEntity tile, ServerPlayerEntity player
	) {
		return new WorkstationContainer(windowId, tile, player.inventory,false, "SERVER");
	}

	@Override
	protected WorkstationContainer createClientContainer(
		int windowId,
		WorkstationTileEntity tile,
		PlayerInventory playerInv,
		PacketBuffer buffer
	) {
		return new WorkstationContainer(windowId, tile, playerInv,true, "CLIENT");
	}

	@Override
	protected void prepareClientContainer(
		WorkstationTileEntity tile, PacketBuffer buffer
	) {

	}
}
