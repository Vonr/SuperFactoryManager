package ca.teamdman.sfm.common.cablenetwork;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;
import java.util.stream.Collectors;

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

    public static void printDebugInfo() {
//        SFM.LOGGER.debug(SFMUtil.getMarker(CableNetworkManager.class), "{} networks now", size());
    }

    /**
     * Remove a block from any networks it is in. Then, prune any empty networks.
     */
    public static void unregister(Level level, BlockPos cablePos) {
        getNetwork(level, cablePos).ifPresent(network -> {
            var branches = network.remove(cablePos);
            // remove old network
            removeNetwork(network);
            // add all branch networks
            branches.forEach(CableNetworkManager::addNetwork);
        });
    }

    public static Optional<CableNetwork> getOrRegisterNetwork(BlockEntity tile) {
        return getOrRegisterNetwork(tile.getLevel(), tile.getBlockPos());
    }

    private static Optional<CableNetwork> getNetwork(Level level, BlockPos pos) {
        return NETWORKS
                .getOrDefault(level, Collections.emptyList())
                .stream()
                .filter(net -> net.getLevel().isClientSide() == level.isClientSide())
                .filter(net -> net.containsCableLocation(pos))
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
        return NETWORKS
                .getOrDefault(level, Collections.emptyList())
                .stream()
                .filter(net -> net.getLevel().isClientSide() == level.isClientSide)
                .filter(net -> net.hasCableNeighbour(pos))
                .collect(Collectors.toSet());
    }

    /**
     * Creates a new cable network and saves it to the cache
     */
    private static Optional<CableNetwork> createAndRegisterNetwork(Level level, BlockPos origin) {
        CableNetwork network = new CableNetwork(level);
        addNetwork(network);
        network.rebuildNetwork(origin);
        return Optional.of(network);
    }


    private static Optional<CableNetwork> mergeNetworks(Set<CableNetwork> networks) {
        if (networks.isEmpty()) return Optional.empty();

        Iterator<CableNetwork> iterator = networks.iterator();
        CableNetwork           main     = iterator.next();

        // Merge the rest into the first
        iterator.forEachRemaining(other -> {
            main.mergeNetwork(other);
            removeNetwork(other);
        });

        return Optional.of(main);
    }

    /**
     * Gets the cable network object. If none exists and one should, it will create and populate
     * one.
     */
    public static Optional<CableNetwork> getOrRegisterNetwork(Level level, BlockPos pos) {
        if (level == null) return Optional.empty();
        // only cables define the main spine of a network
        if (!CableNetwork.isCable(level, pos)) return Optional.empty();

        // discover existing network for this position
        Optional<CableNetwork> existing = getNetwork(level, pos);
        if (existing.isPresent()) return existing;

        // find potential networks
        Set<CableNetwork> candidates = getCandidateNetworks(level, pos);

        // no candidates, create new network
        if (candidates.isEmpty()) return createAndRegisterNetwork(level, pos);

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

}
