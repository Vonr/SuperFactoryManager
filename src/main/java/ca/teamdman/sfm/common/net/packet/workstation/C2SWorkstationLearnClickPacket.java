package ca.teamdman.sfm.common.net.packet.workstation;

import ca.teamdman.sfm.common.container.WorkstationContainer;
import ca.teamdman.sfm.common.item.CraftingContractItem;
import ca.teamdman.sfm.common.tile.WorkstationTileEntity;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class C2SWorkstationLearnClickPacket {

	public final int WINDOW_ID;

	public C2SWorkstationLearnClickPacket(int windowId) {
		this.WINDOW_ID = windowId;
	}

	public static void encode(
		C2SWorkstationLearnClickPacket msg,
		PacketBuffer packetBuffer
	) {
		packetBuffer.writeInt(msg.WINDOW_ID);
	}

	public static C2SWorkstationLearnClickPacket decode(PacketBuffer packetBuffer) {
		return new C2SWorkstationLearnClickPacket(packetBuffer.readInt());
	}

	public static void handle(
		C2SWorkstationLearnClickPacket msg,
		Supplier<Context> contextSupplier
	) {
		contextSupplier.get().enqueueWork(() -> {
			ServerPlayerEntity sender = contextSupplier.get().getSender();
			if (sender == null) return;
			if (sender.openContainer == null) return;
			if (sender.openContainer.windowId != msg.WINDOW_ID) return;
			if (!(sender.openContainer instanceof WorkstationContainer)) {
				return;
			}
			WorkstationContainer container = (WorkstationContainer) sender.openContainer;
			WorkstationTileEntity tile = container.getSource();
			for (int slot = 0; slot < tile.INVENTORY.getSlots(); slot++) {
				tile.INVENTORY.setStackInSlot(slot, ItemStack.EMPTY);
			}
			AtomicInteger slot = new AtomicInteger(0);
			Stream<ICraftingRecipe> oneStepRecipes = container.getOneStepRecipes();
			oneStepRecipes
				.map(CraftingContractItem::withRecipe)
				.forEach(stack -> tile.INVENTORY.setStackInSlot(
					slot.getAndIncrement(),
					stack
				));
		});
	}
}
