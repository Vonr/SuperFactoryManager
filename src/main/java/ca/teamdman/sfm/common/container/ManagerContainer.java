package ca.teamdman.sfm.common.container;

import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.ManagerUpdatePacket;
import ca.teamdman.sfm.common.registrar.ContainerRegistrar;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.network.PacketDispatcher;
import net.minecraftforge.fml.network.PacketDistributor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ManagerContainer extends Container {
	private final ManagerTileEntity TILE;
	final         Field             LISTENERS = ObfuscationReflectionHelper
			.findField(Container.class, "listeners");
	private int tick = 0;

	public ManagerContainer(int windowId, ManagerTileEntity tile) {
		super(ContainerRegistrar.Containers.MANAGER, windowId);
		System.out.printf("Created container on side %s\n", tile.getWorld().isRemote ? "REMOTE" : "SERVER");
		this.TILE = tile;
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}

	@Override
	public void detectAndSendChanges() {
		if (++tick % 100 != 0)
			return;
		try {

			//noinspection unchecked
			List<IContainerListener> listeners = (List<IContainerListener>) LISTENERS.get(this);
			for (IContainerListener v : listeners) {
				if (v instanceof ServerPlayerEntity) {
					PacketHandler.INSTANCE.send(
							PacketDistributor.PLAYER.with(()->(ServerPlayerEntity) v),
							new ManagerUpdatePacket("BEANS")
					);
				}
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
