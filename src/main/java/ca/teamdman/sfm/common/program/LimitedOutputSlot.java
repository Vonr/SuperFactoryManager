package ca.teamdman.sfm.common.program;

import ca.teamdman.sfml.ast.OutputStatement;

public class LimitedOutputSlot<STACK, ITEM, CAP> extends LimitedSlot<STACK, ITEM, CAP, OutputResourceTracker<STACK, ITEM, CAP>> {
    private final OutputStatement STATEMENT;

    public LimitedOutputSlot(
            OutputStatement statement,
            CAP handler,
            int slot,
            OutputResourceTracker<STACK, ITEM, CAP> matcher
    ) {
        super(handler, matcher.LIMIT.resourceId().getResourceType(), slot, matcher);
        this.STATEMENT = statement;
        TRACKER.visit(this);
    }
}
