package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.InputResourceMatcher;
import ca.teamdman.sfm.common.program.LimitedInputSlot;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.program.ResourceMatcher;
import ca.teamdman.sfm.common.resourcetype.ResourceType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record InputStatement<STACK, CAP>(
        LabelAccess labelAccess,
        Matchers<STACK, CAP> matchers,
        boolean each
) implements Statement {

    @Override
    public void tick(ProgramContext context) {
        context.addInput(this);
    }

    public Stream<LimitedInputSlot<STACK, CAP>> getSlots(ProgramContext context) {
        List<CAP> caps = matchers
                .createInputMatchers()
                .stream()
                .map(ResourceMatcher::getLimit)
                .map(ResourceLimit::resourceId)
                .map(ResourceIdentifier::getResourceType)
                .flatMap(t -> t.getCaps(context, labelAccess)).toList();
        Set<ResourceType<STACK, CAP>> types = matchers
                .createInputMatchers()
                .stream()
                .map(ResourceMatcher::getLimit)
                .map(ResourceLimit::resourceId)
                .map(ResourceIdentifier::getResourceType)
                .collect(Collectors.toSet());
        var                                    rtn      = Stream.<LimitedInputSlot<STACK, CAP>>builder();
        List<InputResourceMatcher<STACK, CAP>> matchers = null;
        for (var cap : caps) {
            if (matchers == null || each) matchers = this.matchers.createInputMatchers();
            for (var type : types) {
                if (!type.matchesCapType(cap)) continue;
                for (int slot = 0; slot < type.getSlots(cap); slot++) {
                    if (labelAccess.slots().contains(slot)) {
                        for (var matcher : matchers) {
                            rtn.add(new LimitedInputSlot<>(this, cap, slot, matcher));
                        }
                    }
                }
            }
        }
        return rtn.build();
    }
}
