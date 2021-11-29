package ca.teamdman.sfm.common.cablenetwork;

import ca.teamdman.sfm.common.util.SFMUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CableNetwork {

    private final Level                      LEVEL;
    private final Set<BlockPos>              CABLES      = new HashSet<>();
    private final Map<BlockPos, BlockEntity> INVENTORIES = new HashMap<>();

    public CableNetwork(Level level) {
        this.LEVEL = level;
    }

    /**
     * Only cable blocks are valid network members
     */
    public static boolean isValidNetworkMember(Level world, BlockPos cablePos) {
        return world.getBlockState(cablePos).getBlock() instanceof ICable;
    }

    public void rebuildNetwork(BlockPos pos) {
        CABLES.clear();
        INVENTORIES.clear();
        discoverCables(pos).forEach(this::addCable);
    }

    public Stream<BlockPos> discoverCables(BlockPos startPos) {
        return SFMUtil.getRecursiveStream((current, next, results) -> {
            results.accept(current);
            for (Direction d : Direction.values()) {
                BlockPos offset = current.offset(d.getNormal());
                if (isValidNetworkMember(getLevel(), offset)) {
                    next.accept(offset);
                }
            }
        }, startPos);
    }

    public boolean addCable(BlockPos pos) {
        boolean isNewMember = CABLES.add(pos);
        if (isNewMember) {
            rebuildAdjacentInventories(pos);
        }
        return isNewMember;
    }

    public Level getLevel() {
        return LEVEL;
    }

    public void rebuildAdjacentInventories(BlockPos pos) {
        Arrays
                .stream(Direction.values())
                .map(Direction::getNormal)
                .map(pos::offset)
                .distinct()
                .peek(INVENTORIES::remove)
                .filter(this::containsNeighbour) // Verify if should [re]join network
                .map(LEVEL::getBlockEntity)
                .filter(Objects::nonNull)
                .forEach(tile -> INVENTORIES.put(tile.getBlockPos(), tile)); // register tile [again]
    }

    /**
     * Cables should only join the network if they would be touching a cable already in the network
     *
     * @param pos Candidate cable position
     * @return {@code true} if adjacent to cable in network
     */
    public boolean containsNeighbour(BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (CABLES.contains(pos.offset(direction.getNormal()))) {
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
            BlockPos p = pos.offset(direction.getNormal());
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
                    BlockPos off = current.offset(direction.getNormal());
                    if (!off.equals(pos) && contains(off)) {
                        next.accept(off);
                    }
                }
            }, start).collect(Collectors.toSet());

            Set<BlockPos> remove = CABLES.stream().filter(p -> !retain.contains(p)).collect(Collectors.toSet());
            remove.forEach(this::removeCable);
            remove.remove(pos);
            return remove;
        }
    }

    public boolean contains(BlockPos pos) {
        return CABLES.contains(pos);
    }

    public boolean removeCable(BlockPos pos) {
        boolean wasMember = CABLES.remove(pos);
        if (wasMember) {
            rebuildAdjacentInventories(pos);
        }
        return wasMember;
    }

    public Optional<BlockEntity> getInventory(BlockPos pos) {
        return Optional.ofNullable(INVENTORIES.get(pos));
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

    public Collection<BlockEntity> getInventories() {
        return INVENTORIES.values();
    }

    public ItemStack getPreview(BlockPos pos) {
        return new ItemStack(LEVEL.getBlockState(pos).getBlock().asItem());
    }

    public Set<BlockPos> getCables() {
        return CABLES;
    }
}
