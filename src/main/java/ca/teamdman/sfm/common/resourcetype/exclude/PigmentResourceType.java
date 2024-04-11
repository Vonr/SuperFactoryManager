package ca.teamdman.sfm.common.resourcetype.exclude;

import ca.teamdman.sfm.common.resourcetype.ResourceType;
import mekanism.api.Action;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import net.minecraft.core.Registry;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.CapabilityManager;
import net.neoforged.neoforge.common.capabilities.CapabilityToken;
import org.apache.commons.lang3.NotImplementedException;

public class PigmentResourceType extends ResourceType<PigmentStack, Pigment, IPigmentHandler> {
    public static final Capability<IPigmentHandler> CAP = CapabilityManager.get(new CapabilityToken<>() {
    });

    public PigmentResourceType() {
        super(CAP);
    }

    @Override
    public long getAmount(PigmentStack stack) {
        return stack.getAmount();
    }

    @Override
    public PigmentStack getStackInSlot(IPigmentHandler handler, int slot) {
        return handler.getChemicalInTank(slot);
    }

    @Override
    public PigmentStack extract(IPigmentHandler handler, int slot, long amount, boolean simulate) {
        return handler.extractChemical(slot, amount, simulate ? Action.SIMULATE : Action.EXECUTE);
    }

    @Override
    public int getSlots(IPigmentHandler handler) {
        return handler.getTanks();
    }

    @Override
    public long getMaxStackSize(PigmentStack stack) {
        return Long.MAX_VALUE;
    }

    @Override
    public long getMaxStackSize(IPigmentHandler handler, int slot) {
        return handler.getTankCapacity(slot);
    }

    @Override
    public PigmentStack insert(
            IPigmentHandler handler,
            int slot,
            PigmentStack stack,
            boolean simulate
    ) {
        return handler.insertChemical(slot, stack, simulate ? Action.SIMULATE : Action.EXECUTE);
    }

    @Override
    public boolean isEmpty(PigmentStack stack) {
        return stack.isEmpty();
    }

    @Override
    public PigmentStack getEmptyStack() {
        return PigmentStack.EMPTY;
    }

    @Override
    public boolean matchesStackType(Object o) {
        return o instanceof PigmentStack;
    }

    @Override
    public boolean matchesCapabilityType(Object o) {
        return o instanceof IPigmentHandler;
    }


    @Override
    public Registry<Pigment> getRegistry() {
        throw new NotImplementedException();
//        return MekanismAPI.pigmentRegistry();
    }

    @Override
    public Pigment getItem(PigmentStack stack) {
        return stack.getType();
    }

    @Override
    public PigmentStack copy(PigmentStack stack) {
        return stack.copy();
    }

    @Override
    protected PigmentStack setCount(PigmentStack stack, long amount) {
        stack.setAmount(amount);
        return stack;
    }
}
