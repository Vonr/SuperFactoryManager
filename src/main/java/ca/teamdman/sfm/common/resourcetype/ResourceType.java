package ca.teamdman.sfm.common.resourcetype;

import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.util.SFMLabelNBTHelper;
import ca.teamdman.sfml.ast.LabelAccess;
import ca.teamdman.sfml.ast.ResourceIdentifier;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public abstract class ResourceType<STACK, ITEM, CAP> {
    private static final Map<String, Predicate<String>> patternCache = new Object2ObjectOpenHashMap<>();
    private final Map<ITEM, ResourceLocation> registryKeyCache = new Object2ObjectOpenHashMap<>();

    static {
        patternCache.put(".*", s -> true);
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

    public boolean test(ResourceIdentifier<STACK, ITEM, CAP> id, Object other) {
        if (!matchesStackType(other)) return false;
        @SuppressWarnings("unchecked") STACK stack = (STACK) other;
        if (isEmpty(stack)) return false;
        var stackId = getRegistryKey(stack);
        Predicate<String> namespacePredicate = patternCache.get(id.resourceNamespace);
        if (namespacePredicate == null) {
            namespacePredicate = buildPredicate(id.resourceNamespace);
            patternCache.put(id.resourceNamespace, namespacePredicate);
        }
        Predicate<String> namePredicate = patternCache.get(id.resourceName);
        if (namePredicate == null) {
            namePredicate = buildPredicate(id.resourceName);
            patternCache.put(id.resourceName, namePredicate);
        }
        return namespacePredicate.test(stackId.getNamespace()) && namePredicate.test(stackId.getPath());
    }

    public abstract boolean matchesCapabilityType(Object o);

    public Stream<CAP> getCapabilities(
            ProgramContext programContext, LabelAccess labelAccess
    ) {
        var disk = programContext.getManager().getDisk();
        if (disk.isEmpty()) return Stream.empty();
        //noinspection DataFlowIssue,ConstantValue
        return SFMLabelNBTHelper
                .getPositions(disk.get(), labelAccess.labels())
                .map(programContext.getNetwork()::getCapabilityProvider)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .flatMap((
                                 prov -> labelAccess
                                         .directions()
                                         .stream()
                                         .map(direction -> prov.getCapability(CAPABILITY, direction))
                         ))
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

    public abstract IForgeRegistry<ITEM> getRegistry();

    public abstract ITEM getItem(STACK stack);
}
