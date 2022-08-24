package ca.teamdman.sfm.common.program;

import ca.teamdman.sfml.ast.OutputStatement;

public class LimitedOutputSlot<STACK, CAP> extends LimitedSlot<STACK, CAP, OutputResourceMatcher<STACK>> {
    private final OutputStatement<STACK, CAP> STATEMENT;

    public LimitedOutputSlot(
            OutputStatement<STACK, CAP> statement,
            CAP handler,
            int slot,
            OutputResourceMatcher<STACK> matcher
    ) {
        super(handler, matcher.LIMIT.resourceId().getType(), slot, matcher);
        this.STATEMENT = statement;
        MATCHER.visit(this);
    }
}
