package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.resourcetype.FluidResourceType;
import ca.teamdman.sfm.common.resourcetype.ForgeEnergyResourceType;
import ca.teamdman.sfm.common.resourcetype.ItemResourceType;
import ca.teamdman.sfm.common.resourcetype.ResourceType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class SFMResourceTypes {
    public static final ResourceLocation REGISTRY_ID = new ResourceLocation(SFM.MOD_ID, "resource_type");

    private static final DeferredRegister<ResourceType<?, ?, ?>> TYPES = DeferredRegister.create(
            REGISTRY_ID,
            SFM.MOD_ID
    );
    public static final Supplier<IForgeRegistry<ResourceType<?, ?, ?>>> DEFERRED_TYPES = TYPES.makeRegistry(
            () -> new RegistryBuilder<ResourceType<?, ?, ?>>().setName(
                    REGISTRY_ID));
    public static final RegistryObject<ResourceType<ItemStack, Item, IItemHandler>> ITEM = TYPES.register(
            "item",
            ItemResourceType::new
    );
    public static final RegistryObject<ResourceType<FluidStack, Fluid, IFluidHandler>> FLUID = TYPES.register(
            "fluid",
            FluidResourceType::new
    );
    public static final RegistryObject<ResourceType<Integer, Class<Integer>, IEnergyStorage>> FORGE_ENERGY = TYPES.register(
            "forge_energy",
            ForgeEnergyResourceType::new
    );

    public static void register(IEventBus bus) {
        TYPES.register(bus);
    }
}
