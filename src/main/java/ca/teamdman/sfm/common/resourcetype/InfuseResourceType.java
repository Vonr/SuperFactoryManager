package ca.teamdman.sfm.common.resourcetype;

import mekanism.api.Action;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

public class InfuseResourceType extends ResourceType<InfusionStack, InfuseType, IInfusionHandler> {
    public static final BlockCapability<IInfusionHandler, @Nullable Direction> CAP = Capabilities.INFUSION.block();

    public InfuseResourceType() {
        super(CAP);
    }

    @Override
    public long getAmount(InfusionStack stack) {
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
    public long getMaxStackSize(InfusionStack stack) {
        return Long.MAX_VALUE;
    }

    @Override
    public long getMaxStackSizeForSlot(IInfusionHandler handler, int slot) {
        return handler.getTankCapacity(slot);
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
    public InfusionStack getEmptyStack() {
        return InfusionStack.EMPTY;
    }

    @Override
    public boolean matchesStackType(Object o) {
        return o instanceof InfusionStack;
    }

    @Override
    public boolean matchesCapabilityType(Object o) {
        return o instanceof IInfusionHandler;
    }


    @Override
    public Registry<InfuseType> getRegistry() {
        return MekanismAPI.INFUSE_TYPE_REGISTRY;
    }

    @Override
    public InfuseType getItem(InfusionStack stack) {
        return stack.getChemical();
    }

    @Override
    public InfusionStack copy(InfusionStack stack) {
        return stack.copy();
    }

    @Override
    protected InfusionStack setCount(InfusionStack stack, long amount) {
        stack.setAmount(amount);
        return stack;
    }
}
