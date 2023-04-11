package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.program.LimitedInputSlot;
import ca.teamdman.sfm.common.program.LimitedOutputSlot;
import ca.teamdman.sfm.common.program.OutputResourceTracker;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.resourcetype.ResourceType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public record OutputStatement(
        LabelAccess labelAccess,
        ResourceLimits resourceLimits,
        boolean each
) implements Statement {

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
            LimitedInputSlot<STACK, ITEM, CAP> source,
            LimitedOutputSlot<STACK, ITEM, CAP> destination
    ) {
        // we need to simulate since there are some types of slots we can't undo an extract from
        // you can't put something back in the output slot of a furnace
        var potential = source.extract(Long.MAX_VALUE, true);
        if (source.TYPE.isEmpty(potential)) {
            source.setDone();
            return;
        }
        if (!source.TRACKER.test(potential)) return;
        if (!destination.TRACKER.test(potential)) return;
        var remainder = destination.insert(potential, true);

        // how many can we move unrestrained
        var toMove = source.TYPE.getCount(potential) - source.TYPE.getCount(remainder);
        if (toMove == 0) return;

        // how many have we promised to leave in this slot
        toMove -= source.TRACKER.getExistingPromise(source.SLOT);

        // how many more need to be promised
        var toPromise = source.TRACKER.getRemainingPromise();
        toPromise = Long.min(toMove, toPromise);
        toMove -= toPromise;

        // track the promise
        source.TRACKER.track(source.SLOT, 0, toPromise);

        // if whole slot has been promised, mark done
        if (toMove == 0) {
            source.setDone();
            return;
        }

        // how many are we allowed to put in the other inventory
        toMove = Math.min(toMove, destination.TRACKER.getMaxTransferable());

        // how many can we move at once
        toMove = Math.min(toMove, source.TRACKER.getMaxTransferable());
        if (toMove <= 0) return;

        // extract item for real
        var extracted = source.TYPE.extract(source.HANDLER, source.SLOT, toMove, false);
        // insert item for real
        remainder = destination.TYPE.insert(destination.HANDLER, destination.SLOT, extracted, false);
        // track transfer amounts
        source.TRACKER.trackTransfer(toMove);
        destination.TRACKER.trackTransfer(toMove);

        // if remainder exists, someone lied.
        if (!destination.TYPE.isEmpty(remainder)) {
            SFM.LOGGER.error(
                    "Failed to move all promised items, took {} but had {} left over after insertion.",
                    extracted,
                    remainder
            );
        }
    }

    @Override
    public void tick(ProgramContext context) {
        // gather the input slots from all the input statements
        var inputSlots = context.getInputs().flatMap(in -> in.getSlots(context));

        // collect the output slots
        var outputSlots = this.getSlots(context).toArray(LimitedOutputSlot[]::new);

        // try and move resources from input slots to output slots
        grabbing:
        for (LimitedInputSlot in : (Iterable<? extends LimitedInputSlot<?, ?, ?>>) inputSlots::iterator) {
            for (LimitedOutputSlot out : outputSlots) {
                if (in.TYPE.equals(out.TYPE)) {
                    moveTo(in, out);
                    if (in.isDone()) continue grabbing;
                }
            }
        }
    }

    /**
     * The output statement contains labels.
     * Each block in the world can have more than one label.
     * Each block can have a block entity.
     * Each block entity can have 0 or more slots.
     * <p>
     * We want collect the slots from all the labelled blocks.
     */
    public Stream<LimitedOutputSlot<?, ?, ?>> getSlots(ProgramContext context) {
        Stream.Builder<LimitedOutputSlot<?, ?, ?>> rtn = Stream.builder();

        // find all the types referenced in the output statement
        Stream<ResourceType> types = resourceLimits
                .resourceLimits()
                .stream()
                .map(ResourceLimit::resourceId)
                .distinct()
                .map(ResourceIdentifier::getResourceType);

        if (each) {
            for (var type : (Iterable<ResourceType>) types::iterator) {
                for (var cap : (Iterable<?>) type.getCapabilities(context, labelAccess)::iterator) {
                    // create a new matcher for each capability
                    List<OutputResourceTracker<?, ?, ?>> outputMatchers = resourceLimits.createOutputTrackers();
//                    if (type.matchesCapType(cap)) {
                    getSlots((ResourceType<Object, Object, Object>) type, cap, outputMatchers).forEach(rtn::add);
//                    }
                }
            }
        } else {
            // create a single matcher to be shared by all capabilities
            List<OutputResourceTracker<?, ?, ?>> outputTracker = resourceLimits.createOutputTrackers();
            for (var type : (Iterable<ResourceType>) types::iterator) {
                for (var cap : (Iterable<?>) type.getCapabilities(context, labelAccess)::iterator) {
//                    if (type.matchesCapType(cap)) {
                    getSlots((ResourceType<Object, Object, Object>) type, cap, outputTracker).forEach(rtn::add);
//                    }
                }
            }
        }
        return rtn.build();
    }

    private <STACK, ITEM, CAP> List<LimitedOutputSlot<STACK, ITEM, CAP>> getSlots(
            ResourceType<STACK, ITEM, CAP> type,
            CAP capability,
            List<OutputResourceTracker<?, ?, ?>> trackers
    ) {
        List<LimitedOutputSlot<STACK, ITEM, CAP>> rtn = new ArrayList<>();
        for (int slot = 0; slot < type.getSlots(capability); slot++) {
            if (labelAccess.slots().contains(slot)) {
                // the destination is allowed to be empty, don't check for empty slot
                for (OutputResourceTracker<?, ?, ?> tracker : trackers) {
                    if (tracker.getLimit().resourceId().getResourceType().matchesCapabilityType(capability)) {
                        // doesn't matter if destination slot is empty
                        // doesn't let us short circuit like it does for input slots
                        var x = new LimitedOutputSlot<>(
                                this,
                                capability,
                                slot,
                                (OutputResourceTracker<STACK, ITEM, CAP>) tracker
                        );
                        rtn.add(x);
                    }
                }
            }
        }
        return rtn;
    }
}
