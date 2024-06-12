package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.program.InputResourceTracker;
import ca.teamdman.sfm.common.program.LimitedInputSlot;
import ca.teamdman.sfm.common.program.LimitedInputSlotObjectPool;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.resourcetype.ResourceType;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record InputStatement(
        LabelAccess labelAccess,
        ResourceLimits resourceLimits,
        boolean each
) implements IOStatement {

    @Override
    public void tick(ProgramContext context) {
        context.addInput(this);
        context.getLogger().debug(x->x.accept(Constants.LocalizationKeys.LOG_PROGRAM_TICK_INPUT_STATEMENT.get(
                toString()
        )));
    }

    @SuppressWarnings({"rawtypes", "unchecked"}) // basically impossible to make this method generic safe
    public void gatherSlots(ProgramContext context, Consumer<LimitedInputSlot<?, ?, ?>> acceptor) {
        Stream<ResourceType> types = resourceLimits
                .resourceLimits()
                .stream()
                .map(ResourceLimit::resourceId)
                .map((ResourceIdentifier x) -> x.getResourceType())
                .distinct();

        if (!each) {
            // create a single matcher to be shared by all capabilities
            List<InputResourceTracker<?, ?, ?>> inputMatchers = resourceLimits.createInputTrackers();
            for (var type : (Iterable<ResourceType>) types::iterator) {
                for (var capability : (Iterable) type.getCapabilities(context, labelAccess)::iterator) {
                    gatherSlots((ResourceType<Object, Object, Object>) type, capability, inputMatchers, acceptor);
                }
            }
        } else {
            for (ResourceType type : (Iterable<ResourceType>) types::iterator) {
                for (var cap : (Iterable<?>) type.getCapabilities(context, labelAccess)::iterator) {
                    List<InputResourceTracker<?, ?, ?>> inputTrackers = resourceLimits.createInputTrackers();
                    gatherSlots((ResourceType<Object, Object, Object>) type, cap, inputTrackers, acceptor);
                }
            }
        }
    }

    private <STACK, ITEM, CAP> void gatherSlots(
            ResourceType<STACK, ITEM, CAP> type,
            CAP capability,
            List<InputResourceTracker<?, ?, ?>> trackers,
            Consumer<LimitedInputSlot<?, ?, ?>> acceptor
    ) {
        for (int slot = 0; slot < type.getSlots(capability); slot++) {
            if (labelAccess.slots().contains(slot)) {
                STACK stack = type.getStackInSlot(capability, slot);
                if (shouldCreateSlot(type, stack)) {
                    for (InputResourceTracker<?, ?, ?> tracker : trackers) {
                        if (tracker.matchesCapabilityType(capability) && tracker.test(stack)) {
                            //noinspection unchecked
                            acceptor.accept(LimitedInputSlotObjectPool.INSTANCE.acquire(
                                    capability,
                                    slot,
                                    (InputResourceTracker<STACK, ITEM, CAP>) tracker
                            ));
                        }
                    }
                }
            }
        }
    }

    private <STACK,ITEM,CAP> boolean shouldCreateSlot(ResourceType<STACK,ITEM,CAP> type, STACK stack) {
        // make sure there are items to move
        return !type.isEmpty(stack);
    }

    @Override
    public String toString() {
        return "INPUT " + resourceLimits.toStringPretty(Limit.MAX_QUANTITY_NO_RETENTION) + " FROM " + (each ? "EACH " : "") + labelAccess;
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
}
