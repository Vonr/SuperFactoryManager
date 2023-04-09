package ca.teamdman.sfm.common.program;

import ca.teamdman.sfml.ast.OutputStatement;

public class LimitedOutputSlot<STACK, CAP> extends LimitedSlot<STACK, CAP, OutputResourceTracker<STACK, CAP>> {
    private final OutputStatement STATEMENT;

    public LimitedOutputSlot(
            OutputStatement statement,
            CAP handler,
            int slot,
            OutputResourceTracker<STACK, CAP> matcher
    ) {
        super(handler, matcher.LIMIT.resourceId().getResourceType(), slot, matcher);
        this.STATEMENT = statement;
        TRACKER.visit(this);
    }
}
