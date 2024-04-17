package ca.teamdman.sfm.common.util;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.program.LimitedInputSlot;
import ca.teamdman.sfm.common.registry.SFMResourceTypes;
import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfml.ast.Number;
import ca.teamdman.sfml.ast.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SFMUtils {

    /**
     * Gets a stream using a self-feeding mapping function. Prevents the
     * re-traversal of elements that have been visited before.
     *
     * @param operator Consumes queue elements to build the result set and
     *                 append the next queue elements
     * @param first    Initial value, not checked against the filter
     * @param <T>      Type that the mapper consumes and produces
     * @return Stream result after termination of the recursive mapping process
     */
    public static <T> Stream<T> getRecursiveStream(
            RecursiveBuilder<T> operator, T first
    ) {
        Stream.Builder<T> builder = Stream.builder();
        Set<T> debounce = new HashSet<>();
        Deque<T> toVisit = new ArrayDeque<>();
        toVisit.add(first);
        debounce.add(first);
        while (!toVisit.isEmpty()) {
            T current = toVisit.pop();
            operator.accept(current, next -> {
                if (!debounce.contains(next)) {
                    debounce.add(next);
                    toVisit.add(next);
                }
            }, builder::add);
        }
        return builder.build();
    }

    public static TranslatableContents deserializeTranslation(CompoundTag tag) {
        var key = tag.getString("key");
        var args = tag
                .getList("args", Tag.TAG_STRING)
                .stream()
                .map(StringTag.class::cast)
                .map(StringTag::getAsString)
                .toArray();
        return getTranslatableContents(key, args);
    }

    public static CompoundTag serializeTranslation(TranslatableContents contents) {
        CompoundTag tag = new CompoundTag();
        tag.putString("key", contents.getKey());
        ListTag args = new ListTag();
        for (var arg : contents.getArgs()) {
            args.add(StringTag.valueOf(arg.toString()));
        }
        tag.put("args", args);
        return tag;
    }

    /**
     * Helper method to avoid noisy git merges between versions
     */
    public static TranslatableContents getTranslatableContents(String key, Object... args) {
        return new TranslatableContents(key, null, args);
    }

    /**
     * Helper method to avoid noisy git merges between versions
     */
    public static TranslatableContents getTranslatableContents(String key) {
        return getTranslatableContents(key, new Object[]{});
    }

    public static <STACK, ITEM, CAP> Optional<InputStatement> getInputStatementForSlot(
            LimitedInputSlot<STACK, ITEM, CAP> slot,
            LabelAccess labelAccess
    ) {
        STACK potential = slot.peekExtractPotential();
        if (slot.type.isEmpty(potential)) return Optional.empty();
        long toMove = slot.type.getAmount(potential);
        toMove = Long.min(toMove, slot.tracker.getResourceLimit().limit().quantity().number().value());
        long remainingObligation = slot.tracker.getRemainingRetentionObligation();
        toMove -= Long.min(toMove, remainingObligation);
        potential = slot.type.withCount(potential, toMove);
        STACK stack = potential;

        return SFMResourceTypes.DEFERRED_TYPES
                .getResourceKey(slot.type)
                .map(x -> {
                    //noinspection unchecked,rawtypes
                    return (ResourceKey<ResourceType<STACK, ITEM, CAP>>) (ResourceKey) x;
                })
                .map((ResourceKey<ResourceType<STACK, ITEM, CAP>> resourceTypeResourceKey) -> SFMUtils.getInputStatementForStack(
                        resourceTypeResourceKey,
                        slot.type,
                        stack,
                        "temp",
                        slot.slot,
                        false,
                        null
                ))
                // update the labels
                .map(inputStatement -> new InputStatement(new LabelAccess(
                        labelAccess.labels(),
                        labelAccess.directions(),
                        inputStatement.labelAccess()
                                .slots(),
                        RoundRobin.disabled()
                ), inputStatement.resourceLimits(), inputStatement.each()));
    }


    public interface RecursiveBuilder<T> {

        void accept(T current, Consumer<T> next, Consumer<T> results);
    }


    public static <STACK, ITEM, CAP> InputStatement getInputStatementForStack(
            ResourceKey<ResourceType<STACK, ITEM, CAP>> resourceTypeResourceKey,
            ResourceType<STACK, ITEM, CAP> resourceType,
            STACK stack,
            String label,
            int slot,
            boolean each,
            @Nullable Direction direction
    ) {
        LabelAccess labelAccess = new LabelAccess(
                List.of(new Label(label)),
                new DirectionQualifier(
                        direction == null
                        ? EnumSet.noneOf(Direction.class)
                        : EnumSet.of(direction)),
                new NumberRangeSet(
                        new NumberRange[]{new NumberRange(slot, slot)}
                ),
                RoundRobin.disabled()
        );
        Limit limit = new Limit(
                new ResourceQuantity(
                        new Number(resourceType.getAmount(stack)),
                        ResourceQuantity.IdExpansionBehaviour.NO_EXPAND
                ),
                new ResourceQuantity(
                        new Number(0),
                        ResourceQuantity.IdExpansionBehaviour.NO_EXPAND
                )
        );
        ResourceLocation stackId = resourceType.getRegistryKey(stack);
        ResourceIdentifier<STACK, ITEM, CAP> resourceIdentifier = new ResourceIdentifier<>(
                resourceTypeResourceKey.location().getNamespace(),
                resourceTypeResourceKey.location().getPath(),
                stackId.getNamespace(),
                stackId.getPath()
        );
        ResourceLimit<STACK, ITEM, CAP> resourceLimit = new ResourceLimit<>(
                resourceIdentifier, limit
        );
        ResourceLimits resourceLimits = new ResourceLimits(
                List.of(resourceLimit),
                ResourceIdSet.EMPTY
        );
        return new InputStatement(
                labelAccess,
                resourceLimits,
                each
        );
    }

    public static String truncate(String input, int maxLength) {
        if (input.length() > maxLength) {
            SFM.LOGGER.warn(
                    "input too big, truncation has occurred! (len={}, max={}, over={})",
                    input.length(),
                    maxLength,
                    maxLength - input.length()
            );
            String truncationWarning = "\n...truncated";
            return input.substring(0, maxLength - truncationWarning.length()) + truncationWarning;
        }
        return input;
    }

    public static Stream<BlockPos> get3DNeighboursIncludingKittyCorner(BlockPos pos) {
        Stream.Builder<BlockPos> builder = Stream.builder();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x == 0 && y == 0 && z == 0) continue;
                    builder.accept(pos.offset(x, y, z));
                }
            }
        }
        return builder.build();
    }
}
