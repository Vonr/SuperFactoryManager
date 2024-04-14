package ca.teamdman.sfm.common.cablenetwork;

import ca.teamdman.sfm.common.util.SFMUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class CapabilityCache {
    private final Long2ObjectMap<Object2ObjectOpenHashMap<BlockCapability<?, @Nullable Direction>, Object2ObjectOpenHashMap<Direction, BlockCapabilityCache<?, @Nullable Direction>>>> CACHE = new Long2ObjectOpenHashMap<>();

    public void clear() {
        CACHE.clear();
    }

    public int size() {
        return CACHE.values().stream().flatMap(x -> x.values().stream()).mapToInt(Object2ObjectOpenHashMap::size).sum();
    }

    public void overwriteFromOther(BlockPos pos, CapabilityCache other) {
        var found = other.CACHE.get(pos.asLong());
        if (found != null) {
            CACHE.put(pos.asLong(), new Object2ObjectOpenHashMap<>(found));
        }
    }

    public <CAP> @Nullable BlockCapabilityCache<CAP, @Nullable Direction> getCapability(
            BlockPos pos,
            BlockCapability<CAP, Direction> capKind,
            @Nullable Direction direction
    ) {
        if (CACHE.containsKey(pos.asLong())) {
            var capMap = CACHE.get(pos.asLong());
            if (capMap.containsKey(capKind)) {
                var dirMap = capMap.get(capKind);
                if (dirMap.containsKey(direction)) {
                    var found = dirMap.get(direction);
                    if (found == null) {
                        return null;
                    } else {
                        //noinspection unchecked
                        return (BlockCapabilityCache<CAP, Direction>) found;
                    }
                }

            }
        }
        return null;
    }

    @SuppressWarnings({"CodeBlock2Expr", "rawtypes", "unchecked"})
    public void putAll(CapabilityCache other) {
        other.CACHE.forEach((pos, capMap) -> {
            capMap.forEach((capKind, dirMap) -> {
                dirMap.forEach((direction, cap) -> {
                    putCapability(BlockPos.of(pos), (BlockCapability) capKind, direction, cap);
                });
            });
        });
    }

    public Stream<BlockPos> getPositions() {
        return CACHE.keySet().longStream().mapToObj(BlockPos::of);
    }

    public <CAP> @Nullable BlockCapabilityCache<CAP, @Nullable Direction> getOrDiscoverCapability(
            Level level,
            BlockPos pos,
            BlockCapability<CAP, @Nullable Direction> capKind,
            @Nullable Direction direction
    ) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return null;
        }

        var found = getCapability(pos, capKind, direction);
        if (found == null) {
            // Cache miss, discover and store it
            found = BlockCapabilityCache.<CAP, @Nullable Direction>create(
                    capKind,
                    serverLevel,
                    pos,
                    direction,
                    () -> true,
                    () -> remove(pos, capKind, direction)
            );
            putCapability(pos, capKind, direction, found);
        }
        return found;
    }

    public void remove(
            BlockPos pos,
            BlockCapability<?, @Nullable Direction> capKind,
            @Nullable Direction direction
    ) {
        if (CACHE.containsKey(pos.asLong())) {
            var capMap = CACHE.get(pos.asLong());
            if (capMap.containsKey(capKind)) {
                var dirMap = capMap.get(capKind);
                dirMap.remove(direction);
                if (dirMap.isEmpty()) {
                    capMap.remove(capKind);
                    if (capMap.isEmpty()) {
                        CACHE.remove(pos.asLong());
                    }
                }
            }
        }
    }

    public <CAP> void putCapability(
            BlockPos pos,
            BlockCapability<CAP, @Nullable Direction> capKind,
            @Nullable Direction direction,
            BlockCapabilityCache<CAP, @Nullable Direction> entry
    ) {
        var capMap = CACHE.computeIfAbsent(pos.asLong(), k -> new Object2ObjectOpenHashMap<>());
        var dirMap = capMap.computeIfAbsent(capKind, k -> new Object2ObjectOpenHashMap<>());
        dirMap.put(direction, entry);
    }
}
