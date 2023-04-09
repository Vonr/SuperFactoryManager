package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfml.ast.InputStatement;

public class LimitedInputSlot<STACK, CAP> extends LimitedSlot<STACK, CAP, InputResourceTracker<STACK, CAP>> {

    private final InputStatement STATEMENT;

    public LimitedInputSlot(
            InputStatement statement,
            CAP handler,
            int slot,
            InputResourceTracker<STACK, CAP> matcher
    ) {
        super(handler, matcher.LIMIT.resourceId().getResourceType(), slot, matcher);
        this.STATEMENT = statement;
    }

    public InputStatement getStatement() {
        return STATEMENT;
    }

    public void moveTo(LimitedOutputSlot<STACK, CAP> other) {
        var potential = this.extract(Long.MAX_VALUE, true);
        if (this.TYPE.isEmpty(potential)) {
            setDone();
            return;
        }
        if (!TRACKER.test(potential)) return;
        if (!other.TRACKER.test(potential)) return;
        var remainder = other.insert(potential, true);

        // how many can we move unrestrained
        var toMove = this.TYPE.getCount(potential) - this.TYPE.getCount(remainder);
        if (toMove == 0) return;

        // how many have we promised to leave in this slot
        toMove -= this.TRACKER.getExistingPromise(SLOT);

        // how many more need to be promised
        var toPromise = this.TRACKER.getRemainingPromise();
        toPromise = Long.min(toMove, toPromise);
        toMove -= toPromise;

        // track the promise
        this.TRACKER.track(SLOT, 0, toPromise);

        // if whole slot has been promised, mark done
        if (toMove == 0) {
            setDone();
            return;
        }

        // how many are we allowed to put in the other inventory
        toMove = Math.min(toMove, other.TRACKER.getMaxTransferable());

        // how many can we move at once
        toMove = Math.min(toMove, this.TRACKER.getMaxTransferable());
        if (toMove <= 0) return;

        // extract item for real
        var extracted = this.TYPE.extract(this.HANDLER, SLOT, toMove, false);
        // insert item for real
        remainder = other.TYPE.insert(other.HANDLER, other.SLOT, extracted, false);
        // track transfer amounts
        this.TRACKER.trackTransfer(toMove);
        other.TRACKER.trackTransfer(toMove);

        // if remainder exists, someone lied.
        if (!other.TYPE.isEmpty(remainder)) {
            SFM.LOGGER.error(
                    "Failed to move all promised items, took {} but had {} left over after insertion.",
                    extracted,
                    remainder
            );
        }
    }
}
