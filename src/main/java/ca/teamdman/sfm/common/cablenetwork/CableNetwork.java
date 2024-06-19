package ca.teamdman.sfm.common.cablenetwork;

import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.logging.TranslatableLogger;
import ca.teamdman.sfm.common.util.SFMUtils;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class CableNetwork {

    protected final Level LEVEL;
    protected final LongSet CABLE_POSITIONS = new LongOpenHashSet();
    protected final CapabilityCache CAPABILITY_CACHE = new CapabilityCache();

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
        CAPABILITY_CACHE.clear();
        discoverCables(start).forEach(this::addCable);
    }

    public void rebuildNetworkFromCache(BlockPos start, CableNetwork other) {
        CABLE_POSITIONS.clear();
        CAPABILITY_CACHE.clear();

        // discover connected cables
        var cables = SFMUtils.getRecursiveStream((current, next, results) -> {
            results.accept(current);
            for (Direction d : Direction.values()) {
                BlockPos offset = current.offset(d.getNormal());
                if (other.containsCablePosition(offset)) {
                    next.accept(offset);
                }
            }
        }, start).toList();
        for (BlockPos cablePos : cables) {
            CABLE_POSITIONS.add(cablePos.asLong());
        }

        // discover capabilities
        cables
                .stream()
                .flatMap(cablePos -> Arrays.stream(Direction.values()).map(Direction::getNormal).map(cablePos::offset))
                .distinct()
                .forEach(pos -> CAPABILITY_CACHE.overwriteFromOther(pos, other.CAPABILITY_CACHE));
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
        CABLE_POSITIONS.add(pos.asLong());
    }

    public Level getLevel() {
        return LEVEL;
    }

    @Override
    public String toString() {
        return "CableNetwork{level="
               + getLevel().dimension().location()
               + ", #cables="
               + getCableCount()
               + ", #cache="
               + CAPABILITY_CACHE.size()
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

    public <CAP> @Nonnull LazyOptional<CAP> getCapability(
            Capability<CAP> cap,
            BlockPos pos,
            @Nullable Direction direction,
            TranslatableLogger logger
    ) {
        // TODO: move this check higher up the chain
        if (!isAdjacentToCable(pos)) {
            logger.warn(x->x.accept(Constants.LocalizationKeys.LOGS_MISSING_ADJACENT_CABLE.get(pos)));
            return LazyOptional.empty();
        }
        return CAPABILITY_CACHE.getOrDiscoverCapability(LEVEL, pos, cap, direction, logger);
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
        CAPABILITY_CACHE.putAll(other.CAPABILITY_CACHE);
    }

    public boolean isEmpty() {
        return CABLE_POSITIONS.isEmpty();
    }

    public Stream<BlockPos> getCablePositions() {
        return CABLE_POSITIONS.longStream().mapToObj(BlockPos::of);
    }

    public LongSet getCablePositionsRaw() {
        return CABLE_POSITIONS;
    }

    // TODO: replace the logging that uses this with something that shows sidedness
    public Stream<BlockPos> getCapabilityProviderPositions() {
        return CAPABILITY_CACHE.getPositions();
    }

    /**
     * Discover what networks would exist if this network did not have a cable at {@code cablePos}.
     *
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
