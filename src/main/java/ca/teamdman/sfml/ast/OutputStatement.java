package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.LimitedInputSlot;
import ca.teamdman.sfm.common.program.LimitedOutputSlot;
import ca.teamdman.sfm.common.program.OutputResourceTracker;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.resourcetype.ResourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record OutputStatement(
        LabelAccess labelAccess,
        ResourceLimits resourceLimits,
        boolean each
) implements Statement {
    @Override
    public void tick(ProgramContext context) {
        // gather the input slots from all the input statements
        List<LimitedInputSlot<?, ?>> inputSlots = new ArrayList<>();
        for (InputStatement in : context.getInputs().toList()) {
            in.getSlots(context).forEach(inputSlots::add);
        }

        // collect the output slots
        List<LimitedOutputSlot<?, ?>> outputSlots = this.getSlots(context).toList();

        // try and move resources from input slots to output slots
        grabbing:
        for (LimitedInputSlot in : inputSlots) {
            for (LimitedOutputSlot out : outputSlots) {
                if (in.TYPE.equals(out.TYPE)) {
                    in.moveTo(out);
                }
                if (in.isDone()) continue grabbing;
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
    public Stream<LimitedOutputSlot<?, ?>> getSlots(ProgramContext context) {
        Stream.Builder<LimitedOutputSlot<?, ?>> rtn = Stream.builder();

        // find all the types referenced in the output statement
        Set<ResourceType<?, ?>> types = resourceLimits
                .resourceLimits()
                .stream()
                .map(ResourceLimit::resourceId)
                .map(ResourceIdentifier::getResourceType)
                .collect(Collectors.toSet());

        // collect all the capabilities that match the types
        List<?> capabilities = types.stream()
                .flatMap(t -> t.getCaps(context, labelAccess))
                .toList();


        if (each) {
            for (var cap : capabilities) {
                // create a new matcher for each capability
                List<OutputResourceTracker<?, ?>> outputMatchers = resourceLimits.createOutputTrackers();
                for (var type : types) {
                    if (type.matchesCapType(cap)) {
                        getSlots((ResourceType<Object, Object>) type, cap, outputMatchers).forEach(rtn::add);
                    }
                }
            }
        } else {
            // create a single matcher to be shared by all capabilities
            List<OutputResourceTracker<?, ?>> outputMatchers = resourceLimits.createOutputTrackers();
            for (var cap : capabilities) {
                for (var type : types) {
                    if (type.matchesCapType(cap)) {
                        getSlots((ResourceType<Object, Object>) type, cap, outputMatchers).forEach(rtn::add);
                    }
                }
            }
        }
        return rtn.build();
    }

    private <STACK, CAP> List<LimitedOutputSlot<STACK, CAP>> getSlots(
            ResourceType<STACK, CAP> type,
            CAP capability,
            List<OutputResourceTracker<?, ?>> trackers
    ) {
        List<LimitedOutputSlot<STACK, CAP>> rtn = new ArrayList<>();
        for (int slot = 0; slot < type.getSlots(capability); slot++) {
            if (labelAccess.slots().contains(slot)) {
                // the destination is allowed to be empty, don't check for empty slot
                for (OutputResourceTracker<?, ?> tracker : trackers) {
                    // we can't make any assumptions about the tracker
                    var x = new LimitedOutputSlot<>(
                            this,
                            capability,
                            slot,
                            (OutputResourceTracker<STACK, CAP>) tracker
                    );
                    rtn.add(x);
                }
            }
        }
        return rtn;
    }
}
