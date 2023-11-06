package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfml.ast.IfStatement;
import ca.teamdman.sfml.ast.InputStatement;
import ca.teamdman.sfml.ast.Program;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class ProgramContext {
    private final Program PROGRAM;
    private final ManagerBlockEntity MANAGER;
    private final CableNetwork NETWORK;
    private final List<InputStatement> INPUTS = new ArrayList<>();
    private final Level LEVEL;
    private final ExecutionPolicy EXECUTION_POLICY;
    private final List<Branch> PATH_TAKEN = new ArrayList<>();
    private final int EXPLORATION_BRANCH_INDEX;
    private final int REDSTONE_PULSES;

    public ProgramContext(Program program, ManagerBlockEntity manager, ExecutionPolicy executionPolicy) {
        this(program, manager, executionPolicy, 0);
    }

    public ProgramContext(
            Program program,
            ManagerBlockEntity manager,
            ExecutionPolicy executionPolicy,
            int branchIndex
    ) {
        this.PROGRAM = program;
        this.MANAGER = manager;
        //noinspection OptionalGetWithoutIsPresent // program shouldn't be ticking if the network is bad
        NETWORK = CableNetworkManager
                .getOrRegisterNetworkFromManagerPosition(MANAGER)
                .get();
        assert MANAGER.getLevel() != null;
        LEVEL = MANAGER.getLevel();
        REDSTONE_PULSES = MANAGER.getUnprocessedRedstonePulseCount();
        EXECUTION_POLICY = executionPolicy;
        EXPLORATION_BRANCH_INDEX = branchIndex;
    }

    private ProgramContext(ProgramContext other) {
        PROGRAM = other.PROGRAM;
        MANAGER = other.MANAGER;
        NETWORK = other.NETWORK;
        LEVEL = other.LEVEL;
        REDSTONE_PULSES = other.REDSTONE_PULSES;
        EXECUTION_POLICY = other.EXECUTION_POLICY;
        EXPLORATION_BRANCH_INDEX = other.EXPLORATION_BRANCH_INDEX;
        INPUTS.addAll(other.INPUTS);
    }

    public ExecutionPolicy getExecutionPolicy() {
        return EXECUTION_POLICY;
    }

    public List<Branch> getExecutionPath() {
        return PATH_TAKEN;
    }

    public int getExplorationBranchIndex() {
        return EXPLORATION_BRANCH_INDEX;
    }

    public Program getProgram() {
        return PROGRAM;
    }

    public void pushPath(Branch branch) {
        this.PATH_TAKEN.add(branch);
    }

    public ProgramContext copy() {
        return new ProgramContext(this);
    }

    public int getRedstonePulses() {
        return REDSTONE_PULSES;
    }

    public enum ExecutionPolicy {
        EXPLORE_BRANCHES,
        UNRESTRICTED
    }

    public ManagerBlockEntity getManager() {
        return MANAGER;
    }

    public void addInput(InputStatement input) {
        INPUTS.add(input);
    }

    public List<InputStatement> getInputs() {
        return INPUTS;
    }


    public CableNetwork getNetwork() {
        return NETWORK;
    }

    public record Branch(
            IfStatement ifStatement,
            boolean wasTrue
    ) {
    }
}
