package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.program.ProgramContext;

import java.util.function.Predicate;

public record Condition(

) implements Predicate<ProgramContext> {
    @Override
    public boolean test(ProgramContext context) {
        return false;
    }
}
