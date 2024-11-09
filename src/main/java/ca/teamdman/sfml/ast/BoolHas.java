package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.LabelPositionHolder;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.resourcetype.ResourceType;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public record BoolHas(
        SetOperator setOperator,
        LabelAccess labelAccess,
        ComparisonOperator comparisonOperator,
        long quantity,
        ResourceIdSet resourceIdSet,
        With with,
        ResourceIdSet except
) implements BoolExpr {

    @Override
    public boolean test(ProgramContext programContext) {
        AtomicLong overallCount = new AtomicLong(0);
        List<Boolean> satisfactionResults = new ArrayList<>();
        LabelPositionHolder labelPositionHolder = programContext.getLabelPositionHolder();
        ArrayList<Pair<Label, BlockPos>> labelledPositions = labelAccess.getLabelledPositions(labelPositionHolder);
        for (Pair<Label, BlockPos> entry : labelledPositions) {
            BlockPos pos = entry.getSecond();
            AtomicLong inThisInv = new AtomicLong(0);
            for (var resourceId : resourceIdSet.unsafeGetIdentifiers()) {
                var resourceType = resourceId.getResourceType();
                if (resourceType == null) {
                    continue;
                }
                accumulate(
                        programContext,
                        pos,
                        overallCount,
                        inThisInv,
                        resourceType
                );
            }
            satisfactionResults.add(comparisonOperator.test(inThisInv.get(), quantity));
        }

        var isOverallSatisfied = this.comparisonOperator.test(overallCount.get(), this.quantity);
        return setOperator.test(isOverallSatisfied, satisfactionResults);
    }

    @Override
    public String toString() {
        return setOperator
               + " "
               + labelAccess
               + " HAS "
               + comparisonOperator
               + " "
               + quantity
               + " "
               + resourceIdSet.toStringCondensed()
               + (with == With.ALWAYS_TRUE ? "" : " " + with.toStringPretty())
               + (except.isEmpty() ? "" : " EXCEPT " + except.toStringCondensed());
    }

    private <STACK, ITEM, CAP> void accumulate(
            ProgramContext programContext,
            BlockPos pos,
            AtomicLong overallAccumulator,
            AtomicLong invAccumulator,
            ResourceType<STACK, ITEM, CAP> resourceType
    ) {
        resourceType.forEachDirectionalCapability(
                programContext,
                labelAccess.directions(),
                pos,
                (direction, cap) -> resourceType.getStacksInSlots(cap, labelAccess.slots()).forEach(stack -> {
                    if (this.resourceIdSet.getMatchingFromStack(stack) != null) {
                        if (with.matchesStack(resourceType, stack)) {
                            long amount = resourceType.getAmount(stack);
                            invAccumulator.addAndGet(amount);
                            overallAccumulator.addAndGet(amount);
                        }
                    }
                })
        );
    }
}
