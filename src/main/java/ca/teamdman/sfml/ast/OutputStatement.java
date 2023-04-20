package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.program.*;
import ca.teamdman.sfm.common.resourcetype.ResourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class OutputStatement implements Statement {
    private static final LimitedOutputSlotObjectPool SLOT_POOL = new LimitedOutputSlotObjectPool();
    private final LabelAccess LABEL_ACCESS;
    private final ResourceLimits RESOURCE_LIMITS;
    private final boolean EACH;
    private int lastInputCapacity = 32;
    private int lastOutputCapacity = 32;
    public OutputStatement(
            LabelAccess labelAccess,
            ResourceLimits resourceLimits,
            boolean each
    ) {
        this.LABEL_ACCESS = labelAccess;
        this.RESOURCE_LIMITS = resourceLimits;
        this.EACH = each;
    }

    /**
     * Juicy method function here.
     * Given two slots, move as much as possible from one to the other.
     *
     * @param source      The slot to pull from
     * @param destination the slot to push to
     * @param <STACK>     the stack type
     * @param <ITEM>      the item type
     * @param <CAP>       the capability type
     */
    public static <STACK, ITEM, CAP> void moveTo(
            LimitedInputSlot<STACK, ITEM, CAP> source, LimitedOutputSlot<STACK, ITEM, CAP> destination
    ) {
        // always ensure types match
        // items and fluids are incompatible, etc
        if (!source.type.equals(destination.type)) return;

        // find out what we can pull out
        // should never be empty by the time we get here
        STACK potential = source.peekExtractPotential();
        // ensure the output slot allows this item
        if (!destination.tracker.test(potential)) return;
        // find out how much we can fit
        STACK remainder = destination.insert(potential, true);

        // how many can we move before accounting for limits
        long toMove = source.type.getCount(potential) - source.type.getCount(remainder);
        if (toMove == 0) return;

        // how many have we promised to RETAIN in this slot
        toMove -= source.tracker.getExistingRetentionObligation(source.slot);
        // how many more need we are obligated to leave to satisfy the remainder of the RETAIN limit
        long remainingObligation = source.tracker.getRemainingRetentionObligation();
        remainingObligation = Long.min(toMove, remainingObligation);
        toMove -= remainingObligation;

        // update the obligation tracker
        source.tracker.trackRetentionObligation(source.slot, remainingObligation);

        // if we can't move anything after our retention obligations, we're done
        if (toMove == 0) {
            source.setDone();
            return;
        }

        // apply output constraints
        toMove = Math.min(toMove, destination.tracker.getMaxTransferable());

        // apply input constraints
        toMove = Math.min(toMove, source.tracker.getMaxTransferable());

        // apply resource constraints
        toMove = Math.min(toMove, source.type.getMaxStackSize(potential));
        if (toMove <= 0) return;

        // extract item for real
        STACK extracted = source.extract(toMove, false);
        // insert item for real
        remainder = destination.insert(extracted, false);
        // track transfer amounts
        source.tracker.trackTransfer(toMove);
        destination.tracker.trackTransfer(toMove);

        // if remainder exists, someone lied.
        // this should never happen
        // will void items if it does
        if (!destination.type.isEmpty(remainder)) {
            SFM.LOGGER.error(
                    "Failed to move all promised items, took {} but had {} left over after insertion.",
                    extracted,
                    remainder
            );
        }
    }

    public static void releaseSlots(List<LimitedOutputSlot> slots) {
        SLOT_POOL.release(slots);
    }

    public static void releaseSlot(LimitedOutputSlot slot) {
        SLOT_POOL.release(slot);
    }

    @Override
    public void tick(ProgramContext context) {
        // gather the input slots from all the input statements
        List<LimitedInputSlot> inputSlots = new ArrayList<>(lastInputCapacity);
        for (var inputStatement : context.getInputs()) {
            inputStatement.gatherSlots(context, inputSlots::add);
        }
        lastInputCapacity = inputSlots.size();

        // collect the output slots
        List<LimitedOutputSlot> outputSlots = new ArrayList<>(lastOutputCapacity);
        gatherSlots(context, outputSlots::add);
        lastOutputCapacity = outputSlots.size();

        // try and move resources from input slots to output slots
        for (var in : inputSlots) {
            if (in.isDone()) {
                InputStatement.releaseSlot(in);
                continue;
            }
            var outIt = outputSlots.iterator();
            while (outIt.hasNext()) {
                var out = outIt.next();
                if (out.isDone()) {
                    outIt.remove();
                    OutputStatement.releaseSlot(out);
                    if (outputSlots.isEmpty()) return;
                    continue;
                }
                moveTo(in, out);
            }
        }

        OutputStatement.releaseSlots(outputSlots);
        InputStatement.releaseSlots(inputSlots);
    }

    /**
     * The output statement contains labels.
     * Each block in the world can have more than one label.
     * Each block can have a block entity.
     * Each block entity can have 0 or more slots.
     * <p>
     * We want collect the slots from all the labelled blocks.
     */
    public void gatherSlots(ProgramContext context, Consumer<LimitedOutputSlot<?, ?, ?>> acceptor) {
        // find all the types referenced in the output statement
        Stream<ResourceType> types = RESOURCE_LIMITS
                .resourceLimits()
                .stream()
                .map(ResourceLimit::resourceId)
                .map((ResourceIdentifier x) -> x.getResourceType())
                .distinct();

        if (!EACH) {
            // create a single matcher to be shared by all capabilities
            List<OutputResourceTracker<?, ?, ?>> outputTracker = RESOURCE_LIMITS.createOutputTrackers();
            for (var type : (Iterable<ResourceType>) types::iterator) {
                for (var cap : (Iterable<?>) type.getCapabilities(context, LABEL_ACCESS)::iterator) {
                    gatherSlots((ResourceType<Object, Object, Object>) type, cap, outputTracker, acceptor);
                }
            }
        } else {
            for (var type : (Iterable<ResourceType>) types::iterator) {
                for (var cap : (Iterable<?>) type.getCapabilities(context, LABEL_ACCESS)::iterator) {
                    List<OutputResourceTracker<?, ?, ?>> outputMatchers = RESOURCE_LIMITS.createOutputTrackers();
                    gatherSlots((ResourceType<Object, Object, Object>) type, cap, outputMatchers, acceptor);
                }
            }
        }
    }

    private <STACK, ITEM, CAP> void gatherSlots(
            ResourceType<STACK, ITEM, CAP> type,
            CAP capability,
            List<OutputResourceTracker<?, ?, ?>> trackers,
            Consumer<LimitedOutputSlot<?, ?, ?>> acceptor
    ) {
        for (int slot = 0; slot < type.getSlots(capability); slot++) {
            if (LABEL_ACCESS.slots().contains(slot)) {
                for (OutputResourceTracker<?, ?, ?> tracker : trackers) {
                    if (tracker.matchesCapabilityType(capability)) {
                        acceptor.accept(SLOT_POOL.acquire(
                                capability,
                                slot,
                                (OutputResourceTracker<STACK, ITEM, CAP>) tracker
                        ));
                    }
                }
            }
        }
    }
}
