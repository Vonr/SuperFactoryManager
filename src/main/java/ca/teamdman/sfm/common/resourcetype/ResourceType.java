package ca.teamdman.sfm.common.resourcetype;

import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfml.ast.LabelAccess;
import ca.teamdman.sfml.ast.ResourceIdentifier;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.capabilities.Capability;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public abstract class ResourceType<STACK, ITEM, CAP> {
    private final Map<ITEM, ResourceLocation> registryKeyCache = new Object2ObjectOpenHashMap<>();


    public final Capability<CAP> CAPABILITY;

    public ResourceType(Capability<CAP> CAPABILITY) {
        this.CAPABILITY = CAPABILITY;
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
        Optional<ItemStack> disk = programContext.getManager().getDisk();
        if (disk.isEmpty()) return Stream.empty();
        LabelPositionHolder labelPositions = LabelPositionHolder.from(disk.get());
        CableNetwork network = programContext.getNetwork();
        return labelAccess.roundRobin().gather(labelAccess, labelPositions)
                .map(network::getCapabilityProvider)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .flatMap((
                                 prov -> labelAccess
                                         .directions()
                                         .stream()
                                         .map(direction -> prov.getCapability(CAPABILITY, direction))
                         ))
                .map(x -> {
                    //noinspection DataFlowIssue
                    return x.orElse(null);
                })
                .filter(x -> {
                    //noinspection ConstantValue,Convert2MethodRef
                    return x != null;
                });
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
