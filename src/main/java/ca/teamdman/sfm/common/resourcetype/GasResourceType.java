package ca.teamdman.sfm.common.resourcetype;

import mekanism.api.Action;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasHandler;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;

import static net.minecraftforge.common.capabilities.CapabilityManager.get;

public class GasResourceType extends ResourceType<GasStack, IGasHandler> {
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
    public GasStack insert(IGasHandler handler, int slot, GasStack gasStack, boolean simulate) {
        return handler.insertChemical(slot, gasStack, simulate ? Action.SIMULATE : Action.EXECUTE);
    }

    @Override
    public boolean isEmpty(GasStack gasStack) {
        return gasStack.isEmpty();
    }

    @Override
    public boolean matchesStackType(Object o) {
        return o instanceof GasStack;
    }

    @Override
    public boolean matchesCapType(Object o) {
        return o instanceof IGasHandler;
    }

    @Override
    public boolean registryKeyExists(ResourceLocation location) {
        return MekanismAPI.gasRegistry().containsKey(location);
    }

    @Override
    public ResourceLocation getRegistryKey(GasStack gasStack) {
        return MekanismAPI.gasRegistry().getKey(gasStack.getType());
    }
}
