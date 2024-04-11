package ca.teamdman.sfm.common.resourcetype.exclude;

import ca.teamdman.sfm.common.resourcetype.ResourceType;
import mekanism.api.Action;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.Gas;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import net.minecraft.core.Registry;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.CapabilityManager;
import net.neoforged.neoforge.common.capabilities.CapabilityToken;
import org.apache.commons.lang3.NotImplementedException;

public class GasResourceType extends ResourceType<GasStack, Gas, IGasHandler> {
    public static final Capability<IGasHandler> CAP = CapabilityManager.get(new CapabilityToken<>() {
    });

    public GasResourceType() {
        super(CAP);
    }

    @Override
    public long getAmount(GasStack gasStack) {
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
    public Registry<Gas> getRegistry() {
        throw new NotImplementedException();
//        return MekanismAPI.gasRegistry();
    }

    @Override
    public Gas getItem(GasStack gasStack) {
        return gasStack.getType();
    }

    @Override
    public GasStack copy(GasStack gasStack) {
        return gasStack.copy();
    }

    @Override
    protected GasStack setCount(GasStack gasStack, long amount) {
        gasStack.setAmount(amount);
        return gasStack;
    }
}
