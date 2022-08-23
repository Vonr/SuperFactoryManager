package ca.teamdman.sfm.common.program;

import ca.teamdman.sfml.ast.ResourceLimit;
import net.minecraft.world.item.ItemStack;

public class OutputItemMatcher extends ItemMatcher {
    private int seen = 0;

    public OutputItemMatcher(ResourceLimit resourceLimit) {
        super(resourceLimit);
    }

    public void visit(LimitedOutputSlot slot) {
        ItemStack stack = slot.getStackInSlot();
        if (test(stack)) {
            seen += stack.getCount();
        }
    }

    @Override
    public boolean isDone() {
        return false;
    }

    private int getRemainingRoom() {
        return ITEM_LIMIT.limit().retention() - seen;
    }

    @Override
    public void trackTransfer(int amount) {
        super.trackTransfer(amount);
        seen += amount;
    }

    @Override
    public int getMaxTransferable() {
        return Math.min(super.getMaxTransferable(), getRemainingRoom());
    }
}
