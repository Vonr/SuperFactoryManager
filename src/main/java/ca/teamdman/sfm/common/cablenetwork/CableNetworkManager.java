package ca.teamdman.sfm.common.cablenetwork;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.fml.common.Mod;

import javax.annotation.Nullable;
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

    private static final Map<Level, List<CableNetwork>> NETWORKS = new WeakHashMap<>();

    /**
     * Remove a block from any networks it is in. Then, prune any empty networks.
     */
    public static void removeCable(Level level, BlockPos cablePos) {
        getNetworkFromCablePosition(level, cablePos).ifPresent(network -> {
            removeNetwork(network);
            var newNetworks = network.withoutCable(cablePos);
            newNetworks.forEach(CableNetworkManager::addNetwork);
        });
    }

    public static Optional<CableNetwork> getOrRegisterNetworkFromManagerPosition(ManagerBlockEntity tile) {
        return getOrRegisterNetworkFromCablePosition(tile.getLevel(), tile.getBlockPos());
    }

    public static Optional<CableNetwork> getNetworkFromPosition(Level level, BlockPos pos) {
        return getNetworksForLevel(level)
                .filter(net -> net.CABLE_POSITIONS.contains(pos.asLong())
                               || net.CAPABILITY_PROVIDER_POSITIONS.containsKey(pos.asLong()))
                .findFirst();
    }

    public static Stream<CableNetwork> getNetworksForLevel(Level level) {
        return NETWORKS.getOrDefault(level, Collections.emptyList())
                .stream()
                .filter(net -> net.getLevel().isClientSide() == level.isClientSide());
    }

    private static Optional<CableNetwork> getNetworkFromCablePosition(Level level, BlockPos pos) {
        return getNetworksForLevel(level)
                .filter(net -> net.containsCablePosition(pos))
                .findFirst();
    }

    private static void removeNetwork(CableNetwork network) {
        NETWORKS.getOrDefault(network.getLevel(), Collections.emptyList()).remove(network);
    }

    private static void addNetwork(CableNetwork network) {
        NETWORKS.computeIfAbsent(network.getLevel(), k -> new ArrayList<>()).add(network);
    }

    /**
     * Finds the set of networks that contain the given position
     */
    private static Set<CableNetwork> getCandidateNetworks(Level level, BlockPos pos) {
        return getNetworksForLevel(level)
                .filter(net -> net.isAdjacentToCable(pos))
                .collect(Collectors.toSet());
    }


    private static Optional<CableNetwork> mergeNetworks(Set<CableNetwork> networks) {
        if (networks.isEmpty()) return Optional.empty();

        Iterator<CableNetwork> iterator = networks.iterator();
        CableNetwork main = iterator.next();

        // Merge the rest into the first
        iterator.forEachRemaining(other -> {
            main.mergeNetwork(other);
            removeNetwork(other);
        });

        return Optional.of(main);
    }

    public static void unregisterNetworkForTestingPurposes(CableNetwork network) {
        removeNetwork(network);
    }

    /**
     * Gets the cable network object. If none exists and one should, it will create and populate
     * one.
     * <p>
     * Networks should only exist on the server side.
     */
    public static Optional<CableNetwork> getOrRegisterNetworkFromCablePosition(@Nullable Level level, BlockPos pos) {
        if (level == null) return Optional.empty();
        if (level.isClientSide()) return Optional.empty();

        // only cables define the main spine of a network
        if (!CableNetwork.isCable(level, pos)) return Optional.empty();

        // discover existing network for this position
        Optional<CableNetwork> existing = getNetworkFromCablePosition(level, pos);
        if (existing.isPresent()) return existing;

        // find potential networks
        Set<CableNetwork> candidates = getCandidateNetworks(level, pos);

        // no candidates, create new network
        if (candidates.isEmpty()) {
            CableNetwork network = new CableNetwork(level);
            addNetwork(network);
            // rebuild network from world
            // might be first time used after loading from disk
            network.rebuildNetwork(pos);
            return Optional.of(network);
        }

        // one candidate exists, add the cable to it
        if (candidates.size() == 1) {
            // Only one network matches this cable, add cable as member
            CableNetwork network = candidates.iterator().next();
            network.addCable(pos);
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
        NETWORKS.clear();
    }
}
