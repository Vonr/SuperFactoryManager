package ca.teamdman.sfm.common.net.packet.workstation;

import ca.teamdman.sfm.common.container.WorkstationContainer;
import ca.teamdman.sfm.common.item.CraftingContractItem;
import ca.teamdman.sfm.common.net.packet.C2SContainerPacket;
import ca.teamdman.sfm.common.tile.WorkstationTileEntity;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public final class C2SWorkstationLearnClickPacket extends
	C2SContainerPacket<WorkstationTileEntity, WorkstationContainer> {

	public C2SWorkstationLearnClickPacket(int windowId, BlockPos tilePos) {
		super(
			WorkstationTileEntity.class,
			WorkstationContainer.class,
			windowId,
			tilePos
		);
	}

	public static final class Handler extends
		C2SContainerPacketHandler<WorkstationTileEntity, WorkstationContainer, C2SWorkstationLearnClickPacket> {

		@Override
		public void finishEncode(
			C2SWorkstationLearnClickPacket c2SWorkstationLearnClickPacket,
			PacketBuffer buf
		) {

		}

		@Override
		public C2SWorkstationLearnClickPacket finishDecode(
			int windowId, BlockPos tilePos, PacketBuffer buf
		) {
			return new C2SWorkstationLearnClickPacket(windowId, tilePos);
		}

		@Override
		public void handleDetailed(
			Supplier<Context> ctx,
			C2SWorkstationLearnClickPacket c2SWorkstationLearnClickPacket,
			WorkstationTileEntity workstationTileEntity
		) {
			WorkstationContainer container = (WorkstationContainer) ctx.get().getSender().containerMenu;
			WorkstationTileEntity tile = container.getSource();
			for (
				int slot = 0;
				slot < tile.CONTRACT_INVENTORY.getSlots();
				slot++
			) {
				tile.CONTRACT_INVENTORY.setStackInSlot(slot, ItemStack.EMPTY);
			}
			AtomicInteger slot = new AtomicInteger(0);
			Stream<ICraftingRecipe> oneStepRecipes = container.getOneStepRecipes();
			oneStepRecipes
				.map(CraftingContractItem::withRecipe)
				.forEach(stack -> tile.CONTRACT_INVENTORY.setStackInSlot(
					slot.getAndIncrement(),
					stack
				));
		}
	}
}

