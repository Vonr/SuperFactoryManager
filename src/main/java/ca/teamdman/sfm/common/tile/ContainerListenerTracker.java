package ca.teamdman.sfm.common.tile;

import ca.teamdman.sfm.common.net.PacketHandler;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.PacketDistributor;

public interface ContainerListenerTracker {
	Map<ServerPlayerEntity, Integer> getListeners();

	default <MSG> void sendPacketToListeners(Function<Integer, MSG> packetFunc) {
		getListeners().forEach((player, windowId) -> {
			MSG packet = packetFunc.apply(windowId);
			PacketHandler.INSTANCE.send(
				PacketDistributor.PLAYER.with(()->player),
				packet
			);
		});
	}
}
