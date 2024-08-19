package ca.teamdman.sfm.common.resourcetype;

import mekanism.api.Action;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.infuse.IInfusionHandler;
import mekanism.api.chemical.infuse.InfuseType;
import mekanism.api.chemical.infuse.InfusionStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.stream.Stream;

import static net.minecraftforge.common.capabilities.CapabilityManager.get;

public class InfuseResourceType extends ResourceType<InfusionStack, InfuseType, IInfusionHandler> {
    public static final Capability<IInfusionHandler> CAP = get(new CapabilityToken<>() {
    });

    public InfuseResourceType() {
        super(CAP);
    }

    @Override
    public long getAmount(InfusionStack stack) {
        return stack.getAmount();
    }

    @Override
    public InfusionStack getStackInSlot(
            IInfusionHandler handler,
            int slot
    ) {
        return handler.getChemicalInTank(slot);
    }

    @Override
    public InfusionStack extract(
            IInfusionHandler handler,
            int slot,
            long amount,
            boolean simulate
    ) {
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
    public long getMaxStackSize(
            IInfusionHandler handler,
            int slot
    ) {
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
    public Stream<ResourceLocation> getTagsForStack(InfusionStack infusionStack) {
        return infusionStack.getType().getTags().map(TagKey::location);
    }


    @Override
    public IForgeRegistry<InfuseType> getRegistry() {
        return MekanismAPI.infuseTypeRegistry();
    }

    @Override
    public InfuseType getItem(InfusionStack stack) {
        return stack.getType();
    }

    @Override
    public InfusionStack copy(InfusionStack stack) {
        return stack.copy();
    }

    @Override
    protected InfusionStack setCount(
            InfusionStack stack,
            long amount
    ) {
        stack.setAmount(amount);
        return stack;
    }
}
