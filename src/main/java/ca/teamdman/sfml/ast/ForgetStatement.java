package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.program.ProgramContext;
import ca.teamdman.sfm.common.program.SimulateExploreAllPathsProgramBehaviour;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// todo: optimize for forget-all case
public record ForgetStatement(
        List<Label> labels
) implements Statement {
    @Override
    public void tick(ProgramContext context) {
//        List<InputStatement> newInputs = new ArrayList<>();
//        for (InputStatement oldInputStatement : context.getInputs()) {
//            var newLabels = oldInputStatement.labelAccess().labels().stream()
//                    .filter(label -> !this.labels.contains(label))
//                    .toList();
//
//            // always fire event from old to new, even if new has no labels
//            InputStatement newInputStatement = new InputStatement(
//                    new LabelAccess(
//                            newLabels,
//                            oldInputStatement.labelAccess().directions(),
//                            oldInputStatement.labelAccess().slots(),
//                            oldInputStatement.labelAccess().roundRobin()
//                    ),
//                    oldInputStatement.resourceLimits(),
//                    oldInputStatement.each()
//            );
//            if (context.getBehaviour() instanceof SimulateExploreAllPathsProgramBehaviour simulation) {
//                simulation.onInputStatementForgetTransform(oldInputStatement, newInputStatement);
//                oldInputStatement.transferSlotsTo(newInputStatement);
//            }
//
//            // include the empty inputs, ensuring they get properly freed when the input statement is dropped
//            newInputs.add(newInputStatement);
//        }


        // map-replace existing inputs with ones that exclude the union of the label access
        context.free();
        var newInputs = context.getInputs()
                .stream()
                .map(input -> new InputStatement(
                        new LabelAccess(
                                input.labelAccess().labels().stream()
                                        .filter(label -> !this.labels.contains(label))
                                        .collect(Collectors.toList()),
                                input.labelAccess().directions(),
                                input.labelAccess().slots(),
                                input.labelAccess().roundRobin()
                        ),
                        input.resourceLimits(),
                        input.each()
                ))
                .filter(input -> !input.labelAccess().labels().isEmpty())
                .toList();

        context.getInputs().clear();
        context.getInputs().addAll(newInputs);
        context.getLogger().debug(x -> x.accept(Constants.LocalizationKeys.LOG_PROGRAM_TICK_FORGET_STATEMENT.get(
                labels.stream().map(Objects::toString).collect(Collectors.joining(", "))
        )));
    }

    @Override
    public String toString() {
        return "FORGET " + labels.stream().map(Objects::toString).collect(Collectors.joining(", "));
    }
}
