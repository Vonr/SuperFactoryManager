package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record OutputStatement<STACK, CAP>(
        LabelAccess labelAccess,
        Matchers<STACK> matchers,
        boolean each
) implements Statement {
    @Override
    public void tick(ProgramContext context) {
        var inputSlots  = context.getInputs().flatMap(x -> x.getSlots(context));
        var outputSlots = getSlots(context).toList();
        grabbing:
        for (var in : (Iterable<LimitedInputSlot<STACK, CAP>>) inputSlots::iterator) {
            for (var out : outputSlots) {
                in.moveTo(out);
                if (in.isDone()) continue grabbing;
            }
        }
    }

    public Stream<LimitedOutputSlot<STACK, CAP>> getSlots(ProgramContext context) {
        var handlers = matchers
                .createInputMatchers()
                .stream()
                .map(ResourceMatcher::getLimit)
                .map(ResourceLimit::resourceId)
                .map(ResourceIdentifier::getType)
                .flatMap(t -> t.getCaps(context, labelAccess));
        var types = matchers
                .createInputMatchers()
                .stream()
                .map(ResourceMatcher::getLimit)
                .map(ResourceLimit::resourceId)
                .map(ResourceIdentifier::getType)
                .collect(Collectors.toSet());
        var                                rtn      = Stream.<LimitedOutputSlot<STACK, CAP>>builder();
        List<OutputResourceMatcher<STACK>> matchers = null;
        for (var cap : (Iterable<CAP>) handlers::iterator) {
            if (matchers == null || each) matchers = this.matchers.createOutputMatchers();
            for (var type : types) {
                if (!type.matchesCapType(cap)) continue;
                for (int slot = 0; slot < type.getSlots(cap); slot++) {
                    if (labelAccess.slots().contains(slot)) {
                        for (var matcher : matchers) {
                            rtn.add(new LimitedOutputSlot<>(this, cap, slot, matcher));
                        }
                    }
                }
            }
        }
        return rtn.build();
    }
}
