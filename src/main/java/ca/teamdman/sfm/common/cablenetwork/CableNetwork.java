package ca.teamdman.sfm.common.cablenetwork;

import ca.teamdman.sfm.common.registry.SFMCapabilityProviderMappers;
import ca.teamdman.sfm.common.util.SFMUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

public class CableNetwork {

    protected final Level                              LEVEL;
    protected final Set<BlockPos>                      CABLES               = new HashSet<>();
    protected final Map<BlockPos, ICapabilityProvider> CAPABILITY_PROVIDERS = new HashMap<>();

    public CableNetwork(Level level) {
        this.LEVEL = level;
    }

    /**
     * Only cable blocks are valid network members
     */
    public static boolean isCable(@Nullable Level world, BlockPos cablePos) {
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

    public void addCable(BlockPos pos) {
        boolean isNewMember = CABLES.add(pos);
        if (isNewMember) {
            rebuildAdjacentInventories(pos);
        }
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
    @SuppressWarnings("UnstableApiUsage") // for the javadoc lol
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

    @SuppressWarnings("unused")
    public Map<BlockPos, ICapabilityProvider> getCapabilityProviders() {
        return Collections.unmodifiableMap(CAPABILITY_PROVIDERS);
    }

    public Set<BlockPos> getCables() {
        return Collections.unmodifiableSet(CABLES);
    }

    /**
     * Discover what networks would exist if this network did not have a cable at cablePos
     */
    protected List<CableNetwork> withoutCable(BlockPos cablePos) {
        List<CableNetwork> branches = new ArrayList<>();
        for (var direction : Direction.values()) {
            var offsetPos = cablePos.offset(direction.getNormal());
            if (!isCable(getLevel(), offsetPos)) continue;
            // make sure that a branch network doesn't already contain this cable
            if (branches.stream().anyMatch(n -> n.containsCableLocation(offsetPos))) continue;
            var branchNetwork = new CableNetwork(this.getLevel());
            branchNetwork.rebuildNetwork(offsetPos);
            branches.add(branchNetwork);
        }
        return branches;
    }
}
