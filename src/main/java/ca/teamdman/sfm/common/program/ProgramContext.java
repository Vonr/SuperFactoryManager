package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfml.ast.InputStatement;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ProgramContext {
    private final ManagerBlockEntity   MANAGER;
    private final CableNetwork         NETWORK;
    private final List<InputStatement> INPUTS = new ArrayList<>();
    private final Level                LEVEL;

    private final int REDSTONE_PULSES;

    public ProgramContext(ManagerBlockEntity manager) {
        this.MANAGER    = manager;
        NETWORK         = CableNetworkManager
                .getOrRegisterNetwork(MANAGER)
                .get();
        LEVEL           = MANAGER.getLevel();
        REDSTONE_PULSES = MANAGER.getUnprocessedRedstonePulseCount();
    }

    public ProgramContext(ProgramContext other) {
        MANAGER         = other.MANAGER;
        NETWORK         = other.NETWORK;
        LEVEL           = other.LEVEL;
        REDSTONE_PULSES = other.REDSTONE_PULSES;
        INPUTS.addAll(other.INPUTS);
    }

    public int getRedstonePulses() {
        return REDSTONE_PULSES;
    }

    public ProgramContext fork() {
        return new ProgramContext(this);
    }

    public ManagerBlockEntity getManager() {
        return MANAGER;
    }

    public void addInput(InputStatement input) {
        INPUTS.add(input);
    }

    public Stream<InputStatement> getInputs() {
        return INPUTS.stream();
    }


    public CableNetwork getNetwork() {
        return NETWORK;
    }
}
