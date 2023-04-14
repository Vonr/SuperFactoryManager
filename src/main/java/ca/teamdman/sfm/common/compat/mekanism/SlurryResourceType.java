package ca.teamdman.sfm.common.compat.mekanism;

import ca.teamdman.sfm.common.resourcetype.ResourceType;
import mekanism.api.Action;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.registries.IForgeRegistry;

import static net.minecraftforge.common.capabilities.CapabilityManager.get;

public class SlurryResourceType extends ResourceType<SlurryStack, Slurry, ISlurryHandler> {
    public static final Capability<ISlurryHandler> CAP = get(new CapabilityToken<>() {
    });

    public SlurryResourceType() {
        super(CAP);
    }

    @Override
    public long getCount(SlurryStack stack) {
        return stack.getAmount();
    }

    @Override
    public SlurryStack getStackInSlot(ISlurryHandler handler, int slot) {
        return handler.getChemicalInTank(slot);
    }

    @Override
    public SlurryStack extract(ISlurryHandler handler, int slot, long amount, boolean simulate) {
        return handler.extractChemical(slot, amount, simulate ? Action.SIMULATE : Action.EXECUTE);
    }

    @Override
    public int getSlots(ISlurryHandler handler) {
        return handler.getTanks();
    }

    @Override
    public SlurryStack insert(
            ISlurryHandler handler,
            int slot,
            SlurryStack stack,
            boolean simulate
    ) {
        return handler.insertChemical(slot, stack, simulate ? Action.SIMULATE : Action.EXECUTE);
    }

    @Override
    public boolean isEmpty(SlurryStack stack) {
        return stack.isEmpty();
    }

    @Override
    public boolean matchesStackType(Object o) {
        return o instanceof SlurryStack;
    }

    @Override
    public boolean matchesCapabilityType(Object o) {
        return o instanceof ISlurryHandler;
    }


    @Override
    public IForgeRegistry<Slurry> getRegistry() {
        return MekanismAPI.slurryRegistry();
    }

    @Override
    public Slurry getItem(SlurryStack stack) {
        return stack.getType();
    }
}
