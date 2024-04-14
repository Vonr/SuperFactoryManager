package ca.teamdman.sfm.common.resourcetype;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.apache.commons.lang3.NotImplementedException;

public class ForgeEnergyResourceType extends ResourceType<Integer, Class<Integer>, IEnergyStorage> {
    public ForgeEnergyResourceType() {
        super(Capabilities.EnergyStorage.BLOCK);
    }

    @Override
    public long getAmount(Integer integer) {
        return integer;
    }

    @Override
    public Integer getStackInSlot(IEnergyStorage iEnergyStorage, int slot) {
        return iEnergyStorage.getEnergyStored();
    }

    @Override
    public Integer extract(IEnergyStorage iEnergyStorage, int slot, long amount, boolean simulate) {
        int finalAmount = amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount;
        return iEnergyStorage.extractEnergy(finalAmount, simulate);
    }

    @Override
    public int getSlots(IEnergyStorage handler) {
        return 1;
    }

    @Override
    public long getMaxStackSize(Integer integer) {
        return Long.MAX_VALUE;
    }

    @Override
    public long getMaxStackSize(IEnergyStorage iEnergyStorage, int slot) {
        return iEnergyStorage.getMaxEnergyStored();
    }

    @Override
    public Integer insert(IEnergyStorage iEnergyStorage, int slot, Integer integer, boolean simulate) {
        return integer - iEnergyStorage.receiveEnergy(integer, simulate);
    }

    @Override
    public boolean isEmpty(Integer integer) {
        return integer == 0;
    }

    @Override
    public boolean matchesStackType(Object o) {
        return o instanceof Integer;
    }

    @Override
    public boolean matchesCapabilityType(Object o) {
        return o instanceof IEnergyStorage;
    }

    @Override
    public Integer getEmptyStack() {
        return 0;
    }

    public static final ResourceLocation REGISTRY_KEY = new ResourceLocation("forge", "energy");

    @Override
    public ResourceLocation getRegistryKey(Integer integer) {
        return REGISTRY_KEY;
    }

    @Override
    public Registry<Class<Integer>> getRegistry() {
        throw new NotImplementedException();
    }

    @Override
    public boolean registryKeyExists(ResourceLocation location) {
        return location.equals(REGISTRY_KEY);
    }

    @Override
    public Class<Integer> getItem(Integer integer) {
        return Integer.class;
    }

    @Override
    public Integer copy(Integer integer) {
        return integer;
    }

    @Override
    protected Integer setCount(Integer integer, long amount) {
        return amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount;
    }
}
