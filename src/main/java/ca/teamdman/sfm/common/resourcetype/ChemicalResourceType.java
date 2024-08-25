package ca.teamdman.sfm.common.resourcetype;

import mekanism.api.Action;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import mekanism.common.capabilities.Capabilities;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class ChemicalResourceType extends ResourceType<ChemicalStack, Chemical, IChemicalHandler> {
    public static final BlockCapability<IChemicalHandler, @Nullable Direction> CAP = Capabilities.CHEMICAL.block();

    public ChemicalResourceType() {
        super(CAP);
    }

    @Override
    public long getAmount(ChemicalStack gasStack) {
        return gasStack.getAmount();
    }

    @Override
    public ChemicalStack getStackInSlot(
            IChemicalHandler iChemicalHandler,
            int slot
    ) {
        return iChemicalHandler.getChemicalInTank(slot);
    }

    @Override
    public Stream<ResourceLocation> getTagsForStack(ChemicalStack gasStack) {
        return gasStack.getChemical().getTags().map(TagKey::location);
    }

    @Override
    public ChemicalStack extract(
            IChemicalHandler handler,
            int slot,
            long amount,
            boolean simulate
    ) {
        return handler.extractChemical(slot, amount, simulate ? Action.SIMULATE : Action.EXECUTE);
    }

    @Override
    public int getSlots(IChemicalHandler handler) {
        return handler.getChemicalTanks();
    }

    @Override
    public long getMaxStackSize(ChemicalStack gasStack) {
        return Long.MAX_VALUE;
    }

    @Override
    public long getMaxStackSizeForSlot(
            IChemicalHandler handler,
            int slot
    ) {
        return handler.getChemicalTankCapacity(slot);
    }

    @Override
    public ChemicalStack insert(
            IChemicalHandler handler,
            int slot,
            ChemicalStack gasStack,
            boolean simulate
    ) {
        return handler.insertChemical(slot, gasStack, simulate ? Action.SIMULATE : Action.EXECUTE);
    }

    @Override
    public boolean isEmpty(ChemicalStack gasStack) {
        return gasStack.isEmpty();
    }

    @Override
    public ChemicalStack getEmptyStack() {
        return ChemicalStack.EMPTY;
    }

    @Override
    public boolean matchesStackType(Object o) {
        return o instanceof ChemicalStack;
    }

    @Override
    public boolean matchesCapabilityType(Object o) {
        return o instanceof IChemicalHandler;
    }


    @Override
    public Registry<Chemical> getRegistry() {
        return MekanismAPI.CHEMICAL_REGISTRY;
    }

    @Override
    public Chemical getItem(ChemicalStack gasStack) {
        return gasStack.getChemical();
    }

    @Override
    public ChemicalStack copy(ChemicalStack gasStack) {
        return gasStack.copy();
    }

    @Override
    protected ChemicalStack setCount(
            ChemicalStack gasStack,
            long amount
    ) {
        gasStack.setAmount(amount);
        return gasStack;
    }
}
