package ca.teamdman.sfm.common.cablenetwork;

import ca.teamdman.sfm.common.registry.SFMCapabilityProviderMappers;
import ca.teamdman.sfm.common.util.SFMUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CableNetwork {

    private final Level                              LEVEL;
    private final Set<BlockPos>                      CABLES               = new HashSet<>();
    private final Map<BlockPos, ICapabilityProvider> CAPABILITY_PROVIDERS = new HashMap<>();

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
    public static boolean isCable(Level world, BlockPos cablePos) {
        if (world == null) return false;
        return world
                .getBlockState(cablePos)
                .getBlock() instanceof ICable;
    }

    public void rebuildNetwork(BlockPos pos) {
        CABLES.clear();
        CAPABILITY_PROVIDERS.clear();
        discoverCables(pos).forEach(this::addCable);
    }

    public Stream<BlockPos> discoverCables(BlockPos startPos) {
        return SFMUtil.getRecursiveStream((current, next, results) -> {
            results.accept(current);
            for (Direction d : Direction.values()) {
                BlockPos offset = current.offset(d.getNormal());
                if (isCable(getLevel(), offset)) {
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

    /**
     * Collects the capability providers of blocks neighbouring the cable
     *
     * @param cablePos position of the cable
     */
    public void rebuildAdjacentInventories(BlockPos cablePos) {
        Arrays
                .stream(Direction.values())
                .map(Direction::getNormal)
                .map(cablePos::offset)
                .distinct()
                .peek(CAPABILITY_PROVIDERS::remove) // Bust the cache
                .filter(this::hasCableNeighbour) // Verify if should [re]join network
                .map(this::discoverCapabilityProvider) // Check if we can get capabilities from this block
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(prov -> CAPABILITY_PROVIDERS.put(prov.getFirst(), prov.getSecond())); // track it
    }

    /**
     * Find a {@link CapabilityProvider} for a given {@link BlockPos} in the level associated with this cable network.
     * If multiple {@link CapabilityProviderMapper}s match, the first one is returned.
     *
     * @param pos block position be checked
     * @return {@link Optional} containing the {@link CapabilityProvider} if one was found
     */
    public Optional<Pair<BlockPos, ICapabilityProvider>> discoverCapabilityProvider(BlockPos pos) {
        return SFMCapabilityProviderMappers.DEFERRED_MAPPERS
                .get()
                .getValues()
                .stream()
                .map(mapper -> mapper.getProviderFor(LEVEL, pos))
                .filter(Optional::isPresent)
                .map(iCapabilityProvider -> Pair.of(pos, iCapabilityProvider.get()))
                .findFirst();
    }

    /**
     * Cables should only join the network if they would be touching a cable already in the network
     *
     * @param pos Candidate cable position
     * @return {@code true} if adjacent to cable in network
     */
    public boolean hasCableNeighbour(BlockPos pos) {
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
                    if (!containsCableLocation(current)) return;
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
        CAPABILITY_PROVIDERS
                .entrySet()
                .stream()
                .filter(entry -> validInvPositions.contains(entry.getKey()))
                .forEach(entry -> network.CAPABILITY_PROVIDERS.put(entry.getKey(), entry.getValue()));

        return network;
    }

    public boolean containsCableLocation(BlockPos pos) {
        return CABLES.contains(pos);
    }

    public boolean isInNetwork(BlockPos pos) {
        return CAPABILITY_PROVIDERS.containsKey(pos);
    }


    public Optional<ICapabilityProvider> getCapabilityProvider(BlockPos pos) {
        return Optional.ofNullable(CAPABILITY_PROVIDERS.get(pos));
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
        CAPABILITY_PROVIDERS.putAll(other.CAPABILITY_PROVIDERS);
    }

    public boolean isEmpty() {
        return CABLES.isEmpty();
    }

    public Map<BlockPos, ICapabilityProvider> getCapabilityProviders() {
        return Collections.unmodifiableMap(CAPABILITY_PROVIDERS);
    }

    public Set<BlockPos> getCables() {
        return Collections.unmodifiableSet(CABLES);
    }
}
