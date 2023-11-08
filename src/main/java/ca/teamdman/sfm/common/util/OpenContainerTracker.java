package ca.teamdman.sfm.common.util;

import ca.teamdman.sfm.SFM;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = SFM.MOD_ID)
public class OpenContainerTracker {
    private static final Map<ServerPlayer, AbstractContainerMenu> OPEN_CONTAINERS = new WeakHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends AbstractContainerMenu> Stream<Map.Entry<ServerPlayer, T>> getPlayersWithOpenContainer(Class<T> menuClass) {
        return OPEN_CONTAINERS.entrySet().stream()
                .filter(e -> menuClass.isInstance(e.getValue()))
                .map(e -> (Map.Entry<ServerPlayer, T>) e);
    }

    @SubscribeEvent
    public static void onOpenContainer(PlayerContainerEvent.Open event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            OPEN_CONTAINERS.put(serverPlayer, event.getContainer());
        }
    }

    @SubscribeEvent
    public static void onCloseContainer(PlayerContainerEvent.Close event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            OPEN_CONTAINERS.remove(serverPlayer);
        }
    }
}
