package ca.teamdman.sfm.common.cablenetwork;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.fml.common.Mod;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Helper class to memorize the relevant chains of inventory cables.
 * <p>
 * Rather than looking up the connected cable blocks for each manager each tick,
 * this class aims to keep track of the chains instead.
 * Adding or removing cable blocks that invoke the relevant methods for this class
 * will help build the network.
 * <p>
 * Adding cables can do one of:
 * - append to existing network
 * - cause two existing networks to join
 * - create a new network
 * <p>
 * Removing cables can:
 * - Remove it from the network
 * - Remove the network if it was the only member
 * - Cause a network to split into other networks if it was a "bridge" block
 */
public class CableNetworkManager {

    private static final Map<Level, Long2ObjectMap<CableNetwork>> NETWORKS_BY_CABLE_POSITION = new WeakHashMap<>();
    private static final Map<Level, List<CableNetwork>> NETWORKS_BY_LEVEL = new WeakHashMap<>();

    public static Optional<CableNetwork> getOrRegisterNetworkFromManagerPosition(ManagerBlockEntity tile) {
        //noinspection DataFlowIssue
        return getOrRegisterNetworkFromCablePosition(tile.getLevel(), tile.getBlockPos());
    }

    public static Stream<CableNetwork> getNetworksForLevel(Level level) {
        if (level.isClientSide()) return Stream.empty();
        return NETWORKS_BY_LEVEL
                .getOrDefault(level, Collections.emptyList())
                .stream();
    }

    public static Stream<CableNetwork> getNetworksInRange(Level level, BlockPos pos, double maxDistance) {
        if (level.isClientSide()) return Stream.empty();
        return getNetworksForLevel(level)
                .filter(net -> net
                        .getCablePositions()
                        .anyMatch(cablePos -> cablePos.distSqr(pos) < maxDistance * maxDistance));
    }

    public static void unregisterNetworkForTestingPurposes(CableNetwork network) {
        removeNetwork(network);
    }

    public static void onCablePlaced(Level level, BlockPos pos) {
        if (level.isClientSide()) return;
        getOrRegisterNetworkFromCablePosition(level, pos);
    }

    public static void onCableRemoved(Level level, BlockPos cablePos) {
        getNetworkFromCablePosition(level, cablePos).ifPresent(network -> {
            // Unregister the original network
            removeNetwork(network);
            // Register networks that result from the removal of the cable, if any
            var remainingNetworks = network.withoutCable(cablePos);
            remainingNetworks.forEach(CableNetworkManager::addNetwork);
        });
    }

    public static void purgeCableNetworkForManager(ManagerBlockEntity manager) {
        //noinspection DataFlowIssue
        getNetworkFromCablePosition(
                manager.getLevel(),
                manager.getBlockPos()
        ).ifPresent(CableNetworkManager::removeNetwork);
    }

    /**
     * Gets the cable network object. If none exists and one should, it will create and populate
     * one.
     * <p>
     * Networks should only exist on the server side.
     */
    public static Optional<CableNetwork> getOrRegisterNetworkFromCablePosition(Level level, BlockPos pos) {
        if (level.isClientSide()) return Optional.empty();

        // discover existing network for this position
        Optional<CableNetwork> existing = getNetworkFromCablePosition(level, pos);
        if (existing.isPresent()) return existing;

        // only cables define the main spine of a network
        if (!CableNetwork.isCable(level, pos)) return Optional.empty();

        // find potential networks
        Set<CableNetwork> candidates = getNetworksFromCableAdjacentPosition(level, pos);

        // no candidates, create new network
        if (candidates.isEmpty()) {
            CableNetwork network = new CableNetwork(level);
            // rebuild network from world
            // might be first time used after loading from disk
            network.rebuildNetwork(pos);
            addNetwork(network);
            return Optional.of(network);
        }

        // one candidate exists, add the cable to it
        if (candidates.size() == 1) {
            // Only one network matches this cable, add cable as member
            CableNetwork network = candidates.iterator().next();
            network.addCable(pos);
            NETWORKS_BY_CABLE_POSITION.get(level).put(pos.asLong(), network);
            return Optional.of(network);
        }

        // more than one candidate network exists, merge them
        Optional<CableNetwork> result = mergeNetworks(candidates);
        result.ifPresent(net -> net.addCable(pos));
        return result;
    }

    public static List<BlockPos> getBadCableCachePositions(Level level) {
        return getNetworksForLevel(level)
                .flatMap(CableNetwork::getCablePositions)
                .filter(pos -> !(level.getBlockState(pos).getBlock() instanceof ICableBlock))
                .collect(Collectors.toList());
    }

    public static void clear() {
        NETWORKS_BY_LEVEL.clear();
    }

    private static Optional<CableNetwork> getNetworkFromCablePosition(Level level, BlockPos pos) {
        return Optional.ofNullable(NETWORKS_BY_CABLE_POSITION
                                           .computeIfAbsent(level, k -> new Long2ObjectOpenHashMap<>())
                                           .get(pos.asLong()));
    }

    private static void removeNetwork(CableNetwork network) {
        // Unregister network from level lookup
        NETWORKS_BY_LEVEL.getOrDefault(network.getLevel(), Collections.emptyList()).remove(network);

        // Unregister network from cable position lookup
        Long2ObjectMap<CableNetwork> posMap = NETWORKS_BY_CABLE_POSITION
                .computeIfAbsent(network.getLevel(), k -> new Long2ObjectOpenHashMap<>());
        network.getCablePositionsRaw().forEach(posMap::remove);
    }

    private static void addNetwork(CableNetwork network) {
        // Register network to level lookup
        NETWORKS_BY_LEVEL.computeIfAbsent(network.getLevel(), k -> new ArrayList<>()).add(network);

        // Register network to cable position lookup
        Long2ObjectMap<CableNetwork> posMap = NETWORKS_BY_CABLE_POSITION
                .computeIfAbsent(network.getLevel(), k -> new Long2ObjectOpenHashMap<>());
        network.getCablePositionsRaw().forEach(cablePos -> posMap.put(cablePos, network));
    }

    /**
     * Finds the set of networks that contain the given position
     */
    private static Set<CableNetwork> getNetworksFromCableAdjacentPosition(Level level, BlockPos pos) {
        Set<CableNetwork> rtn = new HashSet<>();
        for (Direction direction : Direction.values()) {
            BlockPos offset = pos.relative(direction);
            Optional<CableNetwork> network = getNetworkFromCablePosition(level, offset);
            network.ifPresent(rtn::add);
        }
        return rtn;
    }

    private static Optional<CableNetwork> mergeNetworks(Set<CableNetwork> networks) {
        if (networks.isEmpty()) return Optional.empty();

        Iterator<CableNetwork> iterator = networks.iterator();
        // The first network will absorb the others
        CableNetwork main = iterator.next();

        Level level = main.getLevel();
        var levelMap = NETWORKS_BY_LEVEL.get(level);

        // Merge the rest into the first
        iterator.forEachRemaining(other -> {
            main.mergeNetwork(other);
            levelMap.remove(other); // unregister the network
        });
        // the main network now contains all the cable positions of the others
        // when we addNetwork here, it _should_ clobber all the old entries to point to this network instead
        addNetwork(main);
        return Optional.of(main);
    }
}
