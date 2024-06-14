package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.program.InputResourceTracker;
import ca.teamdman.sfm.common.program.LimitedInputSlot;
import ca.teamdman.sfm.common.program.LimitedInputSlotObjectPool;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.resourcetype.ResourceType;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class InputStatement implements IOStatement {
    private final LabelAccess labelAccess;
    private final ResourceLimits resourceLimits;
    private final boolean each;
    private @Nullable ArrayDeque<LimitedInputSlot<?, ?, ?>> cachedInputSlots = null;
    private int inputCheck = -2;

    public InputStatement(
            LabelAccess labelAccess,
            ResourceLimits resourceLimits,
            boolean each
    ) {
        this.labelAccess = labelAccess;
        this.resourceLimits = resourceLimits;
        this.each = each;
    }

    @Override
    public void tick(ProgramContext context) {
        context.addInput(this);
        context.getLogger().debug(x -> x.accept(Constants.LocalizationKeys.LOG_PROGRAM_TICK_INPUT_STATEMENT.get(
                toString()
        )));
    }

    @SuppressWarnings({"rawtypes", "unchecked"}) // basically impossible to make this method generic safe
    public void gatherSlots(ProgramContext context, Consumer<LimitedInputSlot<?, ?, ?>> acceptor) {
        context
                .getLogger()
                .debug(x -> x.accept(Constants.LocalizationKeys.LOG_PROGRAM_TICK_IO_STATEMENT_GATHER_SLOTS.get(
                        toStringPretty()
                )));
        if (cachedInputSlots != null) {
            context
                    .getLogger()
                    .trace(x -> x.accept(Constants.LocalizationKeys.LOG_PROGRAM_TICK_IO_STATEMENT_GATHER_SLOTS_CACHE_HIT.get()));
            cachedInputSlots.forEach(acceptor);
            return;
        }

        context
                .getLogger()
                .trace(x -> x.accept(Constants.LocalizationKeys.LOG_PROGRAM_TICK_IO_STATEMENT_GATHER_SLOTS_CACHE_MISS.get()));
        cachedInputSlots = new ArrayDeque<>();
        var oldAcceptor = acceptor;
        acceptor = slot -> {
            cachedInputSlots.add(slot);
            oldAcceptor.accept(slot);
        };

        Stream<ResourceType> types = resourceLimits
                .resourceLimits()
                .stream()
                .map(ResourceLimit::resourceId)
                .map((ResourceIdentifier x) -> x.getResourceType())
                .distinct();

        if (!each) {
            context
                    .getLogger()
                    .debug(x -> x.accept(Constants.LocalizationKeys.LOG_PROGRAM_TICK_IO_STATEMENT_GATHER_SLOTS_NOT_EACH.get()));
            // create a single matcher to be shared by all capabilities
            List<InputResourceTracker<?, ?, ?>> inputTrackers = resourceLimits.createInputTrackers();
            for (var type : (Iterable<ResourceType>) types::iterator) {
                context
                        .getLogger()
                        .debug(x -> x.accept(Constants.LocalizationKeys.LOG_PROGRAM_TICK_IO_STATEMENT_GATHER_SLOTS_FOR_RESOURCE_TYPE.get(
                                type.CAPABILITY_KIND.getName())));
                for (var capability : (Iterable) type.getCapabilities(context, labelAccess)::iterator) {
                    gatherSlots(
                            context,
                            (ResourceType<Object, Object, Object>) type,
                            capability,
                            inputTrackers,
                            acceptor
                    );
                }
            }
        } else {
            context
                    .getLogger()
                    .debug(x -> x.accept(Constants.LocalizationKeys.LOG_PROGRAM_TICK_IO_STATEMENT_GATHER_SLOTS_EACH.get()));
            for (ResourceType type : (Iterable<ResourceType>) types::iterator) {
                context
                        .getLogger()
                        .debug(x -> x.accept(Constants.LocalizationKeys.LOG_PROGRAM_TICK_IO_STATEMENT_GATHER_SLOTS_FOR_RESOURCE_TYPE.get(
                                type.CAPABILITY_KIND.getName())));
                for (var cap : (Iterable<?>) type.getCapabilities(context, labelAccess)::iterator) {
                    List<InputResourceTracker<?, ?, ?>> inputTrackers = resourceLimits.createInputTrackers();
                    gatherSlots(context, (ResourceType<Object, Object, Object>) type, cap, inputTrackers, acceptor);
                }
            }
        }

        inputCheck = LimitedInputSlotObjectPool.INSTANCE.getIndex();
    }

    @Override
    public String toString() {
        return "INPUT " + resourceLimits.toStringPretty(Limit.MAX_QUANTITY_NO_RETENTION) + " FROM " + (
                each
                ? "EACH "
                : ""
        ) + labelAccess;
    }

    @Override
    public String toStringPretty() {
        StringBuilder sb = new StringBuilder();
        sb.append("INPUT");
        String rls = resourceLimits.toStringPretty(Limit.MAX_QUANTITY_NO_RETENTION);
        if (rls.lines().count() > 1) {
            sb.append("\n");
            sb.append(rls.lines().map(s -> "  " + s).collect(Collectors.joining("\n")));
            sb.append("\n");
        } else {
            sb.append(" ");
            sb.append(rls);
            sb.append(" ");
        }
        sb.append("FROM ");
        sb.append(each ? "EACH " : "");
        sb.append(labelAccess);
        return sb.toString();
    }

    @Override
    public LabelAccess labelAccess() {
        return labelAccess;
    }

    @Override
    public ResourceLimits resourceLimits() {
        return resourceLimits;
    }

    @Override
    public boolean each() {
        return each;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (InputStatement) obj;
        return Objects.equals(this.labelAccess, that.labelAccess) &&
               Objects.equals(this.resourceLimits, that.resourceLimits) &&
               this.each == that.each;
    }

    @Override
    public int hashCode() {
        return Objects.hash(labelAccess, resourceLimits, each);
    }

    /**
     * Release the slots acquired by this statement
     * </p>
     * This was separated from {@link OutputStatement#tick(ProgramContext)} because we need input statements
     * to keep their counts when used by multiple output statements.
     */
    public void freeSlots() {
        if (cachedInputSlots != null) {
            // TODO: replace all asserts with throws
            if (inputCheck == -2) {
                throw new IllegalStateException("Input slots were not gathered before freeing");
            }
            LimitedInputSlotObjectPool.INSTANCE.release(
                    cachedInputSlots,
                    inputCheck
            );
            cachedInputSlots = null;
            inputCheck = -2;
        }
    }

    private <STACK, ITEM, CAP> void gatherSlots(
            ProgramContext context, ResourceType<STACK, ITEM, CAP> type,
            CAP capability,
            List<InputResourceTracker<?, ?, ?>> trackers,
            Consumer<LimitedInputSlot<?, ?, ?>> acceptor
    ) {
        context
                .getLogger()
                .debug(x -> x.accept(Constants.LocalizationKeys.LOG_PROGRAM_TICK_IO_STATEMENT_GATHER_SLOTS_RANGE.get(
                        labelAccess.slots())));
        for (int slot = 0; slot < type.getSlots(capability); slot++) {
            int finalSlot = slot;
            if (labelAccess.slots().contains(slot)) {
                STACK stack = type.getStackInSlot(capability, slot);
                if (shouldCreateSlot(type, stack)) {
                    for (InputResourceTracker<?, ?, ?> tracker : trackers) {
                        if (tracker.matchesCapabilityType(capability) && tracker.test(stack)) {
                            context
                                    .getLogger()
                                    .debug(x -> x.accept(Constants.LocalizationKeys.LOG_PROGRAM_TICK_IO_STATEMENT_GATHER_SLOTS_SLOT_CREATED.get(
                                            finalSlot, stack, tracker.toString())));
                            //noinspection unchecked
                            acceptor.accept(LimitedInputSlotObjectPool.INSTANCE.acquire(
                                    capability,
                                    slot,
                                    (InputResourceTracker<STACK, ITEM, CAP>) tracker,
                                    stack
                            ));
                        }
                    }
                } else {
                    context
                            .getLogger()
                            .debug(x -> x.accept(Constants.LocalizationKeys.LOG_PROGRAM_TICK_IO_STATEMENT_GATHER_SLOTS_SLOT_SHOULD_NOT_CREATE.get(
                                    finalSlot, stack)));
                }
            } else {
                context
                        .getLogger()
                        .debug(x -> x.accept(Constants.LocalizationKeys.LOG_PROGRAM_TICK_IO_STATEMENT_GATHER_SLOTS_SLOT_NOT_IN_RANGE.get(
                                finalSlot)));
            }
        }
    }

    private <STACK, ITEM, CAP> boolean shouldCreateSlot(ResourceType<STACK, ITEM, CAP> type, STACK stack) {
        // make sure there are items to move
        return !type.isEmpty(stack);
    }

}
