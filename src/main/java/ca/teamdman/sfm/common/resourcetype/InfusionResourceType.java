package ca.teamdman.sfm.common.resourcetype;

import mekanism.api.Action;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.InfusionStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;

import static net.minecraftforge.common.capabilities.CapabilityManager.get;

public class InfusionResourceType extends ResourceType<InfusionStack, IInfusionHandler> {
    public static final Capability<IInfusionHandler> CAP = get(new CapabilityToken<>() {
    });

    public InfusionResourceType() {
        super(CAP);
    }

    @Override
    public long getCount(InfusionStack stack) {
        return stack.getAmount();
    }

    @Override
    public InfusionStack getStackInSlot(IInfusionHandler handler, int slot) {
        return handler.getChemicalInTank(slot);
    }

    @Override
    public InfusionStack extract(IInfusionHandler handler, int slot, long amount, boolean simulate) {
        return handler.extractChemical(slot, amount, simulate ? Action.SIMULATE : Action.EXECUTE);
    }

    @Override
    public int getSlots(IInfusionHandler handler) {
        return handler.getTanks();
    }

    @Override
    public InfusionStack insert(
            IInfusionHandler handler,
            int slot,
            InfusionStack stack,
            boolean simulate
    ) {
        return handler.insertChemical(slot, stack, simulate ? Action.SIMULATE : Action.EXECUTE);
    }

    @Override
    public boolean isEmpty(InfusionStack stack) {
        return stack.isEmpty();
    }

    @Override
    public boolean matchesStackType(Object o) {
        return o instanceof InfusionStack;
    }

    @Override
    public boolean matchesCapType(Object o) {
        return o instanceof IInfusionHandler;
    }

    @Override
    public boolean registryKeyExists(ResourceLocation location) {
        return MekanismAPI.infuseTypeRegistry().containsKey(location);
    }

    @Override
    public ResourceLocation getRegistryKey(InfusionStack infusionStack) {
        return MekanismAPI.infuseTypeRegistry().getKey(infusionStack.getType());
    }
}
