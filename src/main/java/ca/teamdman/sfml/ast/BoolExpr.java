package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ProgramContext;
import net.minecraftforge.items.IItemHandler;

import java.util.function.Predicate;

public record BoolExpr(
        Predicate<ProgramContext> pred
) implements Predicate<ProgramContext>, ASTNode {
    public static BoolExpr from(LabelAccess labelAccess, ItemComparer itemComparer) {
        return new BoolExpr(context -> {
            var handlers = context.getItemHandlersByLabels(labelAccess);
            for (var inv : (Iterable<IItemHandler>) handlers::iterator) {
                for (int slot = 0; slot < inv.getSlots(); slot++) {
                    if (labelAccess.slots().contains(slot)) {
                        var stack = inv.getStackInSlot(slot);
                        if (itemComparer.test(stack)) return true;
                    }
                }
            }
            return false;
        });
    }

    @Override
    public boolean test(ProgramContext context) {
        return pred.test(context);
    }
}
