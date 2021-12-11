package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.util.SFMLabelNBTHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProgramContext {
    private final ManagerBlockEntity MANAGER;
    private final CableNetwork       NETWORK;
    private final List<ItemInput>    INPUTS = new ArrayList<>();
    private final Level              LEVEL;

    public ProgramContext(ManagerBlockEntity manager) {
        this.MANAGER = manager;
        NETWORK      = CableNetworkManager
                .getOrRegisterNetwork(MANAGER)
                .get();
        LEVEL        = MANAGER.getLevel();
    }

    public ManagerBlockEntity getManager() {
        return MANAGER;
    }

    public void addInput(ItemInput input) {
        INPUTS.add(input);
    }

    public List<ItemInput> getInputs() {
        return INPUTS;
    }

    public Stream<BlockEntity> getBlockEntitiesByLabel(String label) {
        var disk = MANAGER
                .getDisk()
                .get();
        var positions = SFMLabelNBTHelper.getPositions(disk, label);
        return positions
                .map(NETWORK::getInventory)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    public List<LazyOptional<IItemHandler>> getItemHandlersByLabel(String label) {
        return getBlockEntitiesByLabel(label)
                .filter(CapabilityProvider.class::isInstance)
                .map(CapabilityProvider.class::cast)
                .map(c -> c.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY))
                .collect(Collectors.toList());
    }
}
