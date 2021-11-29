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

    private CableNetwork(Level level, Collection<BlockPos> init) {
        this(level);
        CABLES.addAll(init);
    }

    /**
     * Only cable blocks are valid network members
     */
    public static boolean isValidNetworkMember(Level world, BlockPos cablePos) {
        return world
                .getBlockState(cablePos)
                .getBlock() instanceof ICable;
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
     * Discover connected cables using only known cable positions.
     * Used during network fragmentation.
     */
    private Set<BlockPos> discoverKnownCables(BlockPos start) {
        return SFMUtil
                .getRecursiveStream((current, next, results) -> {
                    if (!contains(current)) return;
                    results.accept(current);
                    for (Direction direction : Direction.values()) {
                        BlockPos offset = current.offset(direction.getNormal());
                        next.accept(offset);
                    }
                }, start)
                .collect(Collectors.toSet());
    }

    /**
     * Takes the pos out of the network, and returns the networks that result.
     * <p>
     * Removing a position may cause the network to split in two, and will return both networks.
     *
     * @param pos Cable bridge position
     * @return List of positions that need to become new networks
     */
    public Set<CableNetwork> remove(BlockPos pos) {
        CABLES.remove(pos);
        if (isEmpty()) return Collections.emptySet();

        // Discover branches
        Set<CableNetwork> networks = new HashSet<>();
        for (var direction : Direction.values()) {
            var offset = pos.offset(direction.getNormal());
            var branch = discoverKnownCables(offset);
            if (!branch.isEmpty()) {
                var network = getDerivativeNetwork(branch);
                networks.add(network);
            }
        }

        return networks;
    }

    /**
     * Creates a new network using the given positions and already known inventories
     */
    private CableNetwork getDerivativeNetwork(Set<BlockPos> positions) {
        var network = new CableNetwork(getLevel(), positions);

        // get all cable neighbours
        Set<BlockPos> validInvPositions = positions
                .stream()

                .flatMap(pos -> Arrays
                        .stream(Direction.values())
                        .map(Direction::getNormal)
                        .map(pos::offset))
                .collect(Collectors.toSet());

        // get all inventories occupying a neighbour spot
        // add them to the new network
        INVENTORIES
                .entrySet()
                .stream()
                .filter(entry -> validInvPositions.contains(entry.getKey()))
                .forEach(entry -> network.INVENTORIES.put(entry.getKey(), entry.getValue()));

        return network;
    }

    public boolean contains(BlockPos pos) {
        return CABLES.contains(pos);
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
        return new ItemStack(LEVEL
                                     .getBlockState(pos)
                                     .getBlock()
                                     .asItem());
    }

    public Set<BlockPos> getCables() {
        return CABLES;
    }
}
