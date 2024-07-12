package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfml.ast.*;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimulateExploreAllPathsProgramBehaviour implements ProgramBehaviour {
    protected List<ExecutionPath> seenPaths = new ArrayList<>();
    protected ExecutionPath currentPath = new ExecutionPath();
    protected BigInteger triggerPathCount = BigInteger.ZERO;

    public SimulateExploreAllPathsProgramBehaviour() {
    }

    public void terminatePathAndBeginAnew() {
        seenPaths.add(currentPath);
        currentPath = new ExecutionPath();
        triggerPathCount = triggerPathCount.add(BigInteger.ONE);
    }

    public BigInteger getTriggerPathCount() {
        return triggerPathCount;
    }

    public void prepareNextTrigger() {
        triggerPathCount = BigInteger.ZERO;
    }

    public void pushPathElement(ExecutionPathElement statement) {
        currentPath.history.add(statement);
    }

    public void onOutputStatementExecution(OutputStatement outputStatement) {
        pushPathElement(new SimulateExploreAllPathsProgramBehaviour.IO(outputStatement));
    }

    public void onInputStatementExecution(InputStatement inputStatement) {
        pushPathElement(new SimulateExploreAllPathsProgramBehaviour.IO(inputStatement));
    }

    public void onInputStatementForgetTransform(InputStatement old, InputStatement next) {
    }

    public void onInputStatementDropped(InputStatement inputStatement) {
    }


    public void onTriggerDropped(ProgramContext context) {
        context.getInputs().forEach(this::onInputStatementDropped);
    }

    @Override
    public ProgramBehaviour fork() {
        var copy = new SimulateExploreAllPathsProgramBehaviour();
        copy.seenPaths = this.seenPaths; // share the reference
        copy.currentPath = this.currentPath.fork();
        return this;
    }

    @Override
    public void free() {

    }

    public ExecutionPath getCurrentPath() {
        return currentPath;
    }

    public List<ExecutionPath> getSeenPaths() {
        return seenPaths;
    }

    public int[] getSeenIOStatementCountForEachPath() {
        return seenPaths
                .stream()
                .mapToInt(path -> (int) path.history.stream().filter(IO.class::isInstance).count())
                .toArray();
    }

    public void onProgramFinished(Program program) {

    }


    public enum IOKind {
        INPUT,
        OUTPUT
    }

    public interface ExecutionPathElement {
    }

    public record ExecutionPath(
            List<ExecutionPathElement> history
    ) {
        public ExecutionPath() {
            this(new ArrayList<>());
        }

        public ExecutionPath fork() {
            return new ExecutionPath(new ArrayList<>(history));
        }

        public Stream<ExecutionPathElement> stream() {
            return history.stream();
        }

        public Stream<Branch> streamBranches() {
            return history.stream().filter(Branch.class::isInstance).map(Branch.class::cast);
        }

        public Stream<IO> streamInputs() {
            return history
                    .stream()
                    .filter(IO.class::isInstance)
                    .map(IO.class::cast)
                    .filter(io -> io.kind == IOKind.INPUT);
        }

        public Stream<IO> streamOutputs() {
            return history
                    .stream()
                    .filter(IO.class::isInstance)
                    .map(IO.class::cast)
                    .filter(io -> io.kind == IOKind.OUTPUT);
        }
    }

    public record Branch(
            IfStatement ifStatement,
            boolean wasTrue
    ) implements ExecutionPathElement {
    }

    @SuppressWarnings("rawtypes")
    public record IO(
            IOKind kind,
            Set<ResourceType> usedResourceTypes,
            Set<Label> usedLabels
    ) implements ExecutionPathElement {
        public IO(IOStatement statement) {
            //noinspection DataFlowIssue
            this(
                    statement instanceof InputStatement
                    ? IOKind.INPUT
                    : (statement instanceof OutputStatement ? IOKind.OUTPUT : null),
                    statement.resourceLimits().getReferencedResourceTypes().collect(Collectors.toSet()),
                    new HashSet<>(statement.labelAccess().labels())
            );
            if (kind == null) {
                throw new IllegalArgumentException("Unknown IO statement type: " + statement);
            }
        }
    }

    public record Warning(
            TranslatableContents message
    ) implements ExecutionPathElement {
    }
}
