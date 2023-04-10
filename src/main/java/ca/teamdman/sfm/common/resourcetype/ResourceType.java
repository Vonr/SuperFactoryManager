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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ResourceType<STACK, ITEM, CAP> {
    private static final Map<String, Predicate<String>> patternCache = new Object2ObjectOpenHashMap<>();

    static {
        patternCache.put(".*", s -> true);
        patternCache.put("sfm:item:.*:.*", s -> true);
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


    public abstract STACK insert(CAP cap, int slot, STACK stack, boolean simulate);

    public abstract boolean isEmpty(STACK stack);

    public abstract boolean matchesStackType(Object o);

    public boolean test(ResourceIdentifier<STACK, ITEM, CAP> id, Object stack) {
        if (!matchesStackType(stack)) return false;
        if (isEmpty((STACK) stack)) return false;
        var stackId = getRegistryKey((STACK) stack);
        if (stackId == null) return false;
        Predicate<String> pred = patternCache.get(id.toString());
        if (pred == null) {
            pred = buildPredicate(id);
            patternCache.put(id.toString(), pred);
        }
        return pred.test(stackId.toString());

//        Predicate<String> namespacePredicate = patternCache.get(id.resourceNamespace());
//        if (namespacePredicate == null) {
//            namespacePredicate = buildPredicate(id.resourceNamespace());
//            patternCache.put(id.resourceNamespace(), namespacePredicate);
//        }
//        Predicate<String> namePredicate = patternCache.get(id.resourceName());
//        if (namePredicate == null) {
//            namePredicate = buildPredicate(id.resourceName());
//            patternCache.put(id.resourceName(), namePredicate);
//        }
//        return namespacePredicate.test(stackId.getNamespace()) && namePredicate.test(stackId.getPath());
    }

    private Predicate<String> buildPredicate(ResourceIdentifier<STACK, ITEM, CAP> id) {
        Predicate<String> checkNamespace = isRegexPattern(id.resourceNamespace())
                                           ? Pattern.compile(id.resourceNamespace()).asMatchPredicate()
                                           : id.resourceNamespace()::equals;
        Predicate<String> checkPath = isRegexPattern(id.resourceName())
                                      ? Pattern.compile(id.resourceName()).asMatchPredicate()
                                      : id.resourceName()::equals;
        var found = id
                .getResourceType()
                .getRegistry()
                .getEntries()
                .stream()
                .filter(entry -> checkNamespace.test(entry.getKey().location().getNamespace()))
                .filter(entry -> checkPath.test(entry.getKey().location().getPath()))
                .map(entry -> entry.getKey().location().toString())
                .collect(Collectors.toSet());
        return found::contains;
    }


    public abstract boolean matchesCapType(Object o);


    public Optional<CAP> asCapability(Object o) {
        return matchesCapType(o) ? Optional.of((CAP) o) : Optional.empty();
    }

    public Stream<CAP> getCapabilities(
            ProgramContext programContext, LabelAccess labelAccess
    ) {
        var disk = programContext.getManager().getDisk();
        if (disk.isEmpty()) return Stream.empty();
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
        // we tried caching this and it wasn't worth it
        return getRegistry().getKey(getItem(stack));
    }

    public abstract IForgeRegistry<ITEM> getRegistry();

    public abstract ITEM getItem(STACK stack);
}
