package ca.teamdman.sfm.common.util;

/**
 * <a href="https://chat.openai.com/share/d9f474ba-da9e-470a-a79b-cae6c665ef1e">Original conversation for this code</a>
 */

import ca.teamdman.sfml.ast.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ControlFlowGraph {
    private final Program program;
    private final Consumer<Triplet> callback;

    public ControlFlowGraph(Program program, Consumer<Triplet> callback) {
        this.program = program;
        this.callback = callback;
    }

    public void traverse() {
        for (Trigger trigger : program.triggers()) {
            traverseBlock(trigger.getBlock(), new ArrayList<>(), new ArrayList<>());
        }
    }

    private void traverseBlock(Block block, List<InputStatement> inputs, List<Pair<IfStatement, Integer>> branches) {
        for (Statement statement : block.statements()) {
            if (statement instanceof InputStatement) {
                inputs.add((InputStatement) statement);
                continue;
            }
            if (statement instanceof IfStatement ifStatement) {
                for (int i = 0; i < ifStatement.expressions().size(); i++) {
                    branches.add(new Pair<>(ifStatement, i));
                    traverseBlock(ifStatement.blocks().get(i), new ArrayList<>(inputs), new ArrayList<>(branches));
                    branches.remove(branches.size() - 1);
                }
                continue;
            }
            if (statement instanceof OutputStatement) {
                callback.accept(new Triplet(
                        new ArrayList<>(inputs),
                        (OutputStatement) statement,
                        new ArrayList<>(branches)
                ));
            }
        }
    }

    public static class Triplet {
        public final List<InputStatement> inputs;
        public final OutputStatement output;
        public final List<Pair<IfStatement, Integer>> branches;

        public Triplet(List<InputStatement> inputs, OutputStatement output, List<Pair<IfStatement, Integer>> branches) {
            this.inputs = inputs;
            this.output = output;
            this.branches = branches;
        }
    }

    public static class Pair<T, U> {
        public final T first;
        public final U second;

        public Pair(T first, U second) {
            this.first = first;
            this.second = second;
        }
    }
}
