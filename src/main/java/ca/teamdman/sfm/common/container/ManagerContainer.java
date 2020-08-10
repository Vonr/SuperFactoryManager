package ca.teamdman.sfm.common.container;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.registrar.ContainerRegistrar;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;


public class ManagerContainer extends
	BaseContainer<ManagerTileEntity> {

	public final Marker MARKER = MarkerManager.getMarker(getClass().getSimpleName());

	public ManagerContainer(int windowId, ManagerTileEntity tile, boolean isRemote) {
		super(ContainerRegistrar.Containers.MANAGER, windowId, tile, isRemote);
		SFM.LOGGER.debug(MARKER, "Creating container on {} side", isRemote ? "client" : "server");
		MinecraftForge.EVENT_BUS.register(this);
	}

	public static void writeData(ManagerTileEntity tile, PacketBuffer data) {
		data.writeCompoundTag(tile.serializeNBT());
	}

	public void readData(PacketBuffer data) {
		getSource().deserializeNBT(data.readCompoundTag());
	}

	@SubscribeEvent
	public void onContainerOpen(PlayerContainerEvent.Open e) {
		if (!IS_REMOTE) {
			getSource().addContainerListener((ServerPlayerEntity) e.getPlayer());
		}
	}

	@SubscribeEvent
	public void onContainerClose(PlayerContainerEvent.Close e) {
		if (!IS_REMOTE) {
			getSource().removeContainerListener(((ServerPlayerEntity) e.getPlayer()));
		}
	}
}
