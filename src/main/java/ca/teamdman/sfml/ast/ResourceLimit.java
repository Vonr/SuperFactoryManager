package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.InputResourceTracker;
import ca.teamdman.sfm.common.program.OutputResourceTracker;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static ca.teamdman.sfml.ast.ResourceQuantity.IdExpansionBehaviour.EXPAND;
import static ca.teamdman.sfml.ast.ResourceQuantity.IdExpansionBehaviour.NO_EXPAND;

public record ResourceLimit<STACK, ITEM, CAP>(
        Limit limit,
        ResourceIdentifier<STACK, ITEM, CAP> resourceId
) implements ASTNode, Predicate<Object> {
    public static final ResourceLimit<?, ?, ?> TAKE_ALL_LEAVE_NONE = new ResourceLimit<>(
            Limit.MAX_QUANTITY_NO_RETENTION,
            ResourceIdentifier.MATCH_ALL
    );
    public static final ResourceLimit<?, ?, ?> ACCEPT_ALL_WITHOUT_RESTRAINT = new ResourceLimit<>(
            Limit.MAX_QUANTITY_MAX_RETENTION,
            ResourceIdentifier.MATCH_ALL
    );

    public ResourceLimit(ResourceIdentifier<STACK, ITEM, CAP> resourceId) {
        this(new Limit(), resourceId);
    }

    public ResourceLimit<STACK, ITEM, CAP> withDefaults(long quantity, long retention) {
        return new ResourceLimit<>(limit.withDefaults(quantity, retention), resourceId);
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
                                new ResourceLimit<>(limit, rid),
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
                                new ResourceLimit<>(limit, rid),
                                exclusions,
                                new AtomicLong(0),
                                retention
                        )));
            } else if (limit.retention().idExpansionBehaviour() == EXPAND) {
                // no sharing, multiple trackers
                resourceId
                        .expand()
                        .forEach(rid -> gatherer.accept(new InputResourceTracker<>(
                                new ResourceLimit<>(limit, rid),
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
                // no sharing
                gatherer.accept(new OutputResourceTracker<>(this, exclusions, new AtomicLong(0), new AtomicLong(0)));
            } else if (limit.retention().idExpansionBehaviour() == EXPAND) {
                // retention count is shared
                AtomicLong retained = new AtomicLong(0);
                resourceId
                        .expand()
                        .forEach(rid -> gatherer.accept(new OutputResourceTracker<>(
                                new ResourceLimit<>(limit, rid),
                                exclusions,
                                new AtomicLong(0),
                                retained
                        )));
            }
        } else if (limit.quantity().idExpansionBehaviour() == EXPAND) {
            if (limit.retention().idExpansionBehaviour() == NO_EXPAND) {
                // quantity count is shared
                AtomicLong quantity = new AtomicLong(0);
                resourceId
                        .expand()
                        .forEach(rid -> gatherer.accept(new OutputResourceTracker<>(
                                new ResourceLimit<>(limit, rid),
                                exclusions,
                                quantity,
                                new AtomicLong(0)
                        )));
            } else if (limit.retention().idExpansionBehaviour() == EXPAND) {
                // both counts are shared
                AtomicLong quantity = new AtomicLong(0);
                AtomicLong retained = new AtomicLong(0);
                resourceId
                        .expand()
                        .forEach(rid -> gatherer.accept(new OutputResourceTracker<>(
                                new ResourceLimit<>(limit, rid),
                                exclusions,
                                quantity,
                                retained
                        )));
            }
        }
    }

    public boolean test(Object stack) {
        return resourceId.test(stack);
    }
}
