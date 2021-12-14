package ca.teamdman.sfm.common.program;

import ca.teamdman.sfml.ast.OutputStatement;
import net.minecraftforge.items.IItemHandler;

public class LimitedOutputSlot extends LimitedSlot<OutputItemMatcher> {
    private final OutputStatement STATEMENT;

    public LimitedOutputSlot(
            OutputStatement statement,
            IItemHandler handler,
            int slot,
            OutputItemMatcher matcher
    ) {
        super(handler, slot, matcher);
        this.STATEMENT = statement;
        MATCHER.visit(this);
    }
}
