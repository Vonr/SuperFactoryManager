package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfml.ast.Label;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import javax.annotation.Nullable;

public class LimitedOutputSlot<STACK, ITEM, CAP> {
    @SuppressWarnings("NotNullFieldNotInitialized") // done in init method in constructor
    public ResourceType<STACK, ITEM, CAP> type;
    @SuppressWarnings("NotNullFieldNotInitialized") // done in init method in constructor
    public CAP handler;
    @SuppressWarnings("NotNullFieldNotInitialized") // done in init method in constructor
    public BlockPos pos;
    @SuppressWarnings("NotNullFieldNotInitialized") // done in init method in constructor
    public Label label;
    public int slot;
    public boolean freed;
    @SuppressWarnings("NotNullFieldNotInitialized") // done in init method in constructor
    public OutputResourceTracker<STACK, ITEM, CAP> tracker;
    @SuppressWarnings("NotNullFieldNotInitialized") // done in init method in constructor
    private Direction direction;
    private @Nullable STACK stackInSlotCache = null;

    public LimitedOutputSlot(
            Label label,
            BlockPos pos,
            Direction direction,
            int slot,
            CAP handler,
            OutputResourceTracker<STACK, ITEM, CAP> tracker,
            STACK stack
    ) {
        this.init(handler, label, pos, direction, slot, tracker, stack);
    }

    public boolean isDone() {
        if (tracker.isDone()) {
            return true;
        }
        if (slot > type.getSlots(handler) - 1) {
            // composter block changes how many slots it has between insertions
            return true;
        }
        STACK stack = getStackInSlot();
        long count = type.getAmount(stack);
        if (count >= type.getMaxStackSizeForSlot(handler, slot)) {
            return true;
        }
        return count != 0 && !tracker.test(stack);
    }

    public STACK getStackInSlot() {
        if (stackInSlotCache == null) {
            stackInSlotCache = type.getStackInSlot(handler, slot);
        }
        return stackInSlotCache;
    }

    public STACK insert(
            STACK stack,
            boolean simulate
    ) {
        if (!simulate) stackInSlotCache = null;
        return type.insert(handler, slot, stack, simulate);
    }

    public void init(
            CAP handler,
            Label label,
            BlockPos pos,
            Direction direction,
            int slot,
            OutputResourceTracker<STACK, ITEM, CAP> tracker,
            STACK stack
    ) {
        this.stackInSlotCache = stack;

        this.handler = handler;
        this.tracker = tracker;
        this.slot = slot;
        this.pos = pos;
        this.label = label;
        this.direction = direction;
        this.freed = false;

        //noinspection DataFlowIssue
        this.type = tracker.getLimit().resourceId().getResourceType();
        if (type == null) {
            throw new NullPointerException("type");
        }
    }

    @Override
    public String toString() {
        return "LimitedOutputSlot{"
               + "label=" + label
               + ", pos=" + pos
               + ", direction=" + direction
               + ", slot=" + slot
               + ", cap=" + type.displayAsCapabilityClass()
               + ", tracker=" + tracker
               + '}';
    }
}
