package ca.teamdman.sfm.common.resourcetype;

import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.program.LabelHolder;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfml.ast.LabelAccess;
import ca.teamdman.sfml.ast.ResourceIdentifier;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public abstract class ResourceType<STACK, ITEM, CAP> {
    private static final Map<String, Predicate<String>> patternCache = new Object2ObjectOpenHashMap<>();
    private final Map<ITEM, ResourceLocation> registryKeyCache = new Object2ObjectOpenHashMap<>();

    static {
        // we want to make common match-all patterns fast
        // resource names are lowercase alphanumeric with underscores
        String[] matchAny = new String[]{
                ".",
                "[a-z0-9/._-]",
                };
        String[] suffixes = new String[]{"+", "*"};
        for (String s : matchAny) {
            for (String suffix : suffixes) {
                patternCache.put(s + suffix, s1 -> true);
                patternCache.put("^" + s + suffix, s1 -> true);
                patternCache.put("^" + s + suffix + "$", s1 -> true);
                patternCache.put(s + suffix + "$", s1 -> true);
            }
        }
    }

    public final Capability<CAP> CAPABILITY;

    public ResourceType(Capability<CAP> CAPABILITY) {
        this.CAPABILITY = CAPABILITY;
    }

    private static Predicate<String> buildPredicate(String possiblePattern) {
        return isRegexPattern(possiblePattern)
               ? Pattern.compile(possiblePattern).asMatchPredicate()
               : possiblePattern::equals;
    }

    private static boolean isRegexPattern(String pattern) {
        String specialChars = ".?*+^$[](){}|\\";
        for (int i = 0; i < pattern.length(); i++) {
            if (specialChars.indexOf(pattern.charAt(i)) >= 0) {
                return true;
            }
        }
        return false;
    }

    public abstract long getCount(STACK stack);

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

    public static <STACK, ITEM, CAP> boolean stackIdMatches(
            ResourceIdentifier<STACK, ITEM, CAP> rid,
            ResourceLocation stackId
    ) {
        Predicate<String> namespacePredicate = patternCache.get(rid.resourceNamespace);
        if (namespacePredicate == null) {
            namespacePredicate = buildPredicate(rid.resourceNamespace);
            patternCache.put(rid.resourceNamespace, namespacePredicate);
        }
        Predicate<String> namePredicate = patternCache.get(rid.resourceName);
        if (namePredicate == null) {
            namePredicate = buildPredicate(rid.resourceName);
            patternCache.put(rid.resourceName, namePredicate);
        }
        return namespacePredicate.test(stackId.getNamespace()) && namePredicate.test(stackId.getPath());
    }

    public boolean stackMatches(ResourceIdentifier<STACK, ITEM, CAP> rid, Object other) {
        if (!matchesStackType(other)) return false;
        @SuppressWarnings("unchecked") STACK stack = (STACK) other;
        if (isEmpty(stack)) return false;
        var stackId = getRegistryKey(stack);
        return stackIdMatches(rid, stackId);
    }

    public abstract boolean matchesCapabilityType(Object o);

    public Stream<CAP> getCapabilities(
            ProgramContext programContext, LabelAccess labelAccess
    ) {
        Optional<ItemStack> disk = programContext.getManager().getDisk();
        if (disk.isEmpty()) return Stream.empty();
        LabelHolder labels = LabelHolder.from(disk.get());
        CableNetwork network = programContext.getNetwork();
        return labels.getPositions(labelAccess)
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

    public abstract IForgeRegistry<ITEM> getRegistry();

    public abstract ITEM getItem(STACK stack);
}
