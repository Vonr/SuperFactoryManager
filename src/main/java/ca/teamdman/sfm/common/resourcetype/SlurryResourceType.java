package ca.teamdman.sfm.common.resourcetype;

import mekanism.api.Action;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.slurry.ISlurryHandler;
import mekanism.api.chemical.slurry.SlurryStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;

import static net.minecraftforge.common.capabilities.CapabilityManager.get;

public class SlurryResourceType extends ResourceType<SlurryStack, ISlurryHandler> {
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
    public boolean matchesCapType(Object o) {
        return o instanceof ISlurryHandler;
    }

    @Override
    public boolean registryKeyExists(ResourceLocation location) {
        return MekanismAPI.slurryRegistry().containsKey(location);
    }

    @Override
    public ResourceLocation getRegistryKey(SlurryStack stack) {
        return MekanismAPI.slurryRegistry().getKey(stack.getType());
    }
}
