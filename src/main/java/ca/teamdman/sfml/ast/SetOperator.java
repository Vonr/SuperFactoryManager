package ca.teamdman.sfml.ast;

import java.util.List;
import java.util.Locale;
import java.util.function.BiPredicate;

public enum SetOperator implements ASTNode, BiPredicate<Boolean, List<Boolean>>, ToStringPretty {
    OVERALL((overall, __) -> overall),
    SOME((__, set) -> set.stream().anyMatch(Boolean::booleanValue)),
    EVERY((__, set) -> set.stream().allMatch(Boolean::booleanValue)),
    ONE((__, set) -> set.stream().filter(Boolean::booleanValue).count() == 1),
    LONE((__, set) -> set.stream().filter(Boolean::booleanValue).count() <= 1);

    private final BiPredicate<Boolean, List<Boolean>> PRED;

    SetOperator(BiPredicate<Boolean, List<Boolean>> pred) {
        this.PRED = pred;
    }

    public static SetOperator from(String text) {
        text = text.toUpperCase(Locale.ROOT);
        if (text.equals("EACH")) {
            text = "EVERY";
        }
        return SetOperator.valueOf(text);
    }

    @Override
    public boolean test(Boolean overall, List<Boolean> counts) {
        return PRED.test(overall, counts);
    }


    @Override
    public String toString() {
        return this.name().toUpperCase(Locale.ROOT);
    }
}
