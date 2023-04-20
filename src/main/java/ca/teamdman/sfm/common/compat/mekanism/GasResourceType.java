package ca.teamdman.sfm.common.compat.mekanism;

import ca.teamdman.sfm.common.resourcetype.ResourceType;
import mekanism.api.Action;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.registries.IForgeRegistry;

import static net.minecraftforge.common.capabilities.CapabilityManager.get;

public class GasResourceType extends ResourceType<GasStack, Gas, IGasHandler> {
    public static final Capability<IGasHandler> CAP = get(new CapabilityToken<>() {
    });

    public GasResourceType() {
        super(CAP);
    }

    @Override
    public long getCount(GasStack gasStack) {
        return gasStack.getAmount();
    }

    @Override
    public GasStack getStackInSlot(IGasHandler iGasHandler, int slot) {
        return iGasHandler.getChemicalInTank(slot);
    }

    @Override
    public GasStack extract(IGasHandler handler, int slot, long amount, boolean simulate) {
        return handler.extractChemical(slot, amount, simulate ? Action.SIMULATE : Action.EXECUTE);
    }

    @Override
    public int getSlots(IGasHandler handler) {
        return handler.getTanks();
    }

    @Override
    public long getMaxStackSize(GasStack gasStack) {
        return Long.MAX_VALUE;
    }

    @Override
    public long getMaxStackSize(IGasHandler handler, int slot) {
        return handler.getTankCapacity(slot);
    }

    @Override
    public GasStack insert(IGasHandler handler, int slot, GasStack gasStack, boolean simulate) {
        return handler.insertChemical(slot, gasStack, simulate ? Action.SIMULATE : Action.EXECUTE);
    }

    @Override
    public boolean isEmpty(GasStack gasStack) {
        return gasStack.isEmpty();
    }

    @Override
    public GasStack getEmptyStack() {
        return GasStack.EMPTY;
    }

    @Override
    public boolean matchesStackType(Object o) {
        return o instanceof GasStack;
    }

    @Override
    public boolean matchesCapabilityType(Object o) {
        return o instanceof IGasHandler;
    }


    @Override
    public IForgeRegistry<Gas> getRegistry() {
        return MekanismAPI.gasRegistry();
    }

    @Override
    public Gas getItem(GasStack gasStack) {
        return gasStack.getType();
    }
}
