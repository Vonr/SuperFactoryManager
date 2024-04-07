package ca.teamdman.sfm.common.cablenetwork;

import ca.teamdman.sfm.common.util.SFMUtils;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class CapabilityCache {
    private final Long2ObjectMap<Object2ObjectOpenHashMap<Capability<?>, Object2ObjectOpenHashMap<Direction, LazyOptional<?>>>> CACHE = new Long2ObjectOpenHashMap<>();

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

    public <CAP> @Nullable LazyOptional<CAP> getCapability(
            BlockPos pos,
            Capability<CAP> capKind,
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
                        return (LazyOptional<CAP>) found;
                    }
                }

            }
        }
        return null;
    }

    public void putAll(CapabilityCache other) {
        other.CACHE.forEach((pos, capMap) -> {
            capMap.forEach((capKind, dirMap) -> {
                dirMap.forEach((direction, cap) -> {
                    putCapability(BlockPos.of(pos), (Capability) capKind, direction, cap);
                });
            });
        });
    }

    public Stream<BlockPos> getPositions() {
        return CACHE.keySet().longStream().mapToObj(BlockPos::of);
    }

    public <CAP> LazyOptional<CAP> getOrDiscoverCapability(
            Level level,
            BlockPos pos,
            Capability<CAP> capKind,
            @Nullable Direction direction
    ) {
        // Check cache
        var found = getCapability(pos, capKind, direction);
        if (found != null) {
            return found;
        }

        // No capability found, discover it
        var provider = SFMUtils.discoverCapabilityProvider(level, pos);
        if (provider.isPresent()) {
            var lazyOptional = provider.get().getCapability(capKind, direction);
            putCapability(pos, capKind, direction, lazyOptional);
            lazyOptional.addListener(x -> remove(pos, capKind, direction));
            return lazyOptional;
        }

        // Fallback to empty
        return LazyOptional.empty();
    }

    public void remove(
            BlockPos pos,
            Capability<?> capKind,
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
            Capability<CAP> capKind,
            @Nullable Direction direction,
            LazyOptional<CAP> cap
    ) {
        var capMap = CACHE.computeIfAbsent(pos.asLong(), k -> new Object2ObjectOpenHashMap<>());
        var dirMap = capMap.computeIfAbsent(capKind, k -> new Object2ObjectOpenHashMap<>());
        dirMap.put(direction, cap);
    }
}
