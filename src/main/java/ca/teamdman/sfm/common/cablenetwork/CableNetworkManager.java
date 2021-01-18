package ca.teamdman.sfm.common.cablenetwork;

import ca.teamdman.sfm.common.block.ICable;
import ca.teamdman.sfm.common.util.SFMUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.util.Direction;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CableNetworkManager {

	private static final Multimap<RegistryKey<World>, CableNetwork> NETWORKS = ArrayListMultimap
		.create();


	public static int size() {
		return NETWORKS.size();
	}

	/**
	 * Remove a block from any networks it is in. Then, prune any empty networks.
	 */
	public static void unregister(World world, BlockPos cablePos) {
		Optional<CableNetwork> lookup = NETWORKS.get(world.getDimensionKey()).stream()
			.filter(network -> network.contains(cablePos))
			.findFirst();
		if (!lookup.isPresent()) {
			return;
		}
		CableNetwork previous = lookup.get();
		if (previous.size() == 1) {
			// Cable was the last in its network, remove the network
			NETWORKS.remove(world.getDimensionKey(), previous);
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
	}

	/**
	 * Gets the cable network object. If none exists and one should, it will create and populate
	 * one.
	 */
	public static Optional<CableNetwork> getOrRegisterNetwork(World world, BlockPos cablePos) {
		if (!isValidNetworkMember(world, cablePos)) {
			return Optional.empty();
		}

		Optional<CableNetwork> existing = NETWORKS.get(world.getDimensionKey()).stream()
			.filter(net -> net.contains(cablePos))
			.findFirst();
		if (existing.isPresent()) {
			// Cable network exists, return it
			return existing;
		} else {
			// No cable network exists

			// Discover candidate networks
			List<CableNetwork> candidates = NETWORKS.get(world.getDimensionKey()).stream()
				.filter(network -> network.containsNeighbour(cablePos))
				.collect(Collectors.toList());

			if (candidates.size() == 0) {
				// No candidates exists for this cable, create a new network
				CableNetwork network = new CableNetwork(world);
				NETWORKS.put(world.getDimensionKey(), network);

				// In case network map not built, rebuild now
				discoverCables(world, cablePos).forEach(network::addCable);
				return Optional.of(network);
			} else if (candidates.size() == 1) {
				// Only one network matches this cable, add cable as member
				CableNetwork network = candidates.get(0);
				network.addCable(cablePos);
				return Optional.of(network);
			} else /*if (candidates.size() > 1)*/ {
				// More than one candidate network exists, creating a join between two networks

				// Keep the first network as the remainder
				CableNetwork network = candidates.get(0);

				// Merge the rest into the first
				candidates.listIterator(1).forEachRemaining(other -> {
					network.mergeNetwork(other);
					NETWORKS.remove(world.getDimensionKey(), other);
				});

				// Register any inventories that the new cable introduces
				network.addCable(cablePos);
				return Optional.of(network);
			}
		}
	}

	/**
	 * Only cable blocks are valid network members
	 */
	private static boolean isValidNetworkMember(World world, BlockPos cablePos) {
		return world.getBlockState(cablePos).getBlock() instanceof ICable;
	}

	/**
	 * @param world
	 * @param startPos
	 * @return
	 */
	public static Stream<BlockPos> discoverCables(World world, BlockPos startPos) {
		return SFMUtil.getRecursiveStream((current, next, results) -> {
			results.accept(current);
			for (Direction d : Direction.values()) {
				BlockPos offset = current.offset(d);
				if (isValidNetworkMember(world, offset)) {
					next.accept(offset);
				}
			}
		}, startPos);
	}
}
