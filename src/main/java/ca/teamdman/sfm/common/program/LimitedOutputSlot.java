package ca.teamdman.sfm.common.program;

import net.minecraftforge.items.IItemHandler;

public class LimitedOutputSlot extends LimitedSlot<OutputItemMatcher> {
    public LimitedOutputSlot(
            IItemHandler handler,
            int slot,
            OutputItemMatcher matcher
    ) {
        super(handler, slot, matcher);
        MATCHER.visit(this);
    }
}
