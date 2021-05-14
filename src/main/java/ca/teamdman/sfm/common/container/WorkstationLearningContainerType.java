package ca.teamdman.sfm.common.container;

import ca.teamdman.sfm.common.tile.WorkstationTileEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class WorkstationLearningContainerType extends
	TileContainerType<WorkstationLearningContainer, WorkstationTileEntity> {

	public WorkstationLearningContainerType() {
		super(WorkstationTileEntity.class);
	}

	@Override
	public WorkstationLearningContainer createServerContainer(
		int windowId, WorkstationTileEntity tile, ServerPlayerEntity player
	) {
		return new WorkstationLearningContainer(windowId, tile, player.inventory,false, "SERVER");
	}

	@Override
	protected WorkstationLearningContainer createClientContainer(
		int windowId,
		WorkstationTileEntity tile,
		PlayerInventory playerInv,
		PacketBuffer buffer
	) {
		return new WorkstationLearningContainer(windowId, tile, playerInv,true, "CLIENT");
	}

	@Override
	public ITextComponent getDisplayName() {
 		return new TranslationTextComponent("container.sfm.workstation_learning");
	}

	@Override
	protected void prepareClientContainer(
		WorkstationTileEntity tile, PacketBuffer buffer
	) {

	}
}
