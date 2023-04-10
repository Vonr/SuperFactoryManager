package ca.teamdman.sfm.common.resourcetype;

import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.util.SFMLabelNBTHelper;
import ca.teamdman.sfml.ast.LabelAccess;
import ca.teamdman.sfml.ast.ResourceIdentifier;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class ResourceType<STACK, CAP> {
    public final Capability<CAP> CAPABILITY;

    public ResourceType(Capability<CAP> CAPABILITY) {
        this.CAPABILITY = CAPABILITY;
    }

    public abstract long getCount(STACK stack);

    public abstract STACK getStackInSlot(CAP cap, int slot);

    public abstract STACK extract(CAP cap, int slot, long amount, boolean simulate);

    public abstract int getSlots(CAP handler);

    public List<STACK> getStacksIfMatches(Object cap) {
        if (!matchesCapType(cap)) return List.of();
        return IntStream.range(0, getSlots((CAP) cap))
                .mapToObj(i -> getStackInSlot((CAP) cap, i))
                .collect(Collectors.toList());
    }

    public abstract STACK insert(CAP cap, int slot, STACK stack, boolean simulate);

    public abstract boolean isEmpty(STACK stack);

    public abstract boolean matchesStackType(Object o);

    private static final Map<String, Predicate<String>> patternCache = new HashMap<>();

    static {
        patternCache.put(".*", s -> true);
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

    public boolean test(ResourceIdentifier<STACK, CAP> id, Object stack) {
        if (!matchesStackType(stack)) return false;
        if (isEmpty((STACK) stack)) return false;
        var stackId = getRegistryKey((STACK) stack);
        if (stackId == null) return false;
        var nsPattern   = patternCache.computeIfAbsent(id.resourceNamespace(), ResourceType::buildPredicate);
        var namePattern = patternCache.computeIfAbsent(id.resourceName(), ResourceType::buildPredicate);
        return nsPattern.test(stackId.getNamespace()) && namePattern.test(stackId.getPath());
    }


    public abstract boolean matchesCapType(Object o);


    public Optional<CAP> asCapability(Object o) {
        return matchesCapType(o) ? Optional.of((CAP) o) : Optional.empty();
    }

    public Stream<CAP> getCaps(
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

    public abstract boolean registryKeyExists(ResourceLocation location);

    public abstract ResourceLocation getRegistryKey(STACK stack);
}
