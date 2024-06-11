package ca.teamdman.sfml.ast;

import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.program.ProgramContext;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public record ForgetStatement(
        List<Label> labels
) implements Statement {
    @Override
    public void tick(ProgramContext context) {
        // map-replace existing inputs with ones that exclude the union of the label access
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
