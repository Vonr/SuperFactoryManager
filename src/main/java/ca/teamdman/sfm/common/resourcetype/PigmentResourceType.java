package ca.teamdman.sfm.common.resourcetype;

import mekanism.api.Action;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.pigment.IPigmentHandler;
import mekanism.api.chemical.pigment.PigmentStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;

import static net.minecraftforge.common.capabilities.CapabilityManager.get;

public class PigmentResourceType extends ResourceType<PigmentStack, IPigmentHandler> {
    public static final Capability<IPigmentHandler> CAP = get(new CapabilityToken<>() {
    });

    public PigmentResourceType() {
        super(CAP);
    }

    @Override
    public long getCount(PigmentStack stack) {
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
    public boolean matchesStackType(Object o) {
        return o instanceof PigmentStack;
    }

    @Override
    public boolean matchesCapType(Object o) {
        return o instanceof IPigmentHandler;
    }

    @Override
    public boolean registryKeyExists(ResourceLocation location) {
        return MekanismAPI.pigmentRegistry().containsKey(location);
    }

    @Override
    public ResourceLocation getRegistryKey(PigmentStack stack) {
        return MekanismAPI.pigmentRegistry().getKey(stack.getType());
    }
}
