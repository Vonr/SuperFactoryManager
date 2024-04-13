package ca.teamdman.sfm.common.resourcetype;

import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfml.ast.LabelAccess;
import ca.teamdman.sfml.ast.ResourceIdentifier;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.util.LazyOptional;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class ResourceType<STACK, ITEM, CAP> {
    private final Map<ITEM, ResourceLocation> registryKeyCache = new Object2ObjectOpenHashMap<>();


    public final Capability<CAP> CAPABILITY_KIND;

    public ResourceType(Capability<CAP> CAPABILITY_KIND) {
        this.CAPABILITY_KIND = CAPABILITY_KIND;
    }


    public abstract long getAmount(STACK stack);

    public abstract STACK getStackInSlot(CAP cap, int slot);

    public abstract STACK extract(CAP cap, int slot, long amount, boolean simulate);

    public abstract int getSlots(CAP handler);

    public abstract long getMaxStackSize(STACK stack);

    public abstract long getMaxStackSize(CAP cap, int slot);


    public abstract STACK insert(CAP cap, int slot, STACK stack, boolean simulate);

    public abstract boolean isEmpty(STACK stack);

    @SuppressWarnings("unused")
    public abstract STACK getEmptyStack();

    public abstract boolean matchesStackType(Object o);

    public boolean matchesStack(ResourceIdentifier<STACK, ITEM, CAP> resourceId, Object stack) {
        if (!matchesStackType(stack)) return false;
        @SuppressWarnings("unchecked") STACK stack_ = (STACK) stack;
        if (isEmpty(stack_)) return false;
        var stackId = getRegistryKey(stack_);
        return resourceId.matchesStack(stackId);
    }

    public abstract boolean matchesCapabilityType(Object o);

    public Stream<CAP> getCapabilities(
            ProgramContext programContext, LabelAccess labelAccess
    ) {
        // Get labels from disk
        Optional<ItemStack> disk = programContext.getManager().getDisk();
        if (disk.isEmpty()) return Stream.empty();
        LabelPositionHolder labelPositions = LabelPositionHolder.from(disk.get());

        // Get positions
        Stream<BlockPos> positions = labelAccess.roundRobin().gather(labelAccess, labelPositions);

        // Expand positions to (pos,direction) pairs
        Stream<Pair<BlockPos, Direction>> position_direction_pairs = positions.flatMap(pos -> labelAccess
                .directions()
                .stream()
                .map(dir -> Pair.of(pos, dir)));

        // Get capability from the network
        CableNetwork network = programContext.getNetwork();
        Stream<LazyOptional<CAP>> caps = position_direction_pairs
                .map(pair -> {
                    BlockPos pos = pair.getFirst();
                    Direction dir = pair.getSecond();
                    return network.getCapability(CAPABILITY_KIND, pos, dir);
                });

        // Unwrap
        // We use isPresent check to detect validity
        // We use orElse with null to unwrap
        //noinspection ConstantValue,DataFlowIssue
        return caps
                .filter(LazyOptional::isPresent)
                .map(x -> x.orElse(null))
                .filter(Objects::nonNull);
    }

    public Stream<STACK> collect(CAP cap, LabelAccess labelAccess) {
        var rtn = Stream.<STACK>builder();
        for (int slot = 0; slot < getSlots(cap); slot++) {
            if (!labelAccess.slots().contains(slot)) continue;
            var stack = getStackInSlot(cap, slot);
            if (!isEmpty(stack)) {
                rtn.add(stack);
            }
        }
        return rtn.build();
    }

    public boolean registryKeyExists(ResourceLocation location) {
        return getRegistry().containsKey(location);
    }

    public ResourceLocation getRegistryKey(STACK stack) {
        ITEM item = getItem(stack);
        var found = registryKeyCache.get(item);
        if (found != null) return found;
        found = getRegistry().getKey(item);
        assert found != null;
        registryKeyCache.put(item, found);
        return found;
    }

    public abstract Registry<ITEM> getRegistry();

    public abstract ITEM getItem(STACK stack);

    public abstract STACK copy(STACK stack);

    protected abstract STACK setCount(STACK stack, long amount);

    @SuppressWarnings("unused")
    public STACK withCount(STACK stack, long count) {
        return setCount(copy(stack), count);
    }
}
