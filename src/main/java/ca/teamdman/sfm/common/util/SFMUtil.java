package ca.teamdman.sfm.common.util;

import ca.teamdman.sfm.common.cablenetwork.CapabilityProviderMapper;
import ca.teamdman.sfm.common.program.LimitedInputSlot;
import ca.teamdman.sfm.common.registry.SFMCapabilityProviderMappers;
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
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SFMUtil {

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
        while (toVisit.size() > 0) {
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

    public static TranslatableContents deserializeTranslation(CompoundTag tag) {
        var key = tag.getString("key");
        var args = tag
                .getList("args", Tag.TAG_STRING)
                .stream()
                .map(StringTag.class::cast)
                .map(StringTag::getAsString)
                .toArray();
        return new TranslatableContents(key, args);
    }

    public static <STACK, ITEM, CAP> Optional<InputStatement> getInputStatementForSlot(
            LimitedInputSlot<STACK, ITEM, CAP> slot,
            LabelAccess labelAccess
    ) {
        return SFMResourceTypes.DEFERRED_TYPES
                .get()
                .getResourceKey(slot.type)
                .map(x -> {
                    //noinspection unchecked,rawtypes
                    return (ResourceKey<ResourceType<STACK, ITEM, CAP>>) (ResourceKey) x;
                })
                .map((ResourceKey<ResourceType<STACK, ITEM, CAP>> resourceTypeResourceKey) -> SFMUtil.getInputStatementForStack(
                        resourceTypeResourceKey,
                        slot.type,
                        slot.peekExtractPotential(),
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
                                .slots()
                ), inputStatement.resourceLimits(), inputStatement.each()));
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
                )
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

    public interface RecursiveBuilder<T> {

        void accept(T next, Consumer<T> nextQueue, Consumer<T> resultBuilder);
    }

    /**
     * Find a {@link CapabilityProvider} as provided by the registered capability provider mappers.
     * If multiple {@link CapabilityProviderMapper}s match, the first one is returned.
     */
    @SuppressWarnings("UnstableApiUsage") // for the javadoc lol
    public static Optional<ICapabilityProvider> discoverCapabilityProvider(LevelAccessor level, BlockPos pos) {
        return SFMCapabilityProviderMappers.DEFERRED_MAPPERS
                .get()
                .getValues()
                .stream()
                .map(mapper -> mapper.getProviderFor(level, pos))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }
}
