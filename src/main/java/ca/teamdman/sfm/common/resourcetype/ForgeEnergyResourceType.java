package ca.teamdman.sfm.common.resourcetype;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.tags.ITagManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ForgeEnergyResourceType extends ResourceType<Integer, Class<Integer>, IEnergyStorage> {
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


    public static final ResourceLocation REGISTRY_KEY = new ResourceLocation("forge", "energy");

    @Override
    public ResourceLocation getRegistryKey(Integer integer) {
        return REGISTRY_KEY;
    }

    @Override
    public IForgeRegistry<Class<Integer>> getRegistry() {
        // ugly hack since there isn't a forge registry for energy?
        return new IForgeRegistry<>() {
            @Override
            public ResourceKey<Registry<Class<Integer>>> getRegistryKey() {
                return ResourceKey.createRegistryKey(REGISTRY_KEY);
            }

            @Override
            public ResourceLocation getRegistryName() {
                return null;
            }

            @Override
            public void register(String key, Class<Integer> value) {

            }

            @Override
            public void register(ResourceLocation key, Class<Integer> value) {

            }

            @Override
            public boolean containsKey(ResourceLocation key) {
                return key.equals(REGISTRY_KEY);
            }

            @Override
            public boolean containsValue(Class<Integer> value) {
                return value == Integer.class;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public @Nullable Class<Integer> getValue(ResourceLocation key) {
                return key.equals(REGISTRY_KEY) ? Integer.class : null;
            }

            @Override
            public @Nullable ResourceLocation getKey(Class<Integer> value) {
                return REGISTRY_KEY;
            }

            @Override
            public @Nullable ResourceLocation getDefaultKey() {
                return REGISTRY_KEY;
            }

            @Override
            public @NotNull Optional<ResourceKey<Class<Integer>>> getResourceKey(Class<Integer> value) {
                return Optional.of(ResourceKey.create(getRegistryKey(), REGISTRY_KEY));
            }

            @Override
            public @NotNull Set<ResourceLocation> getKeys() {
                return null;
            }

            @Override
            public @NotNull Collection<Class<Integer>> getValues() {
                return null;
            }

            @Override
            public @NotNull Set<Map.Entry<ResourceKey<Class<Integer>>, Class<Integer>>> getEntries() {
                return null;
            }

            @Override
            public @NotNull Codec<Class<Integer>> getCodec() {
                return null;
            }

            @Override
            public @NotNull Optional<Holder<Class<Integer>>> getHolder(ResourceKey<Class<Integer>> key) {
                return Optional.empty();
            }

            @Override
            public @NotNull Optional<Holder<Class<Integer>>> getHolder(ResourceLocation location) {
                return Optional.empty();
            }

            @Override
            public @NotNull Optional<Holder<Class<Integer>>> getHolder(Class<Integer> value) {
                return Optional.empty();
            }

            @Override
            public @Nullable ITagManager<Class<Integer>> tags() {
                return null;
            }

            @Override
            public @NotNull Optional<Holder.Reference<Class<Integer>>> getDelegate(ResourceKey<Class<Integer>> rkey) {
                return Optional.empty();
            }

            @Override
            public Holder.@NotNull Reference<Class<Integer>> getDelegateOrThrow(ResourceKey<Class<Integer>> rkey) {
                return null;
            }

            @Override
            public @NotNull Optional<Holder.Reference<Class<Integer>>> getDelegate(ResourceLocation key) {
                return Optional.empty();
            }

            @Override
            public Holder.@NotNull Reference<Class<Integer>> getDelegateOrThrow(ResourceLocation key) {
                return null;
            }

            @Override
            public @NotNull Optional<Holder.Reference<Class<Integer>>> getDelegate(Class<Integer> value) {
                return Optional.empty();
            }

            @Override
            public Holder.@NotNull Reference<Class<Integer>> getDelegateOrThrow(Class<Integer> value) {
                return null;
            }

            @Override
            public <T> T getSlaveMap(ResourceLocation slaveMapName, Class<T> type) {
                return null;
            }

            @NotNull
            @Override
            public Iterator<Class<Integer>> iterator() {
                return null;
            }
        };
    }


    @Override
    public Class<Integer> getItem(Integer integer) {
        return Integer.class;
    }
}
