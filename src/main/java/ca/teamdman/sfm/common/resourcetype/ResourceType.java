package ca.teamdman.sfm.common.resourcetype;

import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.cablenetwork.CapabilityCache;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfml.ast.LabelAccess;
import ca.teamdman.sfml.ast.ResourceIdentifier;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class ResourceType<STACK, ITEM, CAP> {
    private final Map<ITEM, ResourceLocation> registryKeyCache = new Object2ObjectOpenHashMap<>();


    public final BlockCapability<CAP, @Nullable Direction> CAPABILITY_KIND;

    public ResourceType(BlockCapability<CAP, @Nullable Direction> CAPABILITY_KIND) {
        this.CAPABILITY_KIND = CAPABILITY_KIND;
    }


    public abstract long getAmount(STACK stack);

    /**
     * Some resource types may exceed MAX_LONG, this method should be used to get the difference between two stacks
     */
    public long getAmountDifference(STACK stack1, STACK stack2) {
        return getAmount(stack1) - getAmount(stack2);
    }

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
        // TODO: make this return (BlockPos, Direction, CAP) tuples for better logging
        // Log
        programContext
                .getLogger()
                .trace(x -> x.accept(Constants.LocalizationKeys.LOG_RESOURCE_TYPE_GET_CAPABILITIES_BEGIN.get(
                        CAPABILITY_KIND.name(), labelAccess
                )));

        Stream.Builder<CAP> found = Stream.builder();
        CableNetwork network = programContext.getNetwork();

        // Get positions
        Iterable<BlockPos> positions = labelAccess
                .roundRobin()
                .gather(labelAccess, programContext.getlabelPositions())::iterator;

        for (BlockPos pos : positions) {
            // Expand pos to (pos, direction) pairs
            for (Direction dir : (Iterable<? extends Direction>) labelAccess.directions().stream()::iterator) {
                // Get capability from the network
                @Nullable BlockCapabilityCache<CAP, @Nullable Direction> cap = network
                        .getCapability(CAPABILITY_KIND, pos, dir, programContext.getLogger());

                if (cap != null) {
                    // Add to stream
                    found.add(cap.getCapability());
                    programContext.getLogger().debug(x -> x.accept(Constants.LocalizationKeys.LOG_RESOURCE_TYPE_GET_CAPABILITIES_CAP_PRESENT.get(
                            CAPABILITY_KIND.name(), pos, dir
                    )));
                } else {
                    // Log error
                    programContext
                            .getLogger()
                            .error(x -> x.accept(Constants.LocalizationKeys.LOG_RESOURCE_TYPE_GET_CAPABILITIES_CAP_NOT_PRESENT.get(
                                    CAPABILITY_KIND.name(), pos, dir
                            )));
                }
            }
        }

        return found.build().filter(Objects::nonNull);
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

    @SuppressWarnings("unused")
    public STACK withCount(STACK stack, long count) {
        return setCount(copy(stack), count);
    }

    protected abstract STACK setCount(STACK stack, long amount);
}
