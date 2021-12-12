package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.SFM;
import net.minecraftforge.items.IItemHandler;

public class LimitedSlot {
    private final IItemHandler HANDLER;
    private final int          SLOT;
    private final ItemMatcher  MATCHER;
    private       boolean      done = false;

    public LimitedSlot(IItemHandler handler, int slot, ItemMatcher matcher) {
        this.HANDLER = handler;
        this.SLOT    = slot;
        this.MATCHER = matcher;
    }

    public boolean isDone() {
        return done || MATCHER.isDone();
    }

    private void setDone() {
        this.done = true;
    }

    public void moveTo(LimitedSlot other) {
        var potential = this.HANDLER.extractItem(SLOT, Integer.MAX_VALUE, true);
        if (potential.isEmpty()) return;
        var remainder = other.HANDLER.insertItem(other.SLOT, potential, true);

        // how many can we move unrestrained
        var toMove = potential.getCount() - remainder.getCount();
        if (toMove == 0) return;

        // how many do we need to leave in this inventory
        var shouldNotMove = Math.min(toMove, this.MATCHER.getPromised(this.SLOT));
        this.MATCHER.track(this.SLOT, 0, shouldNotMove);
        toMove -= shouldNotMove;
        if (toMove == 0) { // this whole slot has been reserved, continue
            setDone();
            return;
        }
        // how many are we allowed to put in the other inventory
        toMove = Math.min(toMove, other.MATCHER.getPromised(other.SLOT));

        // how many can we move
        toMove = Math.min(this.MATCHER.clamp(toMove), other.MATCHER.clamp(toMove));
        if (toMove <= 0) return;

        var extracted = this.HANDLER.extractItem(SLOT, toMove, false);
        remainder = other.HANDLER.insertItem(other.SLOT, extracted, false);
        this.MATCHER.track(this.SLOT, toMove, 0);
        other.MATCHER.track(other.SLOT, toMove, toMove);
        if (!remainder.isEmpty()) {
            SFM.LOGGER.error(
                    "Failed to move all promised items, took {} but had {} left over after insertion.",
                    extracted,
                    remainder
            );
        }
    }
}
