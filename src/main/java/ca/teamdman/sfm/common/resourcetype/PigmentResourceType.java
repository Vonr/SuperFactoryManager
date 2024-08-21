package ca.teamdman.sfm.common.resourcetype;

import mekanism.api.Action;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.Pigment;
import mekanism.api.chemical.pigment.PigmentStack;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

public class PigmentResourceType extends ResourceType<PigmentStack, Pigment, IPigmentHandler> {
    public static final BlockCapability<IPigmentHandler, @Nullable Direction> CAP = Capabilities.PIGMENT.block();

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
    public long getMaxStackSizeForSlot(IPigmentHandler handler, int slot) {
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
        return MekanismAPI.PIGMENT_REGISTRY;
    }

    @Override
    public Pigment getItem(PigmentStack stack) {
        return stack.getChemical();
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
