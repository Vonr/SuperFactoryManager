package ca.teamdman.sfm.common.resourcetype;

import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public class FluidResourceType extends ResourceType<FluidStack, Fluid, IFluidHandler> {
    public FluidResourceType() {
        super(ForgeCapabilities.FLUID_HANDLER);
    }

    @Override
    public IForgeRegistry<Fluid> getRegistry() {
        return ForgeRegistries.FLUIDS;
    }

    @Override
    public Fluid getItem(FluidStack fluidStack) {
        return fluidStack.getFluid();
    }

    @Override
    public long getCount(FluidStack stack) {
        return stack.getAmount();
    }

    @Override
    public FluidStack getStackInSlot(IFluidHandler cap, int slot) {
        return cap.getFluidInTank(slot);
    }

    @Override
    public FluidStack extract(IFluidHandler handler, int slot, long amount, boolean simulate) {
        var in          = getStackInSlot(handler, slot);
        int finalAmount = amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount;
        var toExtract   = new FluidStack(in.getFluid(), Math.min(in.getAmount(), finalAmount));
        return handler.drain(
                toExtract,
                simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE
        );
    }

    @Override
    public boolean matchesStackType(Object o) {
        return o instanceof FluidStack;
    }

    @Override
    public boolean matchesCapabilityType(Object o) {
        return o instanceof IFluidHandler;
    }

    @Override
    public int getSlots(IFluidHandler handler) {
        return handler.getTanks();
    }

    @Override
    public long getMaxStackSize(FluidStack fluidStack) {
        return Integer.MAX_VALUE;
    }

    @Override
    public FluidStack insert(IFluidHandler handler, int slot, FluidStack stack, boolean simulate) {
        //todo: PR to forge to add a method that takes tank slot index
        var x = handler.fill(stack, simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE);

        // convert units to find amount NOT inserted
        var rtn = new FluidStack(stack.getFluid(), stack.getAmount() - x);
        return rtn;
    }

    @Override
    public boolean isEmpty(FluidStack stack) {
        return stack.isEmpty();
    }

    @Override
    public FluidStack getEmptyStack() {
        return FluidStack.EMPTY;
    }
}
