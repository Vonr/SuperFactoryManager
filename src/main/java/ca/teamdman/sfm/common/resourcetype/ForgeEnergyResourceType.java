package ca.teamdman.sfm.common.resourcetype;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

public class ForgeEnergyResourceType extends ResourceType<Integer, IEnergyStorage> {
    public ForgeEnergyResourceType() {
        super(ForgeCapabilities.ENERGY);
    }

    @Override
    public long getCount(Integer integer) {
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
    public boolean matchesCapType(Object o) {
        return o instanceof IEnergyStorage;
    }

    @Override
    public boolean registryKeyExists(ResourceLocation location) {
        return true;
    }

    @Override
    public ResourceLocation getRegistryKey(Integer integer) {
        return new ResourceLocation("forge", "energy");
    }
}
