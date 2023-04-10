package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.InputResourceTracker;
import ca.teamdman.sfm.common.program.LimitedInputSlot;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.resourcetype.ResourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record InputStatement(
        LabelAccess labelAccess,
        ResourceLimits resourceLimits,
        boolean each
) implements Statement {

    @Override
    public void tick(ProgramContext context) {
        context.addInput(this);
    }

    public Stream<LimitedInputSlot<?, ?>> getSlots(ProgramContext context) {
        Stream.Builder<LimitedInputSlot<?, ?>> rtn = Stream.builder();

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
                List<InputResourceTracker<?, ?>> inputMatchers = resourceLimits.createInputTrackers();
                for (var type : types) {
                    if (type.matchesCapType(cap)) {
                        getSlots((ResourceType<Object, Object>) type, cap, inputMatchers).forEach(rtn::add);
                    }
                }
            }
        } else {
            // create a single matcher to be shared by all capabilities
            List<InputResourceTracker<?, ?>> inputMatchers = resourceLimits.createInputTrackers();
            for (var capability : capabilities) {
                for (var type : types) {
                    if (type.matchesCapType(capability)) {
                        getSlots((ResourceType<Object, Object>) type, capability, inputMatchers).forEach(rtn::add);
                    }
                }
            }
        }
        return rtn.build();
    }

    private <STACK, CAP> List<LimitedInputSlot<STACK, CAP>> getSlots(
            ResourceType<STACK, CAP> type,
            CAP capability,
            List<InputResourceTracker<?, ?>> trackers
    ) {
        List<LimitedInputSlot<STACK, CAP>> rtn = new ArrayList<>();
        for (int slot = 0; slot < type.getSlots(capability); slot++) {
            if (labelAccess.slots().contains(slot)) {
                STACK stack = type.getStackInSlot(capability, slot);
                if (!type.isEmpty(stack)) {
                    for (InputResourceTracker<?, ?> tracker : trackers) {
                        if (tracker.test(stack)) {
                            var x = new LimitedInputSlot<>(
                                    this,
                                    capability,
                                    slot,
                                    (InputResourceTracker<STACK, CAP>) tracker
                            );
                            rtn.add(x);
                        }
                    }
                }
            }
        }
        return rtn;
    }
}
