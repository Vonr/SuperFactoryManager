package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.program.*;
import ca.teamdman.sfm.common.registry.SFMResourceTypes;
import ca.teamdman.sfm.common.resourcetype.ResourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OutputStatement implements Statement {
    private final LabelAccess labelAccess;
    private final ResourceLimits resourceLimits;
    private final boolean each;

    private int lastInputCapacity = 32;
    private int lastOutputCapacity = 32;

    public OutputStatement(
            LabelAccess labelAccess,
            ResourceLimits resourceLimits,
            boolean each
    ) {
        this.labelAccess = labelAccess;
        this.resourceLimits = resourceLimits;
        this.each = each;
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
        long toMove = source.type.getAmount(potential) - source.type.getAmount(remainder);
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
        STACK extracted = source.extract(toMove);
        // insert item for real
        remainder = destination.insert(extracted, false);
        var moved = source.type.getAmount(extracted) - source.type.getAmount(remainder);
        // track transfer amounts
        source.tracker.trackTransfer(moved);
        destination.tracker.trackTransfer(moved);

        // if remainder exists, someone lied.
        // this should never happen
        // will void items if it does
        if (!destination.type.isEmpty(remainder)) {
            SFM.LOGGER.error(
                    "Failed to move all promised items, found {} {}:{}, took {} but had {} left over after insertion. Resource loss may have occurred!!!",
                    potential,
                    SFMResourceTypes.DEFERRED_TYPES.get().getKey(source.type),
                    destination.type.getRegistryKey(potential),
                    extracted,
                    remainder
            );
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void tick(ProgramContext context) {
        if (context.getExecutionPolicy() == ProgramContext.ExecutionPolicy.EXPLORE_BRANCHES) return;
        // gather the input slots from all the input statements, +27 to hopefully avoid resizing
        List<LimitedInputSlot> inputSlots = new ArrayList<>(lastInputCapacity + 27);
        for (var inputStatement : context.getInputs()) {
            inputStatement.gatherSlots(context, inputSlots::add);
        }
        if (inputSlots.isEmpty()) return; // stop if we have nothing to move
        lastInputCapacity = inputSlots.size();

        // collect the output slots, +27 to hopefully avoid resizing
        List<LimitedOutputSlot> outputSlots = new ArrayList<>(lastOutputCapacity + 27);
        gatherSlots(context, outputSlots::add);
        lastOutputCapacity = outputSlots.size();

        // try and move resources from input slots to output slots
        var inIt = inputSlots.iterator();
        while (inIt.hasNext()) {
            var in = inIt.next();
            if (in.isDone()) { // this slot is no longer useful
                inIt.remove(); // ensure we only release slots once
                InputStatement.releaseSlot(in); // release the slot to the object pool
                continue;
            }
            var outIt = outputSlots.iterator();
            while (outIt.hasNext()) {
                var out = outIt.next();
                if (out.isDone()) { // this slot is no longer useful
                    outIt.remove(); // ensure we only release slots once
                    LimitedOutputSlotObjectPool.INSTANCE.release(out); // release the slot to the object pool
                    continue;
                }
                moveTo(in, out); // move the contents from the "in" slot to the "out" slot
                if (in.isDone()) break; // stop processing output slots if we have nothing to move
            }
            if (outputSlots.isEmpty()) break; // stop processing input slots if we have no output slots
        }

        LimitedOutputSlotObjectPool.INSTANCE.release(outputSlots);
        InputStatement.releaseSlots(inputSlots);
    }

    /**
     * The output statement contains labels.
     * Each block in the world can have more than one programString.
     * Each block can have a block entity.
     * Each block entity can have 0 or more slots.
     * <p>
     * We want collect the slots from all the labelled blocks.
     */
    @SuppressWarnings({"rawtypes", "unchecked"}) // basically impossible to make this method generic safe
    public void gatherSlots(ProgramContext context, Consumer<LimitedOutputSlot<?, ?, ?>> acceptor) {
        // find all the types referenced in the output statement
        Stream<ResourceType> types = resourceLimits
                .resourceLimits()
                .stream()
                .map(ResourceLimit::resourceId)
                .map((ResourceIdentifier x) -> x.getResourceType())
                .distinct();

        if (!each) {
            // create a single matcher to be shared by all capabilities
            List<OutputResourceTracker<?, ?, ?>> outputTracker = resourceLimits.createOutputTrackers();
            for (var type : (Iterable<ResourceType>) types::iterator) {
                for (var cap : (Iterable<?>) type.getCapabilities(context, labelAccess)::iterator) {
                    gatherSlots((ResourceType<Object, Object, Object>) type, cap, outputTracker, acceptor);
                }
            }
        } else {
            for (var type : (Iterable<ResourceType>) types::iterator) {
                for (var cap : (Iterable<?>) type.getCapabilities(context, labelAccess)::iterator) {
                    List<OutputResourceTracker<?, ?, ?>> outputTracker = resourceLimits.createOutputTrackers();
                    gatherSlots((ResourceType<Object, Object, Object>) type, cap, outputTracker, acceptor);
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
            if (labelAccess.slots().contains(slot)) {
                for (OutputResourceTracker<?, ?, ?> tracker : trackers) {
                    if (tracker.matchesCapabilityType(capability)) {
                        //noinspection unchecked
                        acceptor.accept(LimitedOutputSlotObjectPool.INSTANCE.acquire(
                                capability,
                                slot,
                                (OutputResourceTracker<STACK, ITEM, CAP>) tracker
                        ));
                    }
                }
            }
        }
    }

    public LabelAccess labelAccess() {
        return labelAccess;
    }

    public ResourceLimits resourceLimits() {
        return resourceLimits;
    }

    public boolean each() {
        return each;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (OutputStatement) obj;
        return Objects.equals(this.labelAccess, that.labelAccess) &&
               Objects.equals(this.resourceLimits, that.resourceLimits) &&
               this.each == that.each;
    }

    @Override
    public int hashCode() {
        return Objects.hash(labelAccess, resourceLimits, each);
    }

    @Override
    public String toString() {
        return "OUTPUT " + resourceLimits + " TO " + (each ? "EACH " : "") + labelAccess;
    }

    public String toStringPretty() {
        StringBuilder sb = new StringBuilder();
        sb.append("OUTPUT");
        String rls = resourceLimits.toStringPretty(Limit.MAX_QUANTITY_MAX_RETENTION);
        if (rls.lines().count() > 1) {
            sb.append("\n");
            sb.append(rls.lines().map(s -> "  " + s).collect(Collectors.joining("\n")));
            sb.append("\n");
        } else {
            sb.append(" ");
            sb.append(rls);
            sb.append(" ");
        }
        sb.append("TO ");
        sb.append(each ? "EACH " : "");
        sb.append(labelAccess);
        return sb.toString();
    }
}
