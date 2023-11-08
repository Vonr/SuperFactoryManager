package ca.teamdman.sfm.common.cablenetwork;

import ca.teamdman.sfm.common.util.SFMUtils;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class CableNetwork {

    protected final Level LEVEL;
    protected final LongSet CABLE_POSITIONS = new LongOpenHashSet();
    protected final Long2ObjectMap<ICapabilityProvider> CAPABILITY_PROVIDER_POSITIONS = new Long2ObjectOpenHashMap<>();

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
                .getBlock() instanceof ICableBlock;
    }

    public void rebuildNetwork(BlockPos start) {
        CABLE_POSITIONS.clear();
        CAPABILITY_PROVIDER_POSITIONS.clear();
        discoverCables(start).forEach(this::addCable);
    }

    public void rebuildNetworkFromCache(BlockPos start, CableNetwork cache) {
        CABLE_POSITIONS.clear();
        CAPABILITY_PROVIDER_POSITIONS.clear();

        // discover existing cables
        var cables = SFMUtils.getRecursiveStream((current, next, results) -> {
            results.accept(current);
            for (Direction d : Direction.values()) {
                BlockPos offset = current.offset(d.getNormal());
                if (cache.containsCablePosition(offset)) {
                    next.accept(offset);
                }
            }
        }, start).toList();
        for (BlockPos cablePos : cables) {
            CABLE_POSITIONS.add(cablePos.asLong());
        }
        // discover existing capability providers
        cables
                .stream()
                .flatMap(cablePos -> Arrays.stream(Direction.values()).map(Direction::getNormal).map(cablePos::offset))
                .distinct()
                .filter(pos -> cache.CAPABILITY_PROVIDER_POSITIONS.containsKey(pos.asLong()))
                .map(capPos -> Pair.of(capPos, cache.CAPABILITY_PROVIDER_POSITIONS.get(capPos.asLong())))
                .forEach(pair -> CAPABILITY_PROVIDER_POSITIONS.put(pair.getFirst().asLong(), pair.getSecond()));
    }

    public Stream<BlockPos> discoverCables(BlockPos startPos) {
        return SFMUtils.getRecursiveStream((current, next, results) -> {
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
        boolean isNewMember = CABLE_POSITIONS.add(pos.asLong());
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
                .peek(pos -> CAPABILITY_PROVIDER_POSITIONS.remove(pos.asLong())) // Bust the cache
                .filter(this::isAdjacentToCable) // Verify if should [re]join network
                .map(pos -> SFMUtils
                        .discoverCapabilityProvider(LEVEL, pos)
                        .map(prov -> Pair.of(pos, prov))) // Check if we can get capabilities from this block
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(prov -> CAPABILITY_PROVIDER_POSITIONS.put(
                        prov.getFirst().asLong(),
                        prov.getSecond()
                )); // track it
    }

    @Override
    public String toString() {
        return "CableNetwork{level="
               + getLevel().dimension().location()
               + ", #cables="
               + getCableCount()
               + ", #capabilityProviders="
               + CAPABILITY_PROVIDER_POSITIONS.size()
               + "}";
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
        return CABLE_POSITIONS.contains(pos.asLong());
    }

    public boolean isInNetwork(BlockPos pos) {
        return CAPABILITY_PROVIDER_POSITIONS.containsKey(pos.asLong());
    }


    public Optional<ICapabilityProvider> getCapabilityProvider(BlockPos pos) {
        return Optional.ofNullable(CAPABILITY_PROVIDER_POSITIONS.get(pos.asLong()));
    }

    public int getCableCount() {
        return CABLE_POSITIONS.size();
    }

    /**
     * Merges a network into this one, such as when a cable connects two networks
     *
     * @param other Foreign network
     */
    public void mergeNetwork(CableNetwork other) {
        CABLE_POSITIONS.addAll(other.CABLE_POSITIONS);
        CAPABILITY_PROVIDER_POSITIONS.putAll(other.CAPABILITY_PROVIDER_POSITIONS);
    }

    public boolean isEmpty() {
        return CABLE_POSITIONS.isEmpty();
    }

    public Stream<BlockPos> getCablePositions() {
        return CABLE_POSITIONS.longStream().mapToObj(BlockPos::of);
    }

    public Stream<BlockPos> getCapabilityProviderPositions() {
        return CAPABILITY_PROVIDER_POSITIONS.keySet().longStream().mapToObj(BlockPos::of);
    }

    /**
     * Discover what networks would exist if this network did not have a cable at {@code cablePos}.
     * @param cablePos cable position to be removed
     * @return resulting networks to replace this network
     */
    protected List<CableNetwork> withoutCable(BlockPos cablePos) {
        CABLE_POSITIONS.remove(cablePos.asLong());
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
