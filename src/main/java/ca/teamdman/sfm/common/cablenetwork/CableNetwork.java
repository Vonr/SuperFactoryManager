package ca.teamdman.sfm.common.cablenetwork;

import ca.teamdman.sfm.common.util.SFMUtil;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

public class CableNetwork {

    protected final Level LEVEL;
    protected final Set<BlockPos> CABLES = new HashSet<>();
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

    public void rebuildNetwork(BlockPos start) {
        CABLES.clear();
        CAPABILITY_PROVIDERS.clear();
        discoverCables(start).forEach(this::addCable);
    }

    public void rebuildNetworkFromCache(BlockPos start, CableNetwork cache) {
        CABLES.clear();
        CAPABILITY_PROVIDERS.clear();

        // discover existing cables
        var cables = SFMUtil.getRecursiveStream((current, next, results) -> {
            results.accept(current);
            for (Direction d : Direction.values()) {
                BlockPos offset = current.offset(d.getNormal());
                if (cache.containsCablePosition(offset)) {
                    next.accept(offset);
                }
            }
        }, start).toList();
        CABLES.addAll(cables);

        // discover existing capability providers
        cables
                .stream()
                .flatMap(cablePos -> Arrays.stream(Direction.values()).map(Direction::getNormal).map(cablePos::offset))
                .distinct()
                .filter(cache.CAPABILITY_PROVIDERS::containsKey)
                .map(capPos -> Pair.of(capPos, cache.CAPABILITY_PROVIDERS.get(capPos)))
                .forEach(pair -> CAPABILITY_PROVIDERS.put(pair.getFirst(), pair.getSecond()));
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
                .filter(this::isAdjacentToCable) // Verify if should [re]join network
                .map(pos -> SFMUtil
                        .discoverCapabilityProvider(LEVEL, pos)
                        .map(prov -> Pair.of(pos, prov))) // Check if we can get capabilities from this block
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(prov -> CAPABILITY_PROVIDERS.put(prov.getFirst(), prov.getSecond())); // track it
    }

    /**
     * Cables should only join the network if they would be touching a cable already in the network
     *
     * @param pos Candidate cable position
     * @return {@code true} if adjacent to cable in network
     */
    public boolean isAdjacentToCable(BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (containsCablePosition(pos.offset(direction.getNormal()))) {
                return true;
            }
        }
        return false;
    }

    public boolean containsCablePosition(BlockPos pos) {
        return CABLES.contains(pos);
    }

    public boolean isInNetwork(BlockPos pos) {
        return CAPABILITY_PROVIDERS.containsKey(pos);
    }


    public Optional<ICapabilityProvider> getCapabilityProvider(BlockPos pos) {
        return Optional.ofNullable(CAPABILITY_PROVIDERS.get(pos));
    }

    public int getCableCount() {
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
     * Discover what networks would exist if this network did not have a cable at {@code cablePos}.
     * @param cablePos cable position to be removed
     * @return resulting networks to replace this network
     */
    protected List<CableNetwork> withoutCable(BlockPos cablePos) {
        CABLES.remove(cablePos);
        List<CableNetwork> branches = new ArrayList<>();
        for (var direction : Direction.values()) {
            var offsetPos = cablePos.offset(direction.getNormal());
            if (!containsCablePosition(offsetPos)) continue;
            // make sure that a branch network doesn't already contain this cable
            if (branches.stream().anyMatch(n -> n.containsCablePosition(offsetPos))) continue;
            var branchNetwork = new CableNetwork(this.getLevel());
            branchNetwork.rebuildNetworkFromCache(offsetPos, this);
            branches.add(branchNetwork);
        }
        return branches;
    }
}
