package ca.teamdman.sfm.common.cablenetwork;

import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CableNetwork {

	private final World WORLD;
	private final Set<BlockPos> CABLES = new HashSet<>();
	private final Map<BlockPos, TileEntity> INVENTORIES = new HashMap<>();

	public CableNetwork(World world) {
		this.WORLD = world;
	}

	public boolean addCable(BlockPos pos) {
		boolean isNewMember = CABLES.add(pos);
		if (isNewMember) {
			rebuildInventories(pos);
		}
		return isNewMember;
	}


	public void rebuildInventories(BlockPos pos) {
		Arrays.stream(Direction.values())
			.map(pos::offset)
			.distinct()
			.peek(INVENTORIES::remove) // Remove tile if present
			.filter(this::containsNeighbour) // Verify if should [re]join network
			.map(WORLD::getTileEntity)
			.filter(Objects::nonNull)
			.forEach(tile -> INVENTORIES.put(tile.getPos(), tile)); // register tile [again]
	}

	/**
	 * Cables should only join the network if they would be touching a cable already in the network
	 *
	 * @param pos Candidate cable position
	 * @return {@code true} if adjacent to cable in network
	 */
	public boolean containsNeighbour(BlockPos pos) {
		for (Direction direction : Direction.values()) {
			if (CABLES.contains(pos.offset(direction))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * If the position is a bridge, splits the network and returns the part that is removed Assumes
	 * the bridge position is still a member
	 *
	 * @param pos Cable bridge position
	 * @return List of positions that need to become new networks
	 */
	public Set<BlockPos> split(BlockPos pos) {
		// Discover an adjacent cable that's part of the network
		BlockPos start = null;
		for (Direction direction : Direction.values()) {
			BlockPos p = pos.offset(direction);
			if (contains(p)) {
				start = p;
				break;
			}
		}

		if (start == null) {
			// No cable exists, not a bridge since not valid network member
			return Collections.emptySet();
		} else {
			// Discover cable chain branching from the starting neighbour
			Set<BlockPos> retain = SFMUtil.getRecursiveStream((current, next, results) -> {
				results.accept(current);
				for (Direction direction : Direction.values()) {
					BlockPos off = current.offset(direction);
					if (!off.equals(pos) && contains(off)) {
						next.accept(off);
					}
				}
			}, start).collect(Collectors.toSet());

			Set<BlockPos> remove = CABLES.stream()
				.filter(p -> !retain.contains(p))
				.collect(Collectors.toSet());
			remove.forEach(this::removeCable);
			remove.remove(pos);
			return remove;
		}
	}

	public boolean contains(BlockPos pos) {
		return CABLES.contains(pos);
	}

	public Optional<TileEntity> getInventory(BlockPos pos) {
		return Optional.ofNullable(INVENTORIES.get(pos));
	}

	public boolean removeCable(BlockPos pos) {
		boolean wasMember = CABLES.remove(pos);
		if (wasMember) {
			rebuildInventories(pos);
		}
		return wasMember;
	}

	public int size() {
		return CABLES.size();
	}

	/**
	 * Merges a network into this one, such as when a cable connects two networks
	 *
	 * @param other Foreign network
	 */
	public void mergeNetwork(CableNetwork other) {
		CABLES.addAll(other.CABLES);
		INVENTORIES.putAll(other.INVENTORIES);
	}

	public boolean isEmpty() {
		return CABLES.isEmpty();
	}

	public Collection<TileEntity> getInventories() {
		return INVENTORIES.values();
	}

	public ItemStack getPreview(BlockPos pos) {
		return new ItemStack(WORLD.getBlockState(pos).getBlock().asItem());
	}

	public Set<BlockPos> getCables() {
		return CABLES;
	}
}
