package ca.teamdman.sfm.common.util;

import ca.teamdman.sfm.SFM;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Stream;

import static net.minecraftforge.event.entity.player.PlayerContainerEvent.Close;
import static net.minecraftforge.event.entity.player.PlayerContainerEvent.Open;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = SFM.MOD_ID)
public class OpenContainerTracker {
    private static final Map<ServerPlayer, AbstractContainerMenu> OPEN_CONTAINERS = new WeakHashMap<>();

    public static <T extends AbstractContainerMenu> Stream<Map.Entry<ServerPlayer, T>> getPlayersWithOpenContainer(Class<T> menuClass) {
        return OPEN_CONTAINERS.entrySet().stream()
                .filter(e -> menuClass.isInstance(e.getValue()))
                .map(e -> (Map.Entry<ServerPlayer, T>) e);
    }

    @SubscribeEvent
    public static void onOpenContainer(Open event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            OPEN_CONTAINERS.put(serverPlayer, event.getContainer());
        }
    }

    @SubscribeEvent
    public static void onCloseContainer(Close event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            OPEN_CONTAINERS.remove(serverPlayer);
        }
    }
}
