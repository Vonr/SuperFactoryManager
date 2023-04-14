package ca.teamdman.sfm.common.program;

import ca.teamdman.sfml.ast.InputStatement;

public class LimitedInputSlot<STACK, ITEM, CAP> extends LimitedSlot<STACK, ITEM, CAP, InputResourceTracker<STACK, ITEM, CAP>> {

    private final InputStatement STATEMENT;

    public LimitedInputSlot(
            InputStatement statement,
            CAP handler,
            int slot,
            InputResourceTracker<STACK, ITEM, CAP> matcher
    ) {
        super(handler, matcher.LIMIT.resourceId().getResourceType(), slot, matcher);
        this.STATEMENT = statement;
    }

    public InputStatement getStatement() {
        return STATEMENT;
    }

}
