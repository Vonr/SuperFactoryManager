package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.InputResourceTracker;
import ca.teamdman.sfm.common.program.LimitedInputSlot;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.resourcetype.ResourceType;

import java.util.ArrayList;
import java.util.List;
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

    public Stream<LimitedInputSlot<?, ?, ?>> getSlots(ProgramContext context) {
        Stream.Builder<LimitedInputSlot<?, ?, ?>> rtn = Stream.builder();

        Stream<ResourceType> types = resourceLimits
                .resourceLimits()
                .stream()
                .map(ResourceLimit::resourceId)
                .distinct()
                .map(ResourceIdentifier::getResourceType);

        if (each) {
            for (ResourceType type : (Iterable<ResourceType>) types::iterator) {
                for (var cap : (Iterable<?>) type.getCapabilities(context, labelAccess)::iterator) {
                    // create a new matcher for each capability
                    List<InputResourceTracker<?, ?, ?>> inputTrackers = resourceLimits.createInputTrackers();
//                    if (type.matchesCapType(cap)) {
                    getSlots((ResourceType<Object, Object, Object>) type, cap, inputTrackers).forEach(rtn::add);
//                    }
                }
            }
        } else {
            // create a single matcher to be shared by all capabilities
            List<InputResourceTracker<?, ?, ?>> inputMatchers = resourceLimits.createInputTrackers();
            for (var type : (Iterable<ResourceType>) types::iterator) {
                for (var capability : (Iterable) type.getCapabilities(context, labelAccess)::iterator) {
//                    if (type.matchesCapType(capability)) {
                    getSlots(
                            (ResourceType<Object, Object, Object>) type,
                            capability,
                            inputMatchers
                    ).forEach(rtn::add);
//                    }
                }
            }
        }
        return rtn.build();
    }

    private <STACK, ITEM, CAP> List<LimitedInputSlot<STACK, ITEM, CAP>> getSlots(
            ResourceType<STACK, ITEM, CAP> type,
            CAP capability,
            List<InputResourceTracker<?, ?, ?>> trackers
    ) {
        List<LimitedInputSlot<STACK, ITEM, CAP>> rtn = new ArrayList<>();
        for (int slot = 0; slot < type.getSlots(capability); slot++) {
            if (labelAccess.slots().contains(slot)) {
                STACK stack = type.getStackInSlot(capability, slot);
                if (!type.isEmpty(stack)) {
                    for (InputResourceTracker<?, ?, ?> tracker : trackers) {
                        if (tracker.getLimit().resourceId().getResourceType().matchesCapabilityType(capability)
                            && tracker.test(stack)) {
                            var x = new LimitedInputSlot<>(
                                    this,
                                    capability,
                                    slot,
                                    (InputResourceTracker<STACK, ITEM, CAP>) tracker
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
