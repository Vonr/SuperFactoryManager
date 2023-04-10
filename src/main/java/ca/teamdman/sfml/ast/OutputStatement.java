package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.LimitedInputSlot;
import ca.teamdman.sfm.common.program.LimitedOutputSlot;
import ca.teamdman.sfm.common.program.OutputResourceTracker;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.resourcetype.ResourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public record OutputStatement(
        LabelAccess labelAccess,
        ResourceLimits resourceLimits,
        boolean each
) implements Statement {
    @Override
    public void tick(ProgramContext context) {
        // gather the input slots from all the input statements
        var inputSlots = context.getInputs().flatMap(in -> in.getSlots(context));

        // collect the output slots
        var outputSlots = this.getSlots(context).toArray(LimitedOutputSlot[]::new);

        // try and move resources from input slots to output slots
        grabbing:
        for (LimitedInputSlot in : (Iterable<? extends LimitedInputSlot<?, ?, ?>>) inputSlots::iterator) {
            for (LimitedOutputSlot out : outputSlots) {
                if (in.TYPE.equals(out.TYPE)) {
                    in.moveTo(out);
                    if (in.isDone()) continue grabbing;
                }
            }
        }
    }

    /**
     * The output statement contains labels.
     * Each block in the world can have more than one label.
     * Each block can have a block entity.
     * Each block entity can have 0 or more slots.
     * <p>
     * We want collect the slots from all the labelled blocks.
     */
    public Stream<LimitedOutputSlot<?, ?, ?>> getSlots(ProgramContext context) {
        Stream.Builder<LimitedOutputSlot<?, ?, ?>> rtn = Stream.builder();

        // find all the types referenced in the output statement
        Stream<ResourceType> types = resourceLimits
                .resourceLimits()
                .stream()
                .map(ResourceLimit::resourceId)
                .distinct()
                .map(ResourceIdentifier::getResourceType);

        if (each) {
            for (var type : (Iterable<ResourceType>) types::iterator) {
                for (var cap : (Iterable<?>) type.getCapabilities(context, labelAccess)::iterator) {
                    // create a new matcher for each capability
                    List<OutputResourceTracker<?, ?, ?>> outputMatchers = resourceLimits.createOutputTrackers();
//                    if (type.matchesCapType(cap)) {
                    getSlots((ResourceType<Object, Object, Object>) type, cap, outputMatchers).forEach(rtn::add);
//                    }
                }
            }
        } else {
            // create a single matcher to be shared by all capabilities
            List<OutputResourceTracker<?, ?, ?>> outputTracker = resourceLimits.createOutputTrackers();
            for (var type : (Iterable<ResourceType>) types::iterator) {
                for (var cap : (Iterable<?>) type.getCapabilities(context, labelAccess)::iterator) {
//                    if (type.matchesCapType(cap)) {
                    getSlots((ResourceType<Object, Object, Object>) type, cap, outputTracker).forEach(rtn::add);
//                    }
                }
            }
        }
        return rtn.build();
    }

    private <STACK, ITEM, CAP> List<LimitedOutputSlot<STACK, ITEM, CAP>> getSlots(
            ResourceType<STACK, ITEM, CAP> type,
            CAP capability,
            List<OutputResourceTracker<?, ?, ?>> trackers
    ) {
        List<LimitedOutputSlot<STACK, ITEM, CAP>> rtn = new ArrayList<>();
        for (int slot = 0; slot < type.getSlots(capability); slot++) {
            if (labelAccess.slots().contains(slot)) {
                // the destination is allowed to be empty, don't check for empty slot
                for (OutputResourceTracker<?, ?, ?> tracker : trackers) {
                    if (tracker.getLimit().resourceId().getResourceType().matchesCapabilityType(capability)) {
                        // doesn't matter if destination slot is empty
                        // doesn't let us short circuit like it does for input slots
                        var x = new LimitedOutputSlot<>(
                                this,
                                capability,
                                slot,
                                (OutputResourceTracker<STACK, ITEM, CAP>) tracker
                        );
                        rtn.add(x);
                    }
                }
            }
        }
        return rtn;
    }
}
