package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.InputResourceTracker;
import ca.teamdman.sfm.common.program.OutputResourceTracker;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static ca.teamdman.sfml.ast.ResourceQuantity.IdExpansionBehaviour.EXPAND;
import static ca.teamdman.sfml.ast.ResourceQuantity.IdExpansionBehaviour.NO_EXPAND;

public record ResourceLimit<STACK, ITEM, CAP>(
        ResourceIdentifier<STACK, ITEM, CAP> resourceId,
        Limit limit
) implements ASTNode, Predicate<Object> {
    public static final ResourceLimit<?, ?, ?> TAKE_ALL_LEAVE_NONE = new ResourceLimit<>(
            ResourceIdentifier.MATCH_ALL, Limit.MAX_QUANTITY_NO_RETENTION
    );
    public static final ResourceLimit<?, ?, ?> ACCEPT_ALL_WITHOUT_RESTRAINT = new ResourceLimit<>(
            ResourceIdentifier.MATCH_ALL, Limit.MAX_QUANTITY_MAX_RETENTION
    );

    public ResourceLimit<STACK, ITEM, CAP> withDefaults(Limit defaults) {
        return new ResourceLimit<>(resourceId, limit.withDefaults(defaults));
    }

    public ResourceLimit<STACK, ITEM, CAP> withLimit(Limit limit) {
        return new ResourceLimit<>(resourceId, limit);
    }

    public void gatherInputTrackers(Consumer<InputResourceTracker<?, ?, ?>> gatherer, ResourceIdSet exclusions) {
        if (limit.quantity().idExpansionBehaviour() == NO_EXPAND) {
            if (limit.retention().idExpansionBehaviour() == NO_EXPAND) {
                // no sharing, single tracker
                gatherer.accept(new InputResourceTracker<>(
                        this,
                        exclusions,
                        new AtomicLong(0),
                        new AtomicLong(0)
                ));
            } else if (limit.retention().idExpansionBehaviour() == EXPAND) {
                // expand retention
                // share quantity
                AtomicLong quantity = new AtomicLong(0);
                resourceId
                        .expand()
                        .forEach(rid -> gatherer.accept(new InputResourceTracker<>(
                                new ResourceLimit<>(rid, limit),
                                exclusions,
                                quantity,
                                new AtomicLong(0)
                        )));
            }
        } else if (limit.quantity().idExpansionBehaviour() == EXPAND) {
            if (limit.retention().idExpansionBehaviour() == NO_EXPAND) {
                // expand quantity
                // share retention
                AtomicLong retention = new AtomicLong(0);
                resourceId
                        .expand()
                        .forEach(rid -> gatherer.accept(new InputResourceTracker<>(
                                new ResourceLimit<>(rid, limit),
                                exclusions,
                                new AtomicLong(0),
                                retention
                        )));
            } else if (limit.retention().idExpansionBehaviour() == EXPAND) {
                // no sharing, multiple trackers
                resourceId
                        .expand()
                        .forEach(rid -> gatherer.accept(new InputResourceTracker<>(
                                new ResourceLimit<>(rid, limit),
                                exclusions,
                                new AtomicLong(0),
                                new AtomicLong(0)
                        )));
            }
        }
    }

    public void gatherOutputTrackers(Consumer<OutputResourceTracker<?, ?, ?>> gatherer, ResourceIdSet exclusions) {
        if (limit.quantity().idExpansionBehaviour() == NO_EXPAND) {
            if (limit.retention().idExpansionBehaviour() == NO_EXPAND) {
                // single tracker
                gatherer.accept(new OutputResourceTracker<>(this, exclusions, new AtomicLong(0), new AtomicLong(0)));
            } else if (limit.retention().idExpansionBehaviour() == EXPAND) {
                // tracker for each retention, sharing quantity
                AtomicLong quantity = new AtomicLong(0);
                resourceId
                        .expand()
                        .forEach(rid -> gatherer.accept(new OutputResourceTracker<>(
                                new ResourceLimit<>(rid, limit),
                                exclusions,
                                quantity,
                                new AtomicLong(0)
                        )));
            }
        } else if (limit.quantity().idExpansionBehaviour() == EXPAND) {
            if (limit.retention().idExpansionBehaviour() == NO_EXPAND) {
                // tracker for each quantity, sharing retention
                AtomicLong retained = new AtomicLong(0);
                resourceId
                        .expand()
                        .forEach(rid -> gatherer.accept(new OutputResourceTracker<>(
                                new ResourceLimit<>(rid, limit),
                                exclusions,
                                new AtomicLong(0),
                                retained
                        )));
            } else if (limit.retention().idExpansionBehaviour() == EXPAND) {
                // expand both quantity and retention, no sharing
                resourceId
                        .expand()
                        .forEach(rid -> gatherer.accept(new OutputResourceTracker<>(
                                new ResourceLimit<>(rid, limit),
                                exclusions,
                                new AtomicLong(0),
                                new AtomicLong(0)
                        )));
            }
        }
    }

    @Override
    public boolean test(Object stack) {
        return resourceId.test(stack);
    }

    @Override
    public String toString() {
        return limit + " " + resourceId;
    }

    public String toStringCondensed(Limit defaults) {
        return (limit.toStringCondensed(defaults) + " " + resourceId.toStringCondensed()).trim();
    }
}
