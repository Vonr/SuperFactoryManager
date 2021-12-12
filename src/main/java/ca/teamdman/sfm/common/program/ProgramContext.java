package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.util.SFMLabelNBTHelper;
import ca.teamdman.sfml.ast.DirectionQualifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class ProgramContext {
    private final ManagerBlockEntity     MANAGER;
    private final CableNetwork           NETWORK;
    private final List<InventoryTracker> INPUTS = new ArrayList<>();
    private final Level                  LEVEL;

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

    public void addInput(InventoryTracker input) {
        INPUTS.add(input);
    }

    public Stream<InventoryTracker> getInputs() {
        return INPUTS.stream();
    }

    public Stream<BlockEntity> getBlockEntitiesByLabel(String label) {
        var disk = MANAGER
                .getDisk()
                .get();
        var positions = SFMLabelNBTHelper.getLabelPositions(disk, label);
        return positions
                .map(NETWORK::getInventory)
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    public Stream<LazyOptional<IItemHandler>> getItemHandlersByLabel(String label, DirectionQualifier dir) {
        if (dir.directions().isEmpty()) {
            return getBlockEntitiesByLabel(label)
                    .filter(CapabilityProvider.class::isInstance)
                    .map(CapabilityProvider.class::cast)
                    .map(c -> c.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY));
        } else {
            return getBlockEntitiesByLabel(label)
                    .filter(CapabilityProvider.class::isInstance)
                    .map(CapabilityProvider.class::cast)
                    .mapMulti((prov, accum) -> dir
                            .directions()
                            .forEach(d -> accum.accept(prov.getCapability(
                                    CapabilityItemHandler.ITEM_HANDLER_CAPABILITY,
                                    d
                            ))));
        }
    }
}
