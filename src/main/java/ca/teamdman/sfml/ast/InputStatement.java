package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.InputResourceTracker;
import ca.teamdman.sfm.common.program.LimitedInputSlot;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.resourcetype.ResourceType;

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
                    var stacksInSlots = type.getStacksIfMatches(cap);
                    for (int slot = 0; slot < stacksInSlots.size(); slot++) {
                        if (labelAccess.slots().contains(slot)) {
                            for (var matcher : inputMatchers) {
                                var x = new LimitedInputSlot(this, cap, slot, matcher);
                                rtn.add(x);
                            }
                        }
                    }
                }
            }
        } else {
            // create a single matcher to be shared by all capabilities
            List<InputResourceTracker<?, ?>> inputMatchers = resourceLimits.createInputTrackers();
            for (var cap : capabilities) {
                for (var type : types) {
                    var stacksInSlots = type.getStacksIfMatches(cap);
                    for (int slot = 0; slot < stacksInSlots.size(); slot++) {
                        if (labelAccess.slots().contains(slot)) {
                            for (var matcher : inputMatchers) {
                                var x = new LimitedInputSlot(this, cap, slot, matcher);
                                rtn.add(x);
                            }
                        }
                    }
                }
            }
        }
        return rtn.build();
    }
}
