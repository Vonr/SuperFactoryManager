package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.InputResourceTracker;
import ca.teamdman.sfm.common.program.OutputResourceTracker;
import ca.teamdman.sfm.common.resourcetype.ResourceType;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static ca.teamdman.sfml.ast.ResourceQuantity.IdExpansionBehaviour.EXPAND;
import static ca.teamdman.sfml.ast.ResourceQuantity.IdExpansionBehaviour.NO_EXPAND;

public record ResourceLimit(
        ResourceIdSet resourceIds,
        Limit limit,
        With with
) implements ASTNode, Predicate<Object> {
    public static final ResourceLimit TAKE_ALL_LEAVE_NONE = new ResourceLimit(
            ResourceIdSet.MATCH_ALL,
            Limit.MAX_QUANTITY_NO_RETENTION,
            With.ALWAYS_TRUE
    );
    public static final ResourceLimit ACCEPT_ALL_WITHOUT_RESTRAINT = new ResourceLimit(
            ResourceIdSet.MATCH_ALL,
            Limit.MAX_QUANTITY_MAX_RETENTION,
            With.ALWAYS_TRUE
    );

    public ResourceLimit withDefaultLimit(Limit defaults) {
        return new ResourceLimit(resourceIds, limit.withDefaults(defaults), with);
    }

    public ResourceLimit withLimit(Limit limit) {
        return new ResourceLimit(resourceIds, limit, with);
    }

    public void gatherInputTrackers(
            Consumer<InputResourceTracker> gatherer,
            ResourceIdSet exclusions
    ) {
        if (limit.quantity().idExpansionBehaviour() == NO_EXPAND) {
            if (limit.retention().idExpansionBehaviour() == NO_EXPAND) {
                // no sharing, single tracker
                gatherer.accept(new InputResourceTracker(
                        this,
                        exclusions,
                        new AtomicLong(0),
                        new AtomicLong(0)
                ));
            } else if (limit.retention().idExpansionBehaviour() == EXPAND) {
                // expand retention
                // share quantity
                AtomicLong quantity = new AtomicLong(0);
                resourceIds
                        .stream()
                        .map(ResourceIdentifier::expand)
                        .flatMap(List::stream)
                        .forEach(rid -> gatherer.accept(new InputResourceTracker(
                                new ResourceLimit(new ResourceIdSet(List.of(rid)), limit, with),
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
                resourceIds
                        .stream()
                        .map(ResourceIdentifier::expand)
                        .flatMap(List::stream)
                        .forEach(rid -> gatherer.accept(new InputResourceTracker(
                                new ResourceLimit(new ResourceIdSet(List.of(rid)), limit, with),
                                exclusions,
                                new AtomicLong(0),
                                retention
                        )));
            } else if (limit.retention().idExpansionBehaviour() == EXPAND) {
                // no sharing, multiple trackers
                resourceIds
                        .stream()
                        .map(ResourceIdentifier::expand)
                        .flatMap(List::stream)
                        .forEach(rid -> gatherer.accept(new InputResourceTracker(
                                new ResourceLimit(new ResourceIdSet(List.of(rid)), limit, with),
                                exclusions,
                                new AtomicLong(0),
                                new AtomicLong(0)
                        )));
            }
        }
    }

    public void gatherOutputTrackers(
            Consumer<OutputResourceTracker> gatherer,
            ResourceIdSet exclusions
    ) {
        if (limit.quantity().idExpansionBehaviour() == NO_EXPAND) {
            if (limit.retention().idExpansionBehaviour() == NO_EXPAND) {
                // single tracker
                gatherer.accept(new OutputResourceTracker(this, exclusions, new AtomicLong(0), new AtomicLong(0)));
            } else if (limit.retention().idExpansionBehaviour() == EXPAND) {
                // tracker for each retention, sharing quantity
                AtomicLong quantity = new AtomicLong(0);
                resourceIds
                        .stream()
                        .map(ResourceIdentifier::expand)
                        .flatMap(List::stream)
                        .forEach(rid -> gatherer.accept(new OutputResourceTracker(
                                new ResourceLimit(new ResourceIdSet(List.of(rid)), limit, with),
                                exclusions,
                                quantity,
                                new AtomicLong(0)
                        )));
            }
        } else if (limit.quantity().idExpansionBehaviour() == EXPAND) {
            if (limit.retention().idExpansionBehaviour() == NO_EXPAND) {
                // tracker for each quantity, sharing retention
                AtomicLong retained = new AtomicLong(0);
                resourceIds
                        .stream()
                        .map(ResourceIdentifier::expand)
                        .flatMap(List::stream)
                        .forEach(rid -> gatherer.accept(new OutputResourceTracker(
                                new ResourceLimit(new ResourceIdSet(List.of(rid)), limit, with),
                                exclusions,
                                new AtomicLong(0),
                                retained
                        )));
            } else if (limit.retention().idExpansionBehaviour() == EXPAND) {
                // expand both quantity and retention, no sharing
                resourceIds
                        .stream()
                        .map(ResourceIdentifier::expand)
                        .flatMap(List::stream)
                        .forEach(rid -> gatherer.accept(new OutputResourceTracker(
                                new ResourceLimit(new ResourceIdSet(List.of(rid)), limit, with),
                                exclusions,
                                new AtomicLong(0),
                                new AtomicLong(0)
                        )));
            }
        }
    }

    @Override
    public boolean test(Object stack) {
        var matchingIdPattern = resourceIds.getMatchingFromStack(stack);
        if (matchingIdPattern == null) {
            return false;
        }
        @SuppressWarnings("unchecked")
        ResourceType<Object, ?, ?> resourceType = (ResourceType<Object, ?, ?>) matchingIdPattern.getResourceType();
        if (resourceType == null) {
            return false;
        }
        return with.test(resourceType, stack);
    }

    @Override
    public String toString() {
        return limit + " " + resourceIds + (with == With.ALWAYS_TRUE ? "" : " WITH " + with);
    }

    public String toStringCondensed(Limit defaults) {
        return (
                limit.toStringCondensed(defaults) + " " + resourceIds.toStringCondensed() + (
                        with == With.ALWAYS_TRUE
                        ? ""
                        : " WITH " + with
                )
        ).trim();
    }
}
