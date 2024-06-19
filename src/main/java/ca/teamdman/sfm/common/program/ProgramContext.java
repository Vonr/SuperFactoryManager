package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.logging.TranslatableLogger;
import ca.teamdman.sfml.ast.IfStatement;
import ca.teamdman.sfml.ast.InputStatement;
import ca.teamdman.sfml.ast.Program;
import net.minecraft.world.item.ItemStack;
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
    private final ItemStack DISK_STACK;
    private final LabelPositionHolder LABEL_POSITIONS;
    private boolean did_something = false;

    public boolean didSomething() {
        return did_something;
    }

    public void setDidSomething(boolean value) {
        this.did_something = value;
    }

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
        //noinspection OptionalGetWithoutIsPresent // program shouldn't be ticking if there is no disk
        DISK_STACK = MANAGER.getDisk().get();
        LABEL_POSITIONS = LabelPositionHolder.from(DISK_STACK);
    }

    public ItemStack getDisk() {
        return DISK_STACK;
    }

    public LabelPositionHolder getlabelPositions() {
        return LABEL_POSITIONS;
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
        did_something = other.did_something;
        DISK_STACK = other.DISK_STACK;
        LABEL_POSITIONS = other.LABEL_POSITIONS;
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

    /**
     * We free in reverse order because the {@link InputStatement#inputCheck} needs LIFO ordering for the math to work
     */
    public void free() {
        for (int i = INPUTS.size() - 1; i >= 0; i--) {
            INPUTS.get(i).freeSlots();
        }
    }

    public enum ExecutionPolicy {
        EXPLORE_BRANCHES,
        UNRESTRICTED
    }

    public ManagerBlockEntity getManager() {
        return MANAGER;
    }

    public TranslatableLogger getLogger() {
        return MANAGER.logger;
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

    @Override
    public String toString() {
        return "ProgramContext{" +
               "PROGRAM=" + PROGRAM +
               ", MANAGER=" + MANAGER +
               ", NETWORK=" + NETWORK +
               ", INPUTS=" + INPUTS +
               ", LEVEL=" + LEVEL +
               ", EXECUTION_POLICY=" + EXECUTION_POLICY +
               ", PATH_TAKEN=" + PATH_TAKEN +
               ", EXPLORATION_BRANCH_INDEX=" + EXPLORATION_BRANCH_INDEX +
               ", REDSTONE_PULSES=" + REDSTONE_PULSES +
               ", DISK_STACK=" + DISK_STACK +
               ", LABEL_POSITIONS=" + LABEL_POSITIONS +
               ", did_something=" + did_something +
               '}';
    }
}
