package ca.teamdman.sfm.common.cablenetwork;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.util.SFMUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
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

    private static final Multimap<ResourceKey<Level>, CableNetwork> NETWORKS = ArrayListMultimap.create();

    public static void printDebugInfo() {
        SFM.LOGGER.debug(SFMUtil.getMarker(CableNetworkManager.class), "{} networks now", size());
    }

    public static int size() {
        return NETWORKS.size();
    }

    /**
     * Remove a block from any networks it is in. Then, prune any empty networks.
     */
    public static void unregister(Level world, BlockPos cablePos) {
        Optional<CableNetwork>
                lookup =
                NETWORKS.get(world.dimension()).stream().filter(network -> network.contains(cablePos)).findFirst();
        if (!lookup.isPresent()) {
            return;
        }
        CableNetwork previous = lookup.get();
        if (previous.size() == 1) {
            // Cable was the last in its network, remove the network
            NETWORKS.remove(world.dimension(), previous);
        } else /*if (previous.size() > 1)*/ {
            // Cable was not the last, and its removal might cause the network to split
            Deque<BlockPos> split = new ArrayDeque<>(previous.split(cablePos));
            while (!split.isEmpty()) {
                // Get a start position for the new network
                BlockPos start = split.pop();

                // Create a new network for it
                // This will snake along the cable and grab neighbours as well
                getOrRegisterNetwork(world, start).ifPresent(network -> {
                    // Remove cables that joined the new network from the queue
                    split.removeAll(network.getCables());
                });
            }
        }
        printDebugInfo();
    }

    public static Optional<CableNetwork> getOrRegisterNetwork(BlockEntity tile) {
        return getOrRegisterNetwork(tile.getLevel(), tile.getBlockPos());
    }

    /**
     * Gets the cable network object. If none exists and one should, it will create and populate
     * one.
     */
    public static Optional<CableNetwork> getOrRegisterNetwork(Level world, BlockPos cablePos) {
        if (!CableNetwork.isValidNetworkMember(world, cablePos)) {
            return Optional.empty();
        }

        Optional<CableNetwork>
                existing =
                NETWORKS
                        .get(world.dimension())
                        .stream()
                        .filter(net -> net.getLevel().isClientSide() == world.isClientSide())
                        .filter(net -> net.contains(cablePos))
                        .findFirst();
        if (existing.isPresent()) {
            // Cable network exists, return it
            return existing;
        } else {
            // No cable network exists

            // Discover candidate networks
            List<CableNetwork>
                    candidates =
                    NETWORKS
                            .get(world.dimension())
                            .stream()
                            .filter(net -> net.getLevel().isClientSide() == world.isClientSide)
                            .filter(net -> net.containsNeighbour(cablePos))
                            .collect(Collectors.toList());

            if (candidates.size() == 0) {
                // No candidates exists for this cable, create a new network
                CableNetwork network = new CableNetwork(world);
                NETWORKS.put(world.dimension(), network);

                // In case network map not built, rebuild now
                network.rebuildNetwork(cablePos);
                printDebugInfo();
                return Optional.of(network);
            } else if (candidates.size() == 1) {
                // Only one network matches this cable, add cable as member
                CableNetwork network = candidates.get(0);
                network.addCable(cablePos);
                printDebugInfo();
                return Optional.of(network);
            } else /*if (candidates.size() > 1)*/ {
                // More than one candidate network exists, creating a join between two networks

                // Keep the first network as the remainder
                CableNetwork network = candidates.get(0);

                // Merge the rest into the first
                candidates.listIterator(1).forEachRemaining(other -> {
                    network.mergeNetwork(other);
                    NETWORKS.remove(world.dimension(), other);
                });

                // Register any inventories that the new cable introduces
                network.addCable(cablePos);
                printDebugInfo();
                return Optional.of(network);
            }
        }
    }

}
